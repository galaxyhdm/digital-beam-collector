package dev.markusk.digitalbeam.collector.fetcher;

import dev.markusk.digitalbeam.collector.Environment;

public abstract class ScheduledJob implements Runnable {

  private int attempts;

  protected void resetAttempts() {
    this.attempts = 0;
  }

  protected void addAttempt() {
    this.attempts++;
  }

  protected void checkAttempts(final Runnable defaultRunnable, final Runnable exceeded) {
    if (this.attempts <= Environment.MAX_TRIES) defaultRunnable.run();
    else exceeded.run();
  }

}
