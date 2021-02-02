package dev.markusk.digitalbeam.collector.fetcher;

import dev.markusk.digitalbeam.collector.Collector;
import dev.markusk.digitalbeam.collector.Environment;
import dev.markusk.digitalbeam.collector.model.Target;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class FetcherController {

  private final Collector collector;
  private final FetcherRegistry fetcherRegistry;
  private final Map<Target, ScheduleTask> jobs;
  private final Timer timer;

  public FetcherController(final Collector collector, final FetcherRegistry fetcherRegistry) {
    this.collector = collector;
    this.fetcherRegistry = fetcherRegistry;
    this.jobs = new HashMap<>();
    this.timer = new Timer("Fetch Executor");
  }

  public void initializeJobs() {
    this.cancelJobs();
    this.jobs.clear();
    this.fetcherRegistry.getFetcherMap().forEach(this::createJob);
    this.createLookupJob();
  }

  private void createJob(final Target target, final Fetcher fetcher) {
    final CollectJob collectJob = new CollectJob(this.collector, target, fetcher);
    final ScheduleTask scheduleTask = new ScheduleTask(this.collector.getFetcherExecutor(), collectJob);
    this.jobs.put(target, scheduleTask);
    this.timer.scheduleAtFixedRate(scheduleTask, 100L, TimeUnit.MINUTES.toMillis(target.getWaitTime()));
  }

  private void createLookupJob() {
    if (Environment.LOOKUP_WAIT_TIME == 0) return;
    final LookupJob lookupJob = new LookupJob(this.collector, this.collector.getDataProvider());
    final ScheduleTask scheduleTask = new ScheduleTask(this.collector.getFetcherExecutor(), lookupJob);
    this.timer.scheduleAtFixedRate(scheduleTask,
        TimeUnit.SECONDS.toMillis(30),
        TimeUnit.MINUTES.toMillis(Environment.LOOKUP_WAIT_TIME));
  }

  private void cancelJobs() {
    this.jobs.forEach((target, collectJob) -> collectJob.cancel());
    this.timer.purge();
  }

}
