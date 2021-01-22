package dev.markusk.digitalbeam.collector.data;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Updates;
import dev.markusk.digitalbeam.collector.Environment;
import dev.markusk.digitalbeam.collector.model.Article;
import dev.markusk.digitalbeam.collector.model.Target;
import dev.markusk.digitalbeam.collector.model.UserAgent;
import dev.markusk.digitalbeam.collector.mongodb.MongoConnector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.mongodb.client.model.Filters.eq;

public class MongoDataManager implements AbstractDataManager {

  /* Constants */
  private static final Logger LOGGER = LogManager.getLogger();

  private static final String ARTICLE_COLLECTION_NAME = "articles";
  private static final String TARGET_COLLECTION_NAME = "targets";
  private static final String USER_AGENT_COLLECTION_NAME = "user_agents";

  /* Connector */
  private MongoConnector mongoConnector;

  /* Collections */
  private MongoCollection<Article> articleCollection;
  private MongoCollection<Target> targetCollection;
  private MongoCollection<UserAgent> userAgentCollection;

  @Override
  public boolean initialize() {
    return this.initialize(Environment.CONNECTION_URL);
  }

  @Override
  public boolean initialize(final String connectionUrl) {
    if (connectionUrl == null) return false;
    this.mongoConnector = connectionUrl.isEmpty() ? new MongoConnector() : new MongoConnector(connectionUrl);
    if (!this.mongoConnector.connect()) return false;

    /* Collections */
    this.articleCollection = this.mongoConnector.getCollection(ARTICLE_COLLECTION_NAME, Article.class);
    this.targetCollection = this.mongoConnector.getCollection(TARGET_COLLECTION_NAME, Target.class);
    this.userAgentCollection = this.mongoConnector.getCollection(USER_AGENT_COLLECTION_NAME, UserAgent.class);

    /* Create indexes */
    LOGGER.debug("Creating indexes...");
    final IndexOptions indexOptions = new IndexOptions().unique(true);
    this.articleCollection.createIndex(Indexes.ascending("snowflake"), indexOptions.name("snowflake_index"));
    this.targetCollection.createIndex(Indexes.ascending("snowflake"), indexOptions.name("snowflake_index"));
    this.userAgentCollection.createIndex(Indexes.ascending("snowflake"), indexOptions.name("snowflake_index"));
    LOGGER.debug("Indexes created");
    return this.mongoConnector.isConnected();
  }

  @Override
  public Optional<Article> getArticle(final UUID snowflake) {
    final FindIterable<Article> articleIterable = this.articleCollection.find(eq("snowflake", snowflake)).limit(1);
    return Optional.ofNullable(articleIterable.first());
  }

  @Override
  public void updateArticle(final Article article) {
    if (this.articleCollection.replaceOne(eq("snowflake", article.getSnowflake()), article).getModifiedCount() == 0)
      this.articleCollection.insertOne(article);
  }

  @Override
  public Optional<List<Target>> getTargets() {
    final FindIterable<Target> targetFindIterable = this.targetCollection.find();
    return Optional.of(StreamSupport.stream(targetFindIterable.spliterator(), false).collect(Collectors.toList()));
  }

  @Override
  public Optional<Target> getTarget(final UUID snowflake) {
    final FindIterable<Target> targetFindIterable = this.targetCollection.find(eq("snowflake", snowflake)).limit(1);
    return Optional.ofNullable(targetFindIterable.first());
  }

  @Override
  public void updateLastUrl(final Target target) {
    this.targetCollection
        .updateOne(eq("snowflake", target.getSnowflake()), Updates.set("last_url", target.getLastUrl()));
  }

  @Override
  public Optional<List<UserAgent>> getUserAgents() {
    final List<UserAgent> list = new ArrayList<>();
    final FindIterable<UserAgent> find = this.userAgentCollection.find();
    find.forEach((Consumer<? super UserAgent>) list::add);
    return Optional.of(list);
  }

  @Override
  public void close() {
    this.mongoConnector.disconnect();
  }

}
