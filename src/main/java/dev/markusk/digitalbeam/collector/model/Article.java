package dev.markusk.digitalbeam.collector.model;

import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

@Data
public class Article {

  @BsonProperty(value = "_id")
  private ObjectId objectId;

  @BsonProperty(value = "article_id")
  private String articleId;

  @BsonProperty(value = "target_id")
  private ObjectId targetObjectId;

  @BsonProperty
  private String title;

  @BsonProperty
  private String url;

  @BsonProperty(value = "release_time")
  private Date releaseTime;

  @BsonProperty(value = "fetch_time")
  private Date fetchTime;

  @BsonProperty
  private List<Version> versions;

  @BsonProperty(value = "queued_lookups")
  private List<Date> queuedLookups;

  @Override
  public String toString() {
    return "Article{" +
        "objectId='" + objectId + '\'' +
        ", articleId='" + articleId + '\'' +
        ", targetObjectId='" + targetObjectId + '\'' +
        ", title='" + title + '\'' +
        ", url='" + url + '\'' +
        ", releaseTime=" + releaseTime +
        ", fetchTime=" + fetchTime +
        ", versions=" + versions +
        ", queuedLookups=" + queuedLookups +
        '}';
  }
}
