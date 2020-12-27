package dev.markusk.digitalbeam.collector.model;

import java.util.Objects;

public class UserAgent implements Snowflake {

  private final String snowflake;
  private final String userAgent;

  public UserAgent(final String snowflake, final String userAgent) {
    Objects.requireNonNull(snowflake);
    this.snowflake = snowflake;
    this.userAgent = userAgent;
  }

  public String getUserAgent() {
    return this.userAgent;
  }

  @Override
  public String getSnowflakeId() {
    return this.snowflake;
  }

  @Override
  public String toString() {
    return "UserAgent{" +
        "snowflake='" + snowflake + '\'' +
        ", userAgent='" + userAgent + '\'' +
        '}';
  }
}
