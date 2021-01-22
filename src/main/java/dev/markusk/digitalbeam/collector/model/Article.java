package dev.markusk.digitalbeam.collector.model;

import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class Article implements Snowflake {

  private UUID snowflake;

  @BsonProperty(value = "article_id")
  private String articleId;

  @BsonProperty(value = "target_snowflake")
  private UUID targetSnowflake;

  private String title;
  private String url;

  @BsonProperty(value = "release_time")
  private Date releaseTime;

  @BsonProperty(value = "fetch_time")
  private Date fetchTime;

  private List<Version> versions;

  @Override
  public String toString() {
    return "Article{" +
        "snowflake='" + snowflake + '\'' +
        ", articleId='" + articleId + '\'' +
        ", targetSnowflake='" + targetSnowflake + '\'' +
        ", title='" + title + '\'' +
        ", url='" + url + '\'' +
        ", releaseTime=" + releaseTime +
        ", fetchTime=" + fetchTime +
        ", versions=" + versions +
        '}';
  }
}
