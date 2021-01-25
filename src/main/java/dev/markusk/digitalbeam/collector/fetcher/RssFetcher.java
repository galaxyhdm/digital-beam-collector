package dev.markusk.digitalbeam.collector.fetcher;

import com.apptastic.rssreader.Item;
import com.apptastic.rssreader.RssReader;
import dev.markusk.digitalbeam.collector.Collector;
import dev.markusk.digitalbeam.collector.misc.CustomRssReader;
import dev.markusk.digitalbeam.collector.model.Article;
import dev.markusk.digitalbeam.collector.model.Target;
import dev.markusk.digitalbeam.collector.model.Version;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

public class RssFetcher implements Fetcher {

  private static final Logger LOGGER = LogManager.getLogger(Collector.class);

  private Collector collector;
  private Target target;
  private RssReader rssReader;
  private SimpleDateFormat simpleDateFormat;

  @Override
  public void initialize(final Collector collector, final Target target) {
    this.collector = collector;
    this.target = target;
    this.rssReader =
        new CustomRssReader(collector.getSslBuilder(),
            collector.getDataProvider().getUserAgents().orElse(null),
            target.isTor());
    this.simpleDateFormat = new SimpleDateFormat(target.getDatePattern(), Locale.US);
    this.simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
  }

  @Override
  public List<Article> getFetchInfos(final Date fetchTime) throws Exception {
    final List<Article> articles = new ArrayList<>();
    try (final Stream<Item> items = this.rssReader.read(this.target.getFetchUrl())) {
      items.forEach(item -> articles.add(this.itemToArticle(item, fetchTime)));
    }
    return articles;
  }

  @Override
  public Target getTarget() {
    return this.target;
  }

  private Article itemToArticle(final Item item, final Date fetchTime) {
    final Article article = new Article();
    article.setArticleId(""); // TODO: 28.12.20 create article id
    article.setTargetSnowflake(this.target.getSnowflake());
    article.setTitle(item.getTitle().orElse(""));
    article.setUrl(item.getLink().orElse(""));
    article.setReleaseTime(this.extractDate(item.getPubDate().orElse(null)));
    article.setFetchTime(fetchTime);
    article.setVersions(List.of(this.createFirstVersion(fetchTime)));
    return article;
  }

  private Version createFirstVersion(final Date updateTime) {
    final Version version = new Version();
    //versionBuilder.setVersionId();
    version.setVersion(0);
    version.setUpdateTime(updateTime);
    version.setAutoOffset("0d");
    return version;
  }

  /**
   * @param inputDate is a formatted date string
   *                  <p>
   *                  This string can be null. This means that the pubDate could not be extracted from the RSS feed.
   *                  Then return null.
   *                  <p>
   *                  Example: Fri, 13 Mar 2020 19:40:45 +0100
   * @return a date object in utc time zone!
   */
  public Date extractDate(String inputDate) {
    if (inputDate == null) return null;
    final Date parse;
    try {
      parse = this.simpleDateFormat.parse(inputDate);
    } catch (ParseException e) {
      LOGGER.error("Error while parsing date", e);
      return null;
    }
    return parse;
  }

}
