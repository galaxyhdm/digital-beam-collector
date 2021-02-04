package dev.markusk.digitalbeam.collector.fetcher;

import dev.markusk.digitalbeam.collector.Collector;
import dev.markusk.digitalbeam.collector.data.DataProvider;
import dev.markusk.digitalbeam.collector.misc.VersionCreator;
import dev.markusk.digitalbeam.collector.model.Article;
import dev.markusk.digitalbeam.collector.model.Target;
import dev.markusk.digitalbeam.collector.time.OffsetCalculator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CollectJob extends ScheduledJob {

  private static final Logger LOGGER = LogManager.getLogger();

  private final Collector collector;
  private final Target target;
  private final Fetcher fetcher;
  private final DataProvider dataProvider;

  public CollectJob(final Collector collector, final Target target, final Fetcher fetcher) {
    this.collector = collector;
    this.target = target;
    this.fetcher = fetcher;
    this.dataProvider = this.collector.getDataProvider();
  }

  @Override
  public void run() {
    this.addAttempt();
    try {
      final List<Article> fetchInfos = this.fetcher.getFetchInfos();
      final String lastUrl = this.target.getLastUrl();
      final List<Article> filteredInfos = this.filterArticles(fetchInfos, lastUrl);

      LOGGER.info(String.format("%s | Got %d new article%s",
          this.target.getShortname(),
          filteredInfos.size(),
          filteredInfos.size() == 1 ? "" : "s"));

      if (filteredInfos.size() <= 0) return;
      filteredInfos.forEach(this::updateArticle);

      this.updateLastUrl(filteredInfos);
    } catch (Exception exception) {
      this.checkAttempts(
          () -> this.collector.getFetcherExecutor().scheduleJob(this),
          () -> LOGGER.error(String.format("Error while fetching infos: %s", this.target.getShortname()), exception));
    }
  }

  private void updateArticle(final Article article) {
    this.setQueuedLookups(article);
    VersionCreator.builder(article).setOffset(0).updateOrInsertArticle(this.dataProvider);
  }

  private void setQueuedLookups(final Article article) {
    final List<Date> lookups = this.target.getLookupOffsets().stream()
        .map(s -> OffsetCalculator.calculate(s, article.getReleaseTime()))
        .collect(Collectors.toList());
    article.setQueuedLookups(lookups);
  }

  private void updateLastUrl(final List<Article> filteredInfos) {
    final String lastFetchedUrl = filteredInfos.get(0).getUrl();
    LOGGER.debug(String.format("%s | lastUrl: %s", this.target.getShortname(), lastFetchedUrl));

    this.target.setLastUrl(lastFetchedUrl);
    this.dataProvider.updateLastUrl(this.target);
  }

  private List<Article> filterArticles(final List<Article> fetchInfos, final String lastUrl) {
    final List<Article> filteredInfos = this.getFilteredInfos(fetchInfos, lastUrl);
    LOGGER.debug(String.format("%s | Got %d article%s after last-url-filter",
        this.target.getShortname(),
        filteredInfos.size(),
        filteredInfos.size() == 1 ? "" : "s"));
    return this.filterNotExistingArticles(filteredInfos);
  }

  private List<Article> getFilteredInfos(final List<Article> fetchInfos, final String lastUrl) {
    final Article lastInfo = fetchInfos.stream().filter(
        article -> article.getUrl().equals(lastUrl))
        .findFirst().orElse(null);

    return lastInfo == null ? fetchInfos : fetchInfos.subList(0, fetchInfos.indexOf(lastInfo));
  }

  private List<Article> filterNotExistingArticles(final List<Article> articles) {
    return articles.stream().filter(article -> this.dataProvider.getArticleById(article.getArticleId()).isEmpty())
        .collect(Collectors.toList());
  }

}
