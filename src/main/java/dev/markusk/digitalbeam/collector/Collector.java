package dev.markusk.digitalbeam.collector;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import dev.markusk.digitalbeam.collector.console.BetterSystemOut;
import dev.markusk.digitalbeam.collector.console.ConsoleController;
import dev.markusk.digitalbeam.collector.misc.SslBuilder;
import dev.markusk.digitalbeam.collector.model.UserAgent;
import dev.markusk.digitalbeam.collector.mongodb.MongoConnector;
import joptsimple.OptionSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Collector {

  private static final Logger LOGGER = LogManager.getLogger();

  private final OptionSet optionSet;

  private ConsoleController consoleController;
  private MongoConnector mongoConnector;

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

    this.mongoConnector = new MongoConnector(Environment.CONNECTION_URL);
    this.mongoConnector.connect();

    this.userAgents = new ArrayList<>();
    final MongoCollection<UserAgent> userAgents = this.mongoConnector.getCollection("user_agents", UserAgent.class);
    final FindIterable<UserAgent> find = userAgents.find();
    find.forEach((Consumer<? super UserAgent>) this.userAgents::add);
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
