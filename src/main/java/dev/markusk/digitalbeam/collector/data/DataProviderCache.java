package dev.markusk.digitalbeam.collector.data;

import dev.markusk.digitalbeam.collector.data.loader.ArticleCacheLoader;
import dev.markusk.digitalbeam.collector.data.loader.TargetCacheLoader;
import dev.markusk.digitalbeam.collector.model.Article;
import dev.markusk.digitalbeam.collector.model.Target;
import dev.markusk.digitalbeam.collector.model.UserAgent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.cache2k.io.AsyncCacheLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class DataProviderCache implements DataProvider {

  /* Constants */
  private static final Logger LOGGER = LogManager.getLogger();

  /* PersistentDataProvider */
  private final DataProvider persistentDataProvider;

  /* Cache */
  private Cache<ObjectId, Optional<Article>> articleCache;
  private Cache<ObjectId, Optional<Target>> targetCache;
  private List<UserAgent> userAgents;

  public DataProviderCache(final DataProvider persistentDataProvider) {
    this.persistentDataProvider = persistentDataProvider;
  }

  @Override
  public boolean initialize() {
    return this.initialize("");
  }

  @Override
  public boolean initialize(final String connectionUrl) {
    /* Cache */
    LOGGER.debug("Creating caches...");
    this.userAgents = new ArrayList<>();
    this.articleCache = this.createArticleCache(new ArticleCacheLoader(this.persistentDataProvider));
    this.targetCache = this.createTargetCache(new TargetCacheLoader(this.persistentDataProvider));

    /* Pre-Filling */
    LOGGER.debug("Filling caches...");
    this.loadUserAgents();
    return true;
  }

  @Override
  public Optional<Article> getArticle(final ObjectId objectId) {
    if (objectId == null) throw new NullPointerException("Id ist null");
    return this.articleCache.get(objectId);
  }

  @Override
  public void updateArticle(final Article article) {
    if (article == null || article.getObjectId() == null) throw new NullPointerException("Article ist null");
    this.persistentDataProvider.updateArticle(article);
  }

  @Override
  public Optional<List<Target>> getTargets() {
    return this.persistentDataProvider.getTargets();
  }

  @Override
  public Optional<List<Target>> getActiveTargets() {
    return this.persistentDataProvider.getActiveTargets();
  }

  @Override
  public Optional<Target> getTarget(final ObjectId objectId) {
    if (objectId == null) throw new NullPointerException("Id ist null");
    return this.targetCache.get(objectId);
  }

  @Override
  public void updateLastUrl(final Target target) {
    if (target == null || target.getObjectId() == null) throw new NullPointerException("Target ist null");
    this.persistentDataProvider.updateLastUrl(target);
  }

  @Override
  public Optional<List<UserAgent>> getUserAgents() {
    if (this.userAgents == null || this.userAgents.isEmpty()) this.loadUserAgents();
    return Optional.of(this.userAgents);
  }

  @Override
  public void close() {
    this.targetCache.close();

    this.targetCache = null;
    this.userAgents = null;
  }

  private Cache<ObjectId, Optional<Article>> createArticleCache(
      AsyncCacheLoader<ObjectId, Optional<Article>> cacheLoader) {
    return new Cache2kBuilder<ObjectId, Optional<Article>>() {
    }
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .refreshAhead(true)
        .loader(cacheLoader)
        .build();
  }

  private Cache<ObjectId, Optional<Target>> createTargetCache(
      AsyncCacheLoader<ObjectId, Optional<Target>> cacheLoader) {
    return new Cache2kBuilder<ObjectId, Optional<Target>>() {
    }
        .expireAfterWrite(15, TimeUnit.MINUTES)
        .refreshAhead(true)
        .loader(cacheLoader)
        .build();
  }

  private void loadUserAgents() {
    final Optional<List<UserAgent>> userAgents = this.persistentDataProvider.getUserAgents();
    if (userAgents.isEmpty()) return;
    this.userAgents.addAll(userAgents.get());
  }

}
