package dev.markusk.digitalbeam.collector.time;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OffsetCalculator {

  private static final String SELECT_REGEX;
  private static final Pattern SELECT_PATTERN;

  static {
    SELECT_REGEX = String.format("(?i)^[+]?(\\d{1,5})([%s])$", TimeShortcut.getAsString());
    SELECT_PATTERN = Pattern.compile(SELECT_REGEX);
  }

  public static Date calculate(final String input, Date referenceDate) {
    final Matcher matcher = SELECT_PATTERN.matcher(input);
    if (!matcher.matches()) return null;
    final TimeShortcut shortcut = TimeShortcut.getShortcut(matcher.group(2).charAt(0));
    if (shortcut == null) return null;
    final int multiplier = Integer.parseInt(matcher.group(1));

    final Calendar instance = getCalendar(referenceDate);
    instance.add(shortcut.getIndicator(), multiplier);

    return instance.getTime();
  }

  public static long calculateDistance(final Date referenceDate, final Date lookupDate) {
    return Math.abs(lookupDate.getTime() - referenceDate.getTime());
  }

  private static Calendar getCalendar(final Date referenceDate) {
    final Calendar instance = Calendar.getInstance(TimeZone.getTimeZone("Z"));
    instance.setTime(referenceDate);
    return instance;
  }

}
