package dev.markusk.digitalbeam.collector;

public class Environment {

  public static final boolean DEBUG = System.getenv("DEBUG") != null && Boolean.parseBoolean(System.getenv("DEBUG"));

}
