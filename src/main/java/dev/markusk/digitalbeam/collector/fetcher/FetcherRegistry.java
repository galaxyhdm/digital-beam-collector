package dev.markusk.digitalbeam.collector.fetcher;

import dev.markusk.digitalbeam.collector.Collector;
import dev.markusk.digitalbeam.collector.misc.ClassContainer;
import dev.markusk.digitalbeam.collector.model.Target;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetcherRegistry {

  private static final Logger LOGGER = LogManager.getLogger();

  private final Collector collector;
  private final Map<Target, Fetcher> fetcherMap;

  public FetcherRegistry(final Collector collector) {
    this.collector = collector;
    this.fetcherMap = new HashMap<>();
  }

  public void registerFetchers() {
    final List<Target> targets = this.collector.getDataProvider().getActiveTargets().orElse(List.of());
    LOGGER.info(String.format("Creating %s target%s.", targets.size(), targets.size() == 1 ? "" : "s"));
    targets.forEach(this::registerFetcher);
  }

  public void registerFetcher(final Target target) {
    try {
      final Fetcher fetcher = ClassContainer.newInstance(target.getFetcherClassPath(), Fetcher.class);
      fetcher.initialize(this.collector, target);
      this.fetcherMap.put(target, fetcher);
      LOGGER.debug(String.format("Created fetcher: %s - %s", target.getShortname(), target.getName()));
    } catch (Exception exception) {
      LOGGER.error(String.format("Error while registerFetcher (%s)", target.getShortname()), exception);
    }
  }

  public Map<Target, Fetcher> getFetcherMap() {
    return this.fetcherMap;
  }
}
