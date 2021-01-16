package dev.markusk.digitalbeam.collector.model;

import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.UUID;

@Data
public class UserAgent implements Snowflake {

  private UUID snowflake;

  @BsonProperty(value = "user_agent")
  private String userAgent;

  @Override
  public String toString() {
    return "UserAgent{" +
        "snowflake='" + snowflake + '\'' +
        ", userAgent='" + userAgent + '\'' +
        '}';
  }
}
