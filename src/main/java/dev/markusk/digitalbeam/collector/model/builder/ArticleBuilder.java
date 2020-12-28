package dev.markusk.digitalbeam.collector.model.builder;

import dev.markusk.digitalbeam.collector.model.Article;
import dev.markusk.digitalbeam.collector.model.Version;

import java.util.Date;
import java.util.List;

public class ArticleBuilder {
  private String snowflake;
  private String articleId;
  private String targetSnowflake;
  private String title;
  private String url;
  private Date releaseTime;
  private Date fetchTime;
  private List<Version> versions;

  public ArticleBuilder setSnowflake(final String snowflake) {
    this.snowflake = snowflake;
    return this;
  }

  public ArticleBuilder setArticleId(final String articleId) {
    this.articleId = articleId;
    return this;
  }

  public ArticleBuilder setTargetSnowflake(final String targetSnowflake) {
    this.targetSnowflake = targetSnowflake;
    return this;
  }

  public ArticleBuilder setTitle(final String title) {
    this.title = title;
    return this;
  }

  public ArticleBuilder setUrl(final String url) {
    this.url = url;
    return this;
  }

  public ArticleBuilder setReleaseTime(final Date releaseTime) {
    this.releaseTime = releaseTime;
    return this;
  }

  public ArticleBuilder setFetchTime(final Date fetchTime) {
    this.fetchTime = fetchTime;
    return this;
  }

  public ArticleBuilder setVersions(final List<Version> versions) {
    this.versions = versions;
    return this;
  }

  public Article createArticle() {
    return new Article(snowflake, articleId, targetSnowflake, title, url, releaseTime, fetchTime, versions);
  }
}