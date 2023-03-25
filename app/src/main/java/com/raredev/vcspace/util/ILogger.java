package com.raredev.vcspace.util;

import java.util.ArrayList;
import java.util.List;

public class ILogger {
  private static List<String> cachedLogs = new ArrayList<>();
  private static Observer observer;

  public static void addObserver(Observer obs) {
    observer = obs;

    notifyObserver();
  }

  public static void debug(String tag, String message) {
    log(Priority.DEBUG, tag, message);
  }

  public static void warning(String tag, String message) {
    log(Priority.WARNING, tag, message);
  }

  public static void error(String tag, String message) {
    log(Priority.ERROR, tag, message);
  }

  public static void info(String tag, String message) {
    log(Priority.INFO, tag, message);
  }

  public static void verbose(String tag, String message) {
    log(Priority.VERBOSE, tag, message);
  }

  public static void clear() {
    cachedLogs.clear();
  }

  private static void log(Priority priority, String tag, String message) {
    String[] lines = message.split("\\r?\\n");
    for (String line : lines) {
      cachedLogs.add("[" + priority + "] [" + tag + "] " + line);
    }
    notifyObserver();
  }

  private static void notifyObserver() {
    if (cachedLogs.size() > 100) {
      cachedLogs.remove(0);
    }
    if (observer != null) {
      observer.onLogUpdated(cachedLogs);
    }
  }

  public enum Priority {
    DEBUG,
    WARNING,
    ERROR,
    INFO,
    VERBOSE;
  }

  public interface Observer {
    void onLogUpdated(List<String> logs);
  }
}
