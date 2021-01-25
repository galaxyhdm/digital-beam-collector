package dev.markusk.digitalbeam.collector.data;

import dev.markusk.digitalbeam.collector.model.Article;
import dev.markusk.digitalbeam.collector.model.Target;
import dev.markusk.digitalbeam.collector.model.UserAgent;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

public interface DataProvider {

  boolean initialize();

  boolean initialize(String connectionUrl);

  Optional<Article> getArticle(ObjectId objectId);

  void updateArticle(Article article);

  Optional<List<Target>> getTargets();

  Optional<List<Target>> getActiveTargets();

  Optional<Target> getTarget(ObjectId objectId);

  void updateLastUrl(Target target);

  Optional<List<UserAgent>> getUserAgents();

  void close();

}
