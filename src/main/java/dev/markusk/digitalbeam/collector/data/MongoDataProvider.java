package dev.markusk.digitalbeam.collector.data;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Updates;
import dev.markusk.digitalbeam.collector.Environment;
import dev.markusk.digitalbeam.collector.model.Article;
import dev.markusk.digitalbeam.collector.model.Target;
import dev.markusk.digitalbeam.collector.model.UserAgent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.mongodb.client.model.Filters.eq;

public class MongoDataProvider implements DataProvider {

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

    return this.mongoConnector.isConnected();
  }

  @Override
  public Optional<Article> getArticle(final ObjectId objectId) {
    if (objectId == null) throw new NullPointerException("Id ist null");
    final FindIterable<Article> articleIterable = this.articleCollection.find(eq(objectId)).limit(1);
    return Optional.ofNullable(articleIterable.first());
  }

  @Override
  public void updateArticle(final Article article) {
    if (article == null || article.getObjectId() == null) throw new NullPointerException("Article ist null");
    if (this.articleCollection.replaceOne(eq(article.getObjectId()), article).getModifiedCount() == 0)
      this.articleCollection.insertOne(article);
  }

  @Override
  public Optional<List<Target>> getTargets() {
    final FindIterable<Target> targetFindIterable = this.targetCollection.find();
    return Optional.of(StreamSupport.stream(targetFindIterable.spliterator(), false).collect(Collectors.toList()));
  }

  @Override
  public Optional<List<Target>> getActiveTargets() {
    final FindIterable<Target> targetFindIterable = this.targetCollection.find(eq("active", true));
    return Optional.of(StreamSupport.stream(targetFindIterable.spliterator(), false).collect(Collectors.toList()));
  }

  @Override
  public Optional<Target> getTarget(final ObjectId objectId) {
    if (objectId == null) throw new NullPointerException("Id ist null");
    final FindIterable<Target> targetFindIterable = this.targetCollection.find(eq(objectId)).limit(1);
    return Optional.ofNullable(targetFindIterable.first());
  }

  @Override
  public void updateLastUrl(final Target target) {
    if (target == null || target.getObjectId() == null) throw new NullPointerException("Target ist null");
    this.targetCollection
        .updateOne(eq(target.getObjectId()), Updates.set("last_url", target.getLastUrl()));
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
