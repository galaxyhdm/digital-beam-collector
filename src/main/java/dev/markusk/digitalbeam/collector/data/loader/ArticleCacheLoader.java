package dev.markusk.digitalbeam.collector.data.loader;

import dev.markusk.digitalbeam.collector.data.DataProvider;
import dev.markusk.digitalbeam.collector.model.Article;
import org.bson.types.ObjectId;
import org.cache2k.io.AsyncCacheLoader;

import java.util.Optional;

public class ArticleCacheLoader implements AsyncCacheLoader<ObjectId, Optional<Article>> {

  private final DataProvider persistentDataProvider;

  public ArticleCacheLoader(final DataProvider persistentDataProvider) {
    this.persistentDataProvider = persistentDataProvider;
  }

  @Override
  public void load(ObjectId key, Context<ObjectId, Optional<Article>> context, Callback<Optional<Article>> callback) {
    final Optional<Article> article = this.persistentDataProvider.getArticle(key);
    if (article.isEmpty()) {
      callback.onLoadFailure(new NullPointerException(String.format("Article ('%s') not found", key.toString())));
      return;
    }
    callback.onLoadSuccess(article);
  }
}
