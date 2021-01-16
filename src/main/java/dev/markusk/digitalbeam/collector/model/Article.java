package dev.markusk.digitalbeam.collector.model;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class Article implements Snowflake {

  private UUID snowflake;
  private String articleId;
  private UUID targetSnowflake;
  private String title;
  private String url;
  private Date releaseTime;
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
