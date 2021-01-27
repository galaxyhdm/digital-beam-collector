package dev.markusk.digitalbeam.collector.model;

import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.util.Date;

@Data
public class Version {

  @BsonProperty(value = "version_id")
  private ObjectId objectId;

  @BsonProperty
  private int version;

  @BsonProperty(value = "update_time")
  private Date updateTime;

  @BsonProperty(value = "auto_offset")
  private String autoOffset;

  @Override
  public String toString() {
    return "Version{" +
        "objectId='" + objectId + '\'' +
        ", version=" + version +
        ", updateTime=" + updateTime +
        ", autoOffset='" + autoOffset + '\'' +
        '}';
  }
}
