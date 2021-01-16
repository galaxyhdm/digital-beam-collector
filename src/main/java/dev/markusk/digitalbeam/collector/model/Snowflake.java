package dev.markusk.digitalbeam.collector.model;

import java.util.UUID;

public interface Snowflake {

  UUID getSnowflake();

  void setSnowflake(UUID snowflake);

}
