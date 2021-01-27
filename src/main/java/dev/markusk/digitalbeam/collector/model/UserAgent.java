package dev.markusk.digitalbeam.collector.model;

import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

@Data
public class UserAgent {

  @BsonProperty(value = "_id")
  private ObjectId objectId;

  @BsonProperty(value = "user_agent")
  private String userAgent;

  @Override
  public String toString() {
    return "UserAgent{" +
        "objectId='" + objectId + '\'' +
        ", userAgent='" + userAgent + '\'' +
        '}';
  }
}
