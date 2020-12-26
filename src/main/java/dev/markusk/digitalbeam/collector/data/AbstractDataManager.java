package dev.markusk.digitalbeam.collector.data;

import org.apache.logging.log4j.Logger;

public interface AbstractDataManager {

  boolean initialize(Logger logger, String connectionUrl);

  void close();

}
