package dev.markusk.digitalbeam.collector;

public class Environment {

  public static final boolean DEBUG = System.getenv("DEBUG") != null && Boolean.parseBoolean(System.getenv("DEBUG"));

  public static final int POOL_SIZE =
      System.getenv("POOL_SIZE") != null ? Integer.parseInt(System.getenv("POOL_SIZE")) : 2;

  public static final boolean SYNC_START =
      System.getenv("SYNC_START") != null && Boolean.parseBoolean(System.getenv("SYNC_START"));

  public static final String CONNECTION_URL =
      System.getenv("CONNECTION_URL") != null ? String.valueOf(System.getenv("CONNECTION_URL")) : "";

}
