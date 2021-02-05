package dev.markusk.digitalbeam.collector.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public enum VersionStatus {

  COLLECTED(0, 5),
  DOWNLOADING(5, 10),
  DOWNLOADED(10, 15),
  ANALYZING(15, 20),
  ANALYZED(20, -1),
  FAILED(99, -1);

  private final int id;
  private final int nextId;

  public static VersionStatus findById(final int id) {
    for (final VersionStatus value : values()) {
      if (value.getId() == id) return value;
    }
    return null;
  }

  public VersionStatus nextStatus() {
    final int nextId = this.getNextId();
    if (nextId == -1) return null;
    return findById(nextId);
  }

}
