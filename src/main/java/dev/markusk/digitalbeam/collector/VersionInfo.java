package dev.markusk.digitalbeam.collector;

public class VersionInfo {

  public static final String VERSION;
  public static final String BUILD_DATE;
  public static final String BUILD_TIME;
  public static final boolean DEVELOPMENT_BUILD;

  private static final String VERSION_RAW = "@VERSION@";
  private static final String BUILD_DATE_RAW = "@DATE@";
  private static final String BUILD_TIME_RAW = "@TIME@";

  static {
    //noinspection ConstantConditions
    VERSION = VERSION_RAW.startsWith("@") ? "DEVELOPMENT" : VERSION_RAW;
    //noinspection ConstantConditions
    BUILD_DATE = BUILD_DATE_RAW.startsWith("@") ? "DEBUG_BUILD" : BUILD_DATE_RAW;
    //noinspection ConstantConditions
    BUILD_TIME = BUILD_TIME_RAW.startsWith("@") ? "DEBUG_BUILD" : BUILD_TIME_RAW;
    //noinspection ConstantConditions
    DEVELOPMENT_BUILD = VERSION_RAW.startsWith("@") || VERSION_RAW.endsWith("-SNAPSHOT");
  }

}
