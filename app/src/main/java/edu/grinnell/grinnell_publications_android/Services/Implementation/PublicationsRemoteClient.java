package edu.grinnell.grinnell_publications_android.Services.Implementation;

import android.widget.Toast;
import edu.grinnell.grinnell_publications_android.Models.Interfaces.Publication;
import edu.grinnell.grinnell_publications_android.Models.Realm.RealmAuthor;
import edu.grinnell.grinnell_publications_android.Models.Realm.RealmAuthorContact;
import edu.grinnell.grinnell_publications_android.Models.Realm.RealmPublication;
import edu.grinnell.grinnell_publications_android.Models.Realm.RealmStory;
import edu.grinnell.grinnell_publications_android.Models.RemoteQueryResponse;
import edu.grinnell.grinnell_publications_android.Services.Interfaces.LocalClientAPI;
import edu.grinnell.grinnell_publications_android.Services.Interfaces.RemoteClientAPI;
import edu.grinnell.grinnell_publications_android.Services.POJO.JAuthor;
import edu.grinnell.grinnell_publications_android.Services.POJO.JPublication;
import edu.grinnell.grinnell_publications_android.Services.POJO.JStory;
import io.realm.Realm;
import io.realm.RealmList;
import java.util.List;
import java.util.Set;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;


/*
https://github.com/GrinnellAppDev/Publications-Android/pull/21
 */


 */
/**
 * Remote client that connects to remote end points through networking calls.
 *
 * <p> All networking calls are made through calling on a mRetrofit networking service.
 * As an observable, updates to the remote calls are routed to the respective observers for either
 * changes to the UI or changes to the local data base.</p>
 *
 * @author Albert Owusu-Asare
 * @version 1.1 Thu May  5 15:57:45 CDT 2016
 */
public class PublicationsRemoteClient implements RemoteClientAPI {

  private Realm mRealm;
  private Retrofit mRetrofit;
  private LocalClientAPI mLocalClient;
  private AWSEndpointApiInterface mApi;
  private final String BASE_API =
      "https://g2j7qs2xs7.execute-mApi.us-west-2.amazonaws.com/devstable/";

  public PublicationsRemoteClient() {
    this(new RealmLocalClient());
  }

  public PublicationsRemoteClient(LocalClientAPI localClient) {
    this.mLocalClient = localClient;

    mRetrofit = new Retrofit.Builder().baseUrl(BASE_API)
        .addConverterFactory(GsonConverterFactory.create())
        .build();
    mApi = mRetrofit.create(AWSEndpointApiInterface.class);
  }

  private void instantiateRealm() {
    mRealm = Realm.getDefaultInstance();
    mRealm.beginTransaction();
  }

  @Override public void getAllPublicationsFromAWS() {
    instantiateRealm();
    Call<List<JPublication>> call = mApi.publications();
    call.enqueue(new Callback<List<JPublication>>() {
      @Override
      public void onResponse(Call<List<JPublication>> call, Response<List<JPublication>> response) {
        storeRealmPublication(response.body());
      }
      public void onFailure(Call<List<JPublication>> call, Throwable t) {
        Toast.makeText(getActivity(), "Failed to get all publication!", Toast.LENGTH_LONG).show();
      }
    });
  }

  public void getStoryFromAWS(String publicationId, String articleId) {
    instantiateRealm();
    Call<JStory> call = mApi.article(publicationId, articleId);
    call.enqueue(new Callback<JStory>() {
      @Override public void onResponse(Call<JStory> call, Response<JStory> response) {
        storeRealmStory(convertToRealmStory(response.body()));
      }
      public void onFailure(Call<JStory> call, Throwable t) {
        Toast.makeText(getActivity(), "Failed to get story!", Toast.LENGTH_LONG).show();
      }
    });
  }

    /* Private helper methods */
  private RealmStory convertToRealmStory(JStory story) {
    RealmList realmAuthorList = new RealmList<RealmAuthor>();
    for (JAuthor author : story.getAuthors()) {
      RealmAuthorContact contact = new RealmAuthorContact(author.getEmail(), null, null);
      realmAuthorList.add(new RealmAuthor(author.getName(), contact, null, null));
    }
    RealmStory realmStory =
        new RealmStory(null, story.getDatePublished(), story.getDateEdited(), realmAuthorList, null,
            story.getContent(), story.getBrief(), story.getTitle(), story.getHeaderImage());
    return realmStory;
  }

  // TODO: store mRealm story into mRealm objects, will be implemented after pulling from update
  // Update mRealm publication with story
  private void storeRealmStory(RealmStory realmStory) { }

  private void storeRealmPublication(List<JPublication> list) {
    for (JPublication item : list) {
      RealmPublication pub = new RealmPublication(item.getName(), item.getId(), null, null, null);
      mRealm.copyToRealm(pub);
      mRealm.commitTransaction();
    }
  }

  /* Not currently supported by AWS endpoints*/
  @Override public void getPublications(Set<Integer> publicationIds) { }
  public void getPublication(String id) { }

  // TODO : Add logging to the HTTP calls.
  // TODO : DRY out service calls.? Repetition
    /* Private classes  and interfaces */
  private interface AWSEndpointApiInterface {
    /* Publications */
    @GET("publications/") Call<List<JPublication>> publications();

    /* Articles */
    @GET("publications/{publicationId}/articles/{articleId}") Call<JStory> article(
        @Path("publicationId") String publicationId, @Path("articleId") String articleId);

    /* Not currently supported by AWS endpoints*/
    /* Publications by id */
    @GET("publications/{id}") Call<Publication> publication(@Path("id") String publicationId);

    /* Images */
    @GET("images/{id}") Call<RemoteQueryResponse> image(@Path("id") int imageId);

    /* Series */
    @GET("series/{id}") Call<RemoteQueryResponse> series(@Path("id") int seriesId);
  }
}
