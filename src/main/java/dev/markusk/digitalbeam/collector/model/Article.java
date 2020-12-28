package dev.markusk.digitalbeam.collector.model;

import java.util.Date;
import java.util.List;

public class Article implements Snowflake {

  private final String snowflake;
  private final String articleId;
  private final String targetSnowflake;
  private final String title;
  private final String url;
  private final Date releaseTime;
  private final Date fetchTime;

  private final List<Version> versions;

  public Article(final String snowflake, final String articleId, final String targetSnowflake, final String title,
      final String url, final Date releaseTime, final Date fetchTime, final List<Version> versions) {
    this.snowflake = snowflake;
    this.articleId = articleId;
    this.targetSnowflake = targetSnowflake;
    this.title = title;
    this.url = url;
    this.releaseTime = releaseTime;
    this.fetchTime = fetchTime;
    this.versions = versions;
  }

  public String getArticleId() {
    return this.articleId;
  }

  public String getTargetSnowflake() {
    return this.targetSnowflake;
  }

  public String getTitle() {
    return this.title;
  }

  public String getUrl() {
    return this.url;
  }

  public Date getReleaseTime() {
    return this.releaseTime;
  }

  public Date getFetchTime() {
    return this.fetchTime;
  }

  public List<Version> getVersions() {
    return this.versions;
  }

  @Override
  public String getSnowflakeId() {
    return this.snowflake;
  }

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
