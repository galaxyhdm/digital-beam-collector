package dev.markusk.digitalbeam.collector.model;

import java.util.Date;

public class Version implements Snowflake {

  private final String snowflake;
  private final String articleSnowflake;
  private final String versionId;
  private final int version;
  private final Date updateTime;
  private final String autoOffset;

  public Version(final String snowflake, final String articleSnowflake, final String versionId, final int version,
      final Date updateTime, final String autoOffset) {
    this.snowflake = snowflake;
    this.articleSnowflake = articleSnowflake;
    this.versionId = versionId;
    this.version = version;
    this.updateTime = updateTime;
    this.autoOffset = autoOffset;
  }

  public String getArticleSnowflake() {
    return this.articleSnowflake;
  }

  public String getVersionId() {
    return this.versionId;
  }

  public int getVersion() {
    return this.version;
  }

  public Date getUpdateTime() {
    return this.updateTime;
  }

  public String getAutoOffset() {
    return this.autoOffset;
  }

  @Override
  public String getSnowflakeId() {
    return this.snowflake;
  }

  @Override
  public String toString() {
    return "Version{" +
        "snowflake='" + snowflake + '\'' +
        ", articleSnowflake='" + articleSnowflake + '\'' +
        ", versionId='" + versionId + '\'' +
        ", version=" + version +
        ", updateTime=" + updateTime +
        ", autoOffset='" + autoOffset + '\'' +
        '}';
  }
}
