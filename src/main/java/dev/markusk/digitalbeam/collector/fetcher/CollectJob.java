package dev.markusk.digitalbeam.collector.fetcher;

import dev.markusk.digitalbeam.collector.Collector;
import dev.markusk.digitalbeam.collector.data.DataProvider;
import dev.markusk.digitalbeam.collector.model.Article;
import dev.markusk.digitalbeam.collector.model.Target;
import dev.markusk.digitalbeam.collector.model.Version;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;

import java.util.*;

public class CollectJob extends TimerTask {

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
    try {
      final List<Article> fetchInfos = this.fetcher.getFetchInfos();
      final String lastUrl = this.target.getLastUrl();
      final List<Article> filteredInfos = this.getFilteredInfos(fetchInfos, lastUrl);

      LOGGER.info(String.format("%s | Got %d new article%s",
          this.target.getShortname(),
          filteredInfos.size(),
          filteredInfos.size() == 1 ? "" : "s"));

      if (filteredInfos.size() <= 0) return;
      filteredInfos.forEach(this::updateOrInsertArticle);

      this.updateLastUrl(filteredInfos);
    } catch (Exception exception) {
      LOGGER.error(String.format("Error while fetching infos from: %s", this.target.getShortname()), exception);
    }
  }

  private void updateOrInsertArticle(final Article article) {
    final Optional<Article> articleById = this.dataProvider.getArticleById(article.getArticleId());
    articleById.ifPresentOrElse(this::updateArticle, () -> this.updateArticle(article));
    articleById.ifPresentOrElse(
        data -> LOGGER.debug(String.format("Present in database (%s), updating versions", data.getObjectId())),
        () -> LOGGER.debug(String.format("Not present in database, inserting article (%s)", article.getObjectId())));
  }

  private void updateArticle(final Article article) {
    final List<Version> versions = this.updateVersionList(article);
    article.setVersions(versions);
    this.dataProvider.updateArticle(article);
  }

  private List<Version> updateVersionList(final Article article) {
    final List<Version> versions =
        article.getVersions() != null ? new ArrayList<>(article.getVersions()) : new ArrayList<>();
    final Version newVersion = this.createVersion(versions.size() != 0 ? versions.get(versions.size() - 1) : null);
    versions.add(newVersion);
    return versions;
  }

  private Version createVersion(final Version previousVersion) {
    final Version version = new Version();
    version.setObjectId(new ObjectId());
    version.setVersion(previousVersion != null ? previousVersion.getVersion() + 1 : 0);
    version.setUpdateTime(new Date());
    version.setAutoOffset("0d");
    return version;
  }

  private void updateLastUrl(final List<Article> filteredInfos) {
    final String lastFetchedUrl = filteredInfos.get(0).getUrl();
    LOGGER.debug(String.format("%s | lastUrl: %s", this.target.getShortname(), lastFetchedUrl));

    this.target.setLastUrl(lastFetchedUrl);
    this.dataProvider.updateLastUrl(this.target);
  }

  private List<Article> getFilteredInfos(final List<Article> fetchInfos, final String lastUrl) {
    final Article lastInfo = fetchInfos.stream().filter(
        article -> article.getUrl().equals(lastUrl))
        .findFirst().orElse(null);

    return lastInfo == null ? fetchInfos : fetchInfos.subList(0, fetchInfos.indexOf(lastInfo));
  }

}
