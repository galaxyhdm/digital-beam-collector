package dev.markusk.digitalbeam.collector;

import dev.markusk.digitalbeam.collector.console.BetterSystemOut;
import dev.markusk.digitalbeam.collector.console.ConsoleController;
import dev.markusk.digitalbeam.collector.data.AbstractDataManager;
import dev.markusk.digitalbeam.collector.data.PostgresDataManager;
import dev.markusk.digitalbeam.collector.misc.SslBuilder;
import dev.markusk.digitalbeam.collector.model.UserAgent;
import joptsimple.OptionSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class Collector {

  private static final Logger LOGGER = LogManager.getLogger();

  private final OptionSet optionSet;

  private ConsoleController consoleController;
  private AbstractDataManager dataManager;
  private SslBuilder sslBuilder;
  private List<UserAgent> userAgents;

  private boolean running;

  public Collector(final OptionSet optionSet) {
    this.optionSet = optionSet;
  }

  public void initialize() {
    if (this.running) return;
    this.running = true;
    this.setupConsole();
    //LOGGER.debug("NO_FETCH=" +  + " DEBUG=" + DEBUG + " POOL_SIZE=" + POOL_SIZE);
    LOGGER.info(String
        .format("Starting collector... (Version Nr. %s built on %s at %s)", VersionInfo.VERSION, VersionInfo.BUILD_DATE,
            VersionInfo.BUILD_TIME));

    this.sslBuilder = new SslBuilder();

    this.dataManager = new PostgresDataManager();
    this.dataManager.initialize(LOGGER, Environment.CONNECTION_URL);

    this.userAgents = this.dataManager.getUserAgents().orElse(List.of());
  }

  public AbstractDataManager getDataManager() {
    return this.dataManager;
  }

  public SslBuilder getSslBuilder() {
    return this.sslBuilder;
  }

  public List<UserAgent> getUserAgents() {
    return this.userAgents;
  }

  private void setupConsole() {
    this.consoleController =
        new ConsoleController(VersionInfo.DEBUG || this.optionSet.has("debug") || Environment.DEBUG, false);
    this.consoleController.setupConsole();
    new BetterSystemOut(LOGGER).overwrite();
  }

}
