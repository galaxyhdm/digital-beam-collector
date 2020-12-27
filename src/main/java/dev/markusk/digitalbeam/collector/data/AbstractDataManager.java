package dev.markusk.digitalbeam.collector.data;

import dev.markusk.digitalbeam.collector.model.Target;
import dev.markusk.digitalbeam.collector.model.UserAgent;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

public interface AbstractDataManager {

  boolean initialize(Logger logger, String connectionUrl);

  Optional<List<Target>> getTargets();

  Optional<List<UserAgent>> getUserAgents();

  void close();

}
