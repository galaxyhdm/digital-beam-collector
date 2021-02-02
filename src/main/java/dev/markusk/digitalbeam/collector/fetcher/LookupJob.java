package dev.markusk.digitalbeam.collector.fetcher;

import dev.markusk.digitalbeam.collector.Collector;
import dev.markusk.digitalbeam.collector.data.DataProvider;
import dev.markusk.digitalbeam.collector.misc.VersionCreator;
import dev.markusk.digitalbeam.collector.model.Article;
import dev.markusk.digitalbeam.collector.time.OffsetCalculator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class LookupJob extends ScheduledJob {

  private static final Logger LOGGER = LogManager.getLogger();

  private final Collector collector;
  private final DataProvider dataProvider;

  public LookupJob(final Collector collector, final DataProvider dataProvider) {
    this.collector = collector;
    this.dataProvider = dataProvider;
  }

  @Override
  public void run() {
    this.addAttempt();
    try {
      final Date referenceDate = new Date();
      final List<Article> articles = this.dataProvider.getLookupArticles(referenceDate).orElse(List.of());
      LOGGER.info(String.format("Found %s article%s to update", articles.size(), articles.size() != 1 ? "s" : ""));

      articles.forEach(article -> {
        final List<Date> queuedLookups = article.getQueuedLookups();
        final Date latestExpiredDate = this.getLatestExpiredDate(queuedLookups, referenceDate);

        final long distance = OffsetCalculator.calculateDistance(article.getReleaseTime(), latestExpiredDate);
        this.removeExpiredLookups(queuedLookups, referenceDate);

        VersionCreator.builder(article).setOffset(distance).performPersistentUpdate(this.dataProvider);
        LOGGER.debug(String.format("Creating new version for article '%s'", article.getObjectId()));
      });
    } catch (Exception exception) {
      this.checkAttempts(
          () -> this.collector.getFetcherExecutor().scheduleJob(this),
          () -> LOGGER.error("Error while creating versions", exception));
    }
  }

  private Date getLatestExpiredDate(final List<Date> queuedLookups, final Date referenceDate) {
    queuedLookups.sort(Comparator.reverseOrder());
    return queuedLookups.stream().filter(date -> this.isExpired(date, referenceDate)).findFirst().orElse(null);
  }

  private void removeExpiredLookups(final List<Date> queuedLookups, final Date referenceDate) {
    queuedLookups.removeIf(date -> this.isExpired(date, referenceDate));
  }

  private void removeExpiredLookups(final List<Date> queuedLookups) {
    queuedLookups.removeIf(this::isExpired);
  }

  private boolean isExpired(final Date date) {
    return isExpired(date, new Date());
  }

  private boolean isExpired(final Date date, final Date referenceDate) {
    Objects.requireNonNull(date, "Date is null!");
    Objects.requireNonNull(referenceDate, "Reference date is null");
    return date.before(referenceDate);
  }

}
