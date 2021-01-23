package dev.markusk.digitalbeam.collector;

import dev.markusk.digitalbeam.collector.console.BetterSystemOut;
import dev.markusk.digitalbeam.collector.console.ConsoleController;
import dev.markusk.digitalbeam.collector.data.DataProvider;
import dev.markusk.digitalbeam.collector.data.DataProviderCache;
import dev.markusk.digitalbeam.collector.data.MongoDataProvider;
import dev.markusk.digitalbeam.collector.misc.SslBuilder;
import joptsimple.OptionSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Collector {

  private static final Logger LOGGER = LogManager.getLogger();

  private final OptionSet optionSet;

  private ConsoleController consoleController;
  private DataProvider persistentDataProvider;
  private DataProvider dataProvider;

  private SslBuilder sslBuilder;

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

    this.persistentDataProvider = new MongoDataProvider();
    this.persistentDataProvider.initialize();

    this.dataProvider = new DataProviderCache(this.persistentDataProvider);
    this.dataProvider.initialize();
  }

  public SslBuilder getSslBuilder() {
    return this.sslBuilder;
  }

  public DataProvider getDataProvider() {
    return this.dataProvider;
  }

  public DataProvider getPersistentDataProvider() {
    return this.persistentDataProvider;
  }

  private void setupConsole() {
    this.consoleController =
        new ConsoleController(VersionInfo.DEBUG || this.optionSet.has("debug") || Environment.DEBUG, false);
    this.consoleController.setupConsole();
    new BetterSystemOut(LOGGER).overwrite();
  }

}
