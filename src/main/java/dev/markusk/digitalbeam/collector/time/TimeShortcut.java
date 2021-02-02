package dev.markusk.digitalbeam.collector.time;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Getter
@AllArgsConstructor
public enum TimeShortcut {

  DAY('d', Calendar.DAY_OF_MONTH),
  WEEK('w', Calendar.WEEK_OF_MONTH),
  MONTH('m', Calendar.MONTH),
  YEAR('y', Calendar.YEAR);

  private final char character;
  private final int indicator;

  public static TimeShortcut getShortcut(final Character value) {
    for (final TimeShortcut timeShortcut : values()) {
      if (Character.toLowerCase(value) == timeShortcut.getCharacter()) return timeShortcut;
    }
    return null;
  }

  public static List<Character> getShortcuts() {
    final List<Character> characters = new ArrayList<>();
    for (final TimeShortcut value : values()) {
      characters.add(value.getCharacter());
    }
    return characters;
  }

  /**
   * @return a string with all shortcuts in a string. Like dwmy
   */
  public static String getAsString() {
    final StringBuilder stringBuilder = new StringBuilder();
    for (final Character shortcut : getShortcuts()) {
      stringBuilder.append(shortcut);
    }
    return stringBuilder.toString();
  }

}
