package dev.markusk.digitalbeam.collector.data.loader;

import dev.markusk.digitalbeam.collector.data.AbstractDataManager;
import dev.markusk.digitalbeam.collector.model.Target;
import org.cache2k.io.AsyncCacheLoader;

import java.util.Optional;
import java.util.UUID;

public class TargetCacheLoader implements AsyncCacheLoader<UUID, Optional<Target>> {

  private final AbstractDataManager persistentDataManager;

  public TargetCacheLoader(final AbstractDataManager persistentDataManager) {
    this.persistentDataManager = persistentDataManager;
  }

  @Override
  public void load(UUID key, Context<UUID, Optional<Target>> ctx, Callback<Optional<Target>> callback) {
    final Optional<Target> target = this.persistentDataManager.getTarget(key);
    if (target.isEmpty()) {
      callback.onLoadFailure(new NullPointerException(String.format("Target ('%s') not found", key.toString())));
      return;
    }
    callback.onLoadSuccess(target);
  }

}
