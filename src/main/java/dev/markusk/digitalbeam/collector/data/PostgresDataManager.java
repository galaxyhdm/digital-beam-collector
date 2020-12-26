package dev.markusk.digitalbeam.collector.data;

import dev.markusk.digitalbeam.collector.Environment;
import dev.markusk.digitalbeam.collector.VersionInfo;
import dev.markusk.digitalbeam.collector.model.Target;
import dev.markusk.digitalbeam.collector.model.builder.TargetBuilder;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.apache.logging.log4j.Logger;
import org.postgresql.ds.PGConnectionPoolDataSource;

import javax.sql.PooledConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;

public class PostgresDataManager implements AbstractDataManager {

  private static final String ACCEPTED_DATABASE = "postgresql";
  private final static String SCHEMA_FILE = "schema/db.changelog-main.xml";

  private Logger logger;
  private PGConnectionPoolDataSource dataSource;
  private PooledConnection pooledConnection;

  @Override
  public boolean initialize(final Logger logger, final String connectionUrl) {
    if (connectionUrl == null || connectionUrl.isEmpty()) return false;
    this.logger = logger;
    this.logger.info("Connecting to postgres-database...");

    this.dataSource = new PGConnectionPoolDataSource();
    this.dataSource.setUrl(connectionUrl);

    try (final Connection connection = this.dataSource.getConnection()) {
      final String database = connection.getMetaData().getDatabaseProductName().toLowerCase();
      if (database.equalsIgnoreCase(ACCEPTED_DATABASE)) {
        this.logger.debug(String.format("Found supported database implementation: %s", database));
        this.pooledConnection = this.dataSource.getPooledConnection();
        this.updateDatabase();
        this.logger.info(String.format("Connected to sql-database. (Database: %s)", this.dataSource.getDatabaseName()));
        return true;
      } else {
        this.logger.warn(String.format("Database implementation %s is not supported!", database));
        return false;
      }
    } catch (Exception e) {
      this.logger.error("Error while pooledConnection to database", e);
      return false;
    }
  }

  @Override
  public Optional<List<Target>> getTargets() {
    final List<Target> targets = new ArrayList<>();
    try (final Connection connection = this.pooledConnection.getConnection()) {
      try (final PreparedStatement preparedStatement = connection.prepareStatement(Queries.SELECT_TARGETS)) {
        final ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
          targets.add(getTargetFromResult(resultSet));
        }
        return Optional.of(targets);
      }
    } catch (SQLException exception) {
      this.logger.error("Error while executing getTargets", exception);
    }
    return Optional.empty();
  }

  private Target getTargetFromResult(final ResultSet resultSet) throws SQLException {
    final TargetBuilder targetBuilder = new TargetBuilder();
    targetBuilder.setSnowflake(resultSet.getString("target_snowflake"));
    targetBuilder.setName(resultSet.getString("name"));
    targetBuilder.setShortname(resultSet.getString("shortname"));
    targetBuilder.setFetchUrl(resultSet.getString("fetch_url"));
    targetBuilder.setTor(resultSet.getBoolean("tor"));
    targetBuilder.setWaitTime(resultSet.getInt("wait_time"));
    return targetBuilder.createTarget();
  }

  private void updateDatabase() {
    final liquibase.database.Database implementation;
    try (final Connection connection = dataSource.getConnection()) {
      implementation = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
      this.logger.debug("Updating database...");
      this.liquibaseUpdate(implementation);
    } catch (final SQLException | DatabaseException ex) {
      this.logger.error("Error in updateDatabase", ex);
    }
  }

  private void liquibaseUpdate(final liquibase.database.Database implementation) {
    Objects.requireNonNull(implementation, "Implementation is null!");
    try (final Liquibase liquibase = new Liquibase(SCHEMA_FILE, new ClassLoaderResourceAccessor(), implementation)) {
      java.util.logging.Logger logger = java.util.logging.Logger.getLogger("liquibase");
      logger.setLevel(Environment.DEBUG || VersionInfo.DEBUG ? Level.INFO : Level.SEVERE);
      liquibase.update("");
    } catch (Exception ex) {
      this.logger.error("Error in liquibaseUpdate", ex);
    }
  }

  @Override
  public void close() {
    try {
      if (this.pooledConnection != null)
        this.pooledConnection.close();
    } catch (SQLException e) {
      this.logger.error("Error while closing connection", e);
    }
    this.dataSource = null;
    this.logger.info("SqlDataSource closed!");
  }

}
