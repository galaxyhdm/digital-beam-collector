package dev.markusk.digitalbeam.collector.data;

import dev.markusk.digitalbeam.collector.data.loader.ArticleCacheLoader;
import dev.markusk.digitalbeam.collector.data.loader.TargetCacheLoader;
import dev.markusk.digitalbeam.collector.model.Article;
import dev.markusk.digitalbeam.collector.model.Target;
import dev.markusk.digitalbeam.collector.model.UserAgent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.cache2k.io.AsyncCacheLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DataProviderCache implements DataProvider {

  /* Constants */
  private static final Logger LOGGER = LogManager.getLogger();

  /* PersistentDataProvider */
  private final DataProvider persistentDataProvider;

  /* Cache */
  private Cache<UUID, Optional<Article>> articleCache;
  private Cache<UUID, Optional<Target>> targetCache;
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
  public Optional<Article> getArticle(final UUID snowflake) {
    if (snowflake == null) return Optional.empty();
    return this.articleCache.get(snowflake);
  }

  @Override
  public void updateArticle(final Article article) {
    if (article == null || article.getSnowflake() == null) return;
    this.persistentDataProvider.updateArticle(article);
  }

  @Override
  public Optional<List<Target>> getTargets() {
    return this.persistentDataProvider.getTargets();
  }

  @Override
  public Optional<Target> getTarget(final UUID snowflake) {
    if (snowflake == null) return Optional.empty();
    return this.targetCache.get(snowflake);
  }

  @Override
  public void updateLastUrl(final Target target) {
    if(target == null || target.getSnowflake() == null) return;
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

  private Cache<UUID, Optional<Article>> createArticleCache(AsyncCacheLoader<UUID, Optional<Article>> cacheLoader) {
    return new Cache2kBuilder<UUID, Optional<Article>>() {
    }
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .refreshAhead(true)
        .loader(cacheLoader)
        .build();
  }

  private Cache<UUID, Optional<Target>> createTargetCache(AsyncCacheLoader<UUID, Optional<Target>> cacheLoader) {
    return new Cache2kBuilder<UUID, Optional<Target>>() {
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
