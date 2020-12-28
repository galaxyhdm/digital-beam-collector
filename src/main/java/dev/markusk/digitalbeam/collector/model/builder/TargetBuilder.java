package dev.markusk.digitalbeam.collector.model.builder;

import dev.markusk.digitalbeam.collector.model.Target;

public class TargetBuilder {
  private String snowflake;
  private String name;
  private String shortname;
  private String fetchUrl;
  private boolean tor;
  private int waitTime;
  private String datePattern;
  private boolean active;

  public TargetBuilder setSnowflake(final String snowflake) {
    this.snowflake = snowflake;
    return this;
  }

  public TargetBuilder setName(final String name) {
    this.name = name;
    return this;
  }

  public TargetBuilder setShortname(final String shortname) {
    this.shortname = shortname;
    return this;
  }

  public TargetBuilder setFetchUrl(final String fetchUrl) {
    this.fetchUrl = fetchUrl;
    return this;
  }

  public TargetBuilder setTor(final boolean tor) {
    this.tor = tor;
    return this;
  }

  public TargetBuilder setWaitTime(final int waitTime) {
    this.waitTime = waitTime;
    return this;
  }

  public TargetBuilder setDatePattern(final String datePattern) {
    this.datePattern = datePattern;
    return this;
  }

  public TargetBuilder setActive(final boolean active) {
    this.active = active;
    return this;
  }

  public Target createTarget() {
    return new Target(snowflake, name, shortname, fetchUrl, tor, waitTime, datePattern, active);
  }
}