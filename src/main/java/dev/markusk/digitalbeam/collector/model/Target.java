package dev.markusk.digitalbeam.collector.model;

import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

@Data
public class Target {

  @BsonProperty(value = "_id")
  private ObjectId objectId;

  @BsonProperty
  private String name;

  @BsonProperty
  private String shortname;

  @BsonProperty
  private boolean tor;

  @BsonProperty(value = "fetch_url")
  private String fetchUrl;

  @BsonProperty(value = "wait_time")
  private int waitTime;

  @BsonProperty(value = "date_pattern")
  private String datePattern;

  @BsonProperty(value = "last_url")
  private String lastUrl;

  @BsonProperty(value = "fetcher_class_path")
  private String fetcherClassPath;

  @BsonProperty
  private boolean active;

  @Override
  public String toString() {
    return "Target{" +
        "objectId=" + objectId +
        ", name='" + name + '\'' +
        ", shortname='" + shortname + '\'' +
        ", fetchUrl='" + fetchUrl + '\'' +
        ", tor=" + tor +
        ", waitTime=" + waitTime +
        ", datePattern='" + datePattern + '\'' +
        ", lastUrl='" + lastUrl + '\'' +
        ", active=" + active +
        '}';
  }
}
