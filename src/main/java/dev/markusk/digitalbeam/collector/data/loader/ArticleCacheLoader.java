package dev.markusk.digitalbeam.collector.data.loader;

import dev.markusk.digitalbeam.collector.data.AbstractDataManager;
import dev.markusk.digitalbeam.collector.model.Article;
import org.cache2k.io.AsyncCacheLoader;

import java.util.Optional;
import java.util.UUID;

public class ArticleCacheLoader implements AsyncCacheLoader<UUID, Optional<Article>> {

  private final AbstractDataManager persistentDataManager;

  public ArticleCacheLoader(final AbstractDataManager persistentDataManager) {
    this.persistentDataManager = persistentDataManager;
  }

  @Override
  public void load(UUID key, Context<UUID, Optional<Article>> context, Callback<Optional<Article>> callback) {
    final Optional<Article> article = this.persistentDataManager.getArticle(key);
    if (article.isEmpty()) {
      callback.onLoadFailure(new NullPointerException(String.format("Article ('%s') not found", key.toString())));
      return;
    }
    callback.onLoadSuccess(article);
  }
}
