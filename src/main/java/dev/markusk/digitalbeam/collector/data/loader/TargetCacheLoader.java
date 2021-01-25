package dev.markusk.digitalbeam.collector.data.loader;

import dev.markusk.digitalbeam.collector.data.DataProvider;
import dev.markusk.digitalbeam.collector.model.Target;
import org.bson.types.ObjectId;
import org.cache2k.io.AsyncCacheLoader;

import java.util.Optional;

public class TargetCacheLoader implements AsyncCacheLoader<ObjectId, Optional<Target>> {

  private final DataProvider persistentDataProvider;

  public TargetCacheLoader(final DataProvider persistentDataProvider) {
    this.persistentDataProvider = persistentDataProvider;
  }

  @Override
  public void load(ObjectId key, Context<ObjectId, Optional<Target>> ctx, Callback<Optional<Target>> callback) {
    final Optional<Target> target = this.persistentDataProvider.getTarget(key);
    if (target.isEmpty()) {
      callback.onLoadFailure(new NullPointerException(String.format("Target ('%s') not found", key.toString())));
      return;
    }
    callback.onLoadSuccess(target);
  }

}
