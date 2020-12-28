package dev.markusk.digitalbeam.collector.model.builder;

import dev.markusk.digitalbeam.collector.model.Version;

import java.util.Date;

public class VersionBuilder {
  private String snowflake;
  private String articleSnowflake;
  private String versionId;
  private int version;
  private Date updateTime;
  private String autoOffset;

  public VersionBuilder setSnowflake(final String snowflake) {
    this.snowflake = snowflake;
    return this;
  }

  public VersionBuilder setArticleSnowflake(final String articleSnowflake) {
    this.articleSnowflake = articleSnowflake;
    return this;
  }

  public VersionBuilder setVersionId(final String versionId) {
    this.versionId = versionId;
    return this;
  }

  public VersionBuilder setVersion(final int version) {
    this.version = version;
    return this;
  }

  public VersionBuilder setUpdateTime(final Date updateTime) {
    this.updateTime = updateTime;
    return this;
  }

  public VersionBuilder setAutoOffset(final String autoOffset) {
    this.autoOffset = autoOffset;
    return this;
  }

  public Version createVersion() {
    return new Version(snowflake, articleSnowflake, versionId, version, updateTime, autoOffset);
  }
}