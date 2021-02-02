package dev.markusk.digitalbeam.collector.fetcher;

import java.util.TimerTask;

public class ScheduleTask extends TimerTask {

  private final FetcherExecutor fetcherExecutor;
  private final ScheduledJob job;

  public ScheduleTask(final FetcherExecutor fetcherExecutor, final ScheduledJob job) {
    this.fetcherExecutor = fetcherExecutor;
    this.job = job;
  }

  @Override
  public void run() {
    this.job.resetAttempts();
    this.fetcherExecutor.scheduleJob(this.job);
  }
}
