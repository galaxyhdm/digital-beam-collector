package dev.markusk.digitalbeam.collector.model.builder;

import dev.markusk.digitalbeam.collector.model.UserAgent;

public class UserAgentBuilder {
  private String snowflake;
  private String userAgent;

  public UserAgentBuilder setSnowflake(final String snowflake) {
    this.snowflake = snowflake;
    return this;
  }

  public UserAgentBuilder setUserAgent(final String userAgent) {
    this.userAgent = userAgent;
    return this;
  }

  public UserAgent createUserAgent() {
    return new UserAgent(snowflake, userAgent);
  }
}