package dev.markusk.digitalbeam.collector.model;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class Version implements Snowflake {

  private UUID snowflake;
  private String articleSnowflake;
  private String versionId;
  private int version;
  private Date updateTime;
  private String autoOffset;

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
