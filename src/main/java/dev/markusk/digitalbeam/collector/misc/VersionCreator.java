package dev.markusk.digitalbeam.collector.misc;

import dev.markusk.digitalbeam.collector.data.DataProvider;
import dev.markusk.digitalbeam.collector.model.Article;
import dev.markusk.digitalbeam.collector.model.Version;
import dev.markusk.digitalbeam.collector.model.VersionStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class VersionCreator {

  private static final Logger LOGGER = LogManager.getLogger();

  private final Article article;

  private Date updateTime;
  private long offset;

  private VersionCreator(final Article article) {
    this.article = article;
  }

  public static VersionCreator builder(final Article article) {
    return new VersionCreator(article);
  }

  public VersionCreator setUpdateTime(final Date updateTime) {
    this.updateTime = updateTime;
    return this;
  }

  public VersionCreator setOffset(final long offset) {
    this.offset = Math.abs(offset);
    return this;
  }

  public void updateOrInsertArticle(final DataProvider dataProvider) {
    final Optional<Article> articleById = dataProvider.getArticleById(this.article.getArticleId());
    articleById.ifPresentOrElse(
        persistentArticle -> this.performPersistentUpdate(dataProvider, persistentArticle),
        () -> this.performPersistentUpdate(dataProvider));
    articleById.ifPresentOrElse(
        data -> LOGGER.debug(String.format("Present in database (%s), updating versions", data.getObjectId())),
        () -> LOGGER.debug(String.format("Not present in database, inserting article (%s)", article.getObjectId())));
  }

  public void performPersistentUpdate(final DataProvider dataProvider) {
    this.performPersistentUpdate(dataProvider, this.article);
  }

  private void performPersistentUpdate(final DataProvider dataProvider, final Article article) {
    article.setVersions(this.updateVersionList(article));
    dataProvider.updateArticle(article);
  }

  private List<Version> updateVersionList(final Article article) {
    final List<Version> versions =
        article.getVersions() != null ? new ArrayList<>(article.getVersions()) : new ArrayList<>();
    versions.add(this.createVersion(versions.size() != 0 ? versions.get(versions.size() - 1) : null));
    return versions;
  }

  private Version createVersion(final Version previousVersion) {
    final Version version = new Version();
    version.setObjectId(new ObjectId());
    version.setVersion(previousVersion != null ? previousVersion.getVersion() + 1 : 0);
    version.setUpdateTime(this.updateTime != null ? this.updateTime : new Date());
    version.setOffset(this.offset);
    version.setStatus(VersionStatus.COLLECTED);
    return version;
  }

}
