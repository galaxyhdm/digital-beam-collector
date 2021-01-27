package dev.markusk.digitalbeam.collector.fetcher;

import dev.markusk.digitalbeam.collector.Collector;
import dev.markusk.digitalbeam.collector.model.Article;
import dev.markusk.digitalbeam.collector.model.Target;

import java.util.Date;
import java.util.List;

public interface Fetcher {

  /**
   * @param collector the main {@link Collector} instance
   * @param target    a {@link Target} object
   */
  void initialize(final Collector collector, Target target);

  /**
   * @return a list with {@link Article} extracted from the fetch source.
   * <p>
   * Sorted from new to old!
   * <p>
   * throws {@link Exception} when something is not working
   */
  default List<Article> getFetchInfos() throws Exception {
    return getFetchInfos(new Date());
  }

  /**
   * @param fetchTime this date represents the fetch time of the articles
   * @return a list with {@link Article} extracted from the fetch source
   * <p>
   * throws {@link Exception} when something is not working
   */
  List<Article> getFetchInfos(Date fetchTime) throws Exception;

  /**
   * @return the initialize {@link Target} object
   */
  Target getTarget();

}
