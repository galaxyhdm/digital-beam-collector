package dev.markusk.digitalbeam.collector.fetcher;

import dev.markusk.digitalbeam.collector.Collector;
import dev.markusk.digitalbeam.collector.data.DataProvider;
import dev.markusk.digitalbeam.collector.model.Article;
import dev.markusk.digitalbeam.collector.model.Target;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.TimerTask;

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

      final String lastFetchedUrl = filteredInfos.get(0).getUrl();
      LOGGER.debug(String.format("%s | lastUrl: %s", this.target.getShortname(), lastFetchedUrl));

      this.target.setLastUrl(lastFetchedUrl);
      this.dataProvider.updateLastUrl(this.target);
    } catch (Exception exception) {
      LOGGER.error(String.format("Error while fetching infos from: %s", this.target.getShortname()), exception);
    }
  }

  private List<Article> getFilteredInfos(final List<Article> fetchInfos, final String lastUrl) {
    final Article lastInfo = fetchInfos.stream().filter(
        article -> article.getUrl().equals(lastUrl))
        .findFirst().orElse(null);

    return lastInfo == null ? fetchInfos : fetchInfos.subList(0, fetchInfos.indexOf(lastInfo));
  }

}
