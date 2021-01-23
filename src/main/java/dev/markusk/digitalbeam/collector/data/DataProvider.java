package dev.markusk.digitalbeam.collector.data;

import dev.markusk.digitalbeam.collector.model.Article;
import dev.markusk.digitalbeam.collector.model.Target;
import dev.markusk.digitalbeam.collector.model.UserAgent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DataProvider {

  boolean initialize();

  boolean initialize(String connectionUrl);

  Optional<Article> getArticle(UUID snowflake);

  void updateArticle(Article article);

  Optional<List<Target>> getTargets();

  Optional<Target> getTarget(UUID snowflake);

  void updateLastUrl(Target target);

  Optional<List<UserAgent>> getUserAgents();

  void close();

}
