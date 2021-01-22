package dev.markusk.digitalbeam.collector.model;

import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Date;
import java.util.UUID;

@Data
public class Version implements Snowflake {

  @BsonProperty(value = "version_snowflake")
  private UUID snowflake;

  private int version;

  @BsonProperty(value = "update_time")
  private Date updateTime;

  @BsonProperty(value = "auto_offset")
  private String autoOffset;

  @Override
  public String toString() {
    return "Version{" +
        "snowflake='" + snowflake + '\'' +
        ", version=" + version +
        ", updateTime=" + updateTime +
        ", autoOffset='" + autoOffset + '\'' +
        '}';
  }
}
