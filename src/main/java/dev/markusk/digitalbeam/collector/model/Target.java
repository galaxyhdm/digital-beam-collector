package dev.markusk.digitalbeam.collector.model;

import java.util.Objects;

public class Target implements Snowflake {

  private final String snowflake;
  private final String name;
  private final String shortname;
  private final String fetchUrl;
  private final boolean tor;
  private final int waitTime;
  private final String datePattern;
  private final boolean active;

  public Target(final String snowflake, final String name, final String shortname, final String fetchUrl,
      final boolean tor, final int waitTime, final String datePattern, final boolean active) {
    this.datePattern = datePattern;
    this.active = active;
    Objects.requireNonNull(snowflake);
    this.snowflake = snowflake;
    this.name = name;
    this.shortname = shortname;
    this.fetchUrl = fetchUrl;
    this.tor = tor;
    this.waitTime = waitTime;
  }

  public String getName() {
    return this.name;
  }

  public String getShortname() {
    return this.shortname;
  }

  public String getFetchUrl() {
    return this.fetchUrl;
  }

  public boolean isTor() {
    return this.tor;
  }

  public int getWaitTime() {
    return this.waitTime;
  }

  public String getDatePattern() {
    return this.datePattern;
  }

  public boolean isActive() {
    return this.active;
  }

  @Override
  public String getSnowflakeId() {
    return this.snowflake;
  }

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
