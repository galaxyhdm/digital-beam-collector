package dev.markusk.digitalbeam.collector;

public class Environment {

  public static final boolean DEBUG = System.getenv("DEBUG") != null && Boolean.parseBoolean(System.getenv("DEBUG"));

  public static final int POOL_SIZE =
      System.getenv("POOL_SIZE") != null ? Integer.parseInt(System.getenv("POOL_SIZE")) : 1;

  public static final boolean SYNC_START =
      System.getenv("SYNC_START") != null && Boolean.parseBoolean(System.getenv("SYNC_START"));

  public static final String CONNECTION_URL =
      System.getenv("CONNECTION_URL") != null ? String.valueOf(System.getenv("CONNECTION_URL")) : "";

  public static final String CERT_URL =
      System.getenv("CERT_URL") != null ? String.valueOf(System.getenv("CERT_URL")) : "";

  public static final String PROXY_ADDRESS =
      System.getenv("PROXY_ADDRESS") != null ? String.valueOf(System.getenv("PROXY_ADDRESS")) : "";

  public static final int MAX_TRIES =
      System.getenv("MAX_TRIES") != null ? Integer.parseInt(System.getenv("MAX_TRIES")) : 3;

  public static final int LOOKUP_WAIT_TIME =
      System.getenv("LOOKUP_WAIT_TIME") != null ? Integer.parseInt(System.getenv("LOOKUP_WAIT_TIME")) : 15;
}
