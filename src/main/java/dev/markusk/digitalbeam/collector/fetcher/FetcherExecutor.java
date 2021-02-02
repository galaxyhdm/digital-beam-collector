package dev.markusk.digitalbeam.collector.fetcher;

import dev.markusk.digitalbeam.collector.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.*;

public class FetcherExecutor {

  private static final Logger LOGGER = LogManager.getLogger();

  private final ExecutorService jobScheduler = Executors.newSingleThreadExecutor();
  private final ExecutorService jobExecutor;
  private final BlockingQueue<ScheduledJob> queue;

  private boolean running;

  public FetcherExecutor() {
    this.jobExecutor = Executors.newFixedThreadPool(Environment.POOL_SIZE);
    this.queue = new LinkedBlockingQueue<>();
  }

  public void initializeScheduler() {
    if (this.running) return;
    this.running = true;

    this.jobScheduler.execute(() -> {
      while (this.running) {
        try {
          this.jobExecutor.execute(this.queue.take());
        } catch (InterruptedException e) {
          LOGGER.error("Error while executing task!", e);
        }
      }
    });
  }

  public void scheduleJob(final ScheduledJob collectJob) {
    this.queue.add(collectJob);
  }

  private void closeExecutor(final ExecutorService executorService) {
    executorService.shutdown();
    try {
      if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
        executorService.shutdownNow();
      }
    } catch (InterruptedException e) {
      executorService.shutdownNow();
    }
  }

  public void close() {
    this.running = false;
    this.closeExecutor(this.jobExecutor);
    this.closeExecutor(this.jobScheduler);
  }

}
