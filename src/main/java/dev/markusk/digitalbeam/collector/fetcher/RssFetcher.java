package dev.markusk.digitalbeam.collector.fetcher;

import com.apptastic.rssreader.Item;
import com.apptastic.rssreader.RssReader;
import dev.markusk.digitalbeam.collector.Collector;
import dev.markusk.digitalbeam.collector.interfaces.AbstractFetcher;
import dev.markusk.digitalbeam.collector.misc.CustomRssReader;
import dev.markusk.digitalbeam.collector.model.Article;
import dev.markusk.digitalbeam.collector.model.Target;
import dev.markusk.digitalbeam.collector.model.Version;
import dev.markusk.digitalbeam.collector.model.builder.ArticleBuilder;
import dev.markusk.digitalbeam.collector.model.builder.VersionBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

public class RssFetcher implements AbstractFetcher {

  private static final Logger LOGGER = LogManager.getLogger(Collector.class);

  private Collector collector;
  private Target target;
  private RssReader rssReader;
  private SimpleDateFormat simpleDateFormat;

  @Override
  public void initialize(final Collector collector, final Target target) {
    this.collector = collector;
    this.target = target;
    this.rssReader = new CustomRssReader(collector.getSslBuilder(), collector.getUserAgents(), target.isTor());
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
    final ArticleBuilder articleBuilder = new ArticleBuilder();
    articleBuilder.setArticleId(""); // TODO: 28.12.20 create article id
    articleBuilder.setTargetSnowflake(this.target.getSnowflakeId());
    articleBuilder.setTitle(item.getTitle().orElse(""));
    articleBuilder.setUrl(item.getLink().orElse(""));
    articleBuilder.setReleaseTime(this.extractDate(item.getPubDate().orElse(null)));
    articleBuilder.setFetchTime(fetchTime);
    articleBuilder.setVersions(List.of(this.createFirstVersion(fetchTime)));
    return articleBuilder.createArticle();
  }

  private Version createFirstVersion(final Date updateTime) {
    final VersionBuilder versionBuilder = new VersionBuilder();
    //versionBuilder.setVersionId();
    versionBuilder.setVersion(0);
    versionBuilder.setUpdateTime(updateTime);
    versionBuilder.setAutoOffset("0d");
    return versionBuilder.createVersion();
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
