package dev.markusk.digitalbeam.collector;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.IOException;

public class Launcher {

  public Launcher(final OptionSet optionSet) {
    System.out.println("\n" +
        "\n" +
        "      _ _       _ _        _        _                                         _ _           _             \n" +
        "     | (_)     (_) |      | |      | |                                       | | |         | |            \n" +
        "   __| |_  __ _ _| |_ __ _| |______| |__   ___  __ _ _ __ ___ ______ ___ ___ | | | ___  ___| |_ ___  _ __ \n" +
        "  / _` | |/ _` | | __/ _` | |______| '_ \\ / _ \\/ _` | '_ ` _ \\______/ __/ _ \\| | |/ _ \\/ __| __/ _ \\| '__|\n" +
        " | (_| | | (_| | | || (_| | |      | |_) |  __/ (_| | | | | | |    | (_| (_) | | |  __/ (__| || (_) | |   \n" +
        "  \\__,_|_|\\__, |_|\\__\\__,_|_|      |_.__/ \\___|\\__,_|_| |_| |_|     \\___\\___/|_|_|\\___|\\___|\\__\\___/|_|   \n" +
        "           __/ |                                                                                          \n" +
        "          |___/                                                                                           \n" +
        "\n");

    this.setProperties();
    final Collector collector = new Collector(optionSet);
    collector.initialize();
  }

  public static void main(String[] args) {
    final OptionParser optionParser = createOptionParser();
    final OptionSet optionSet = optionParser.parse(args);
    if (optionSet.has("help")) {
      try {
        optionParser.printHelpOn(System.out);
      } catch (IOException e) {
        e.printStackTrace();
      }
      System.exit(-1);
    }
    new Launcher(optionSet);
  }

  private void setProperties() {
    System.setProperty("sentry.release", VersionInfo.VERSION);
    System.setProperty("sentry.environment", VersionInfo.DEBUG ? "development" : "production");
  }

  private static OptionParser createOptionParser() {
    final OptionParser optionParser = new OptionParser();
    optionParser.accepts("debug", "Enables the debug mode");
    optionParser.accepts("help", "See help");
    return optionParser;
  }

}
