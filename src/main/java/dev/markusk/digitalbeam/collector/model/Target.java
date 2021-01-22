package dev.markusk.digitalbeam.collector.model;

import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.UUID;

@Data
public class Target implements Snowflake {

  private UUID snowflake;
  private String name;
  private String shortname;
  private boolean tor;

  @BsonProperty(value = "fetch_url")
  private String fetchUrl;

  @BsonProperty(value = "wait_time")
  private int waitTime;

  @BsonProperty(value = "date_pattern")
  private String datePattern;

  @BsonProperty(value = "last_url")
  private String lastUrl;

  private boolean active;

  @Override
  public String toString() {
    return "Target{" +
        "snowflake=" + snowflake +
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
