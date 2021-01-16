package dev.markusk.digitalbeam.collector.model;

import lombok.Data;

import java.util.UUID;

@Data
public class Target implements Snowflake {

  private UUID snowflake;
  private String name;
  private String shortname;
  private String fetchUrl;
  private boolean tor;
  private int waitTime;
  private String datePattern;
  private boolean active;

  @Override
  public String toString() {
    return "Target{" +
        "snowflake='" + snowflake + '\'' +
        ", name='" + name + '\'' +
        ", shortname='" + shortname + '\'' +
        ", fetchUrl='" + fetchUrl + '\'' +
        ", tor=" + tor +
        ", waitTime=" + waitTime +
        '}';
  }
}
