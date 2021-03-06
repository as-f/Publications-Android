package edu.grinnell.grinnell_publications_android.Models.Realm;

import java.util.List;

import edu.grinnell.grinnell_publications_android.Models.Interfaces.Author;
import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * This class implements the @code{Author} interface using the Realm persistence.
 *
 * @author Albert Owusu-Asare
 * @version 1.1 Fri May  6 02:39:50 CDT 2016
 * @see RealmObject
 * @see Author
 */
public class RealmAuthor extends RealmObject implements Author {
  private String fullName;
  private RealmAuthorContact authorContactInfo;
  private RealmList<RealmPublication> publications;
  private RealmList<RealmStory> stories;

  /* Default constructor needed for Realm*/
  public RealmAuthor() {
  }

  public RealmAuthor(String fullName, RealmAuthorContact authorContactInfo,
      RealmList<RealmPublication> publications, RealmList<RealmStory> stories) {
    this.fullName = fullName;
    this.authorContactInfo = authorContactInfo;
    this.publications = publications;
    this.stories = stories;
  }

  /** Setters */
  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public void setAuthorContactInfo(RealmAuthorContact authorContact) {
    this.authorContactInfo = authorContact;
  }

  public void setPublications(RealmList<RealmPublication> publications) {
    this.publications = publications;
  }

  public void setStories(RealmList<RealmStory> stories) {
    this.stories = stories;
  }

  /** Getters */
  @Override public String getFullName() {
    return this.fullName;
  }

  @Override public RealmAuthorContact getAuthorContactInfo() {
    return this.authorContactInfo;
  }

  @Override public RealmList<RealmPublication> getPublications() {
    return this.publications;
  }

  @Override public RealmList<RealmStory> getStories() {
    return this.stories;
  }
}
