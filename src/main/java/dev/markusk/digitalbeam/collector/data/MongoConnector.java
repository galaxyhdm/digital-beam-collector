package dev.markusk.digitalbeam.collector.data;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.connection.ClusterSettings;
import dev.markusk.digitalbeam.collector.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

public class MongoConnector {

  private static final Logger LOGGER = LogManager.getLogger();
  private static final String DEFAULT_CONNECTION_STRING = "mongodb://localhost";

  private final String connectionUrl;

  private MongoClient mongoClient;
  private MongoDatabase mongoDatabase;

  public MongoConnector() {
    this(DEFAULT_CONNECTION_STRING);
  }

  public MongoConnector(final String connectionUrl) {
    this.connectionUrl = connectionUrl;
  }

  public boolean connect() {
    if (this.isConnected()) return true;
    this.setupMongodbLogger();

    final CodecRegistry pojoCodecRegistry = this.getPojoCodecRegistry();
    final CodecRegistry codecRegistry =
        CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);

    final MongoClientSettings settings = this.getMongoClientSettings(codecRegistry);
    final MongoClientURI mongoClientURI = new MongoClientURI(this.connectionUrl);

    LOGGER.info(String.format("Connecting to mongodb... (%s)", this.getHostString(settings.getClusterSettings())));
    this.mongoClient = MongoClients.create(settings);
    this.mongoDatabase = this.mongoClient.getDatabase(
        Objects.requireNonNullElse(mongoClientURI.getDatabase(), "public"));
    return this.isConnected();
  }

  @NotNull
  private MongoClientSettings getMongoClientSettings(final CodecRegistry codecRegistry) {
    return MongoClientSettings.builder()
        .uuidRepresentation(UuidRepresentation.STANDARD)
        .codecRegistry(codecRegistry)
        .applyConnectionString(new ConnectionString(this.connectionUrl))
        .build();
  }

  @NotNull
  private CodecRegistry getPojoCodecRegistry() {
    return CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build());
  }

  public void disconnect() {
    this.mongoClient.close();
    this.mongoClient = null;
  }

  public MongoClient getMongoClient() {
    return this.mongoClient;
  }

  public MongoDatabase getDatabase() {
    return this.mongoDatabase;
  }

  public <TDocument> MongoCollection<TDocument> getCollection(String collectionName, Class<TDocument> documentClass) {
    Objects.requireNonNull(this.mongoDatabase);
    return this.mongoDatabase.getCollection(collectionName, documentClass);
  }

  public boolean isConnected() {
    return this.mongoClient != null;
  }

  private String getHostString(final ClusterSettings clusterSettings) {
    final StringBuilder stringBuilder = new StringBuilder();
    final List<ServerAddress> hosts = clusterSettings.getHosts();

    for (int i = 0; i < hosts.size(); i++) {
      final ServerAddress address = hosts.get(i);
      stringBuilder.append(String.format("%s:%s", address.getHost(), address.getPort()));
      if (i == (hosts.size() - 1)) continue;
      stringBuilder.append(", ");
    }
    return stringBuilder.toString();
  }

  private void setupMongodbLogger() {
    java.util.logging.Logger.getLogger("org.mongodb").setLevel(Environment.DEBUG ? Level.FINE : Level.OFF);
  }

}
