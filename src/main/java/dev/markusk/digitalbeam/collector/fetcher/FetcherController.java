package dev.markusk.digitalbeam.collector.fetcher;

import dev.markusk.digitalbeam.collector.Collector;
import dev.markusk.digitalbeam.collector.model.Target;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

public class FetcherController {

  private final Collector collector;
  private final FetcherRegistry fetcherRegistry;
  private final Map<Target, CollectJob> collectJobs;
  private final Timer timer;

  public FetcherController(final Collector collector, final FetcherRegistry fetcherRegistry) {
    this.collector = collector;
    this.fetcherRegistry = fetcherRegistry;
    this.collectJobs = new HashMap<>();
    this.timer = new Timer("Fetch Executor", true);
  }


}
