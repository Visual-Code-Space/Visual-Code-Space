package com.raredev.common.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ILogger {
  private static final String LOG_FILE_NAME = "vcspace.log";
  private static final String LOG_FILE_DIRECTORY = "/data/data/com.raredev.vcspace/files/";

  private static File logFile;
  private static BufferedWriter writer;
  private static Observer observer;
  
  static {
    initialize();
  }

  public static void initialize() {
    File logDirectory = new File(LOG_FILE_DIRECTORY);
    if (!logDirectory.exists()) {
      logDirectory.mkdirs();
    }
    logFile = new File(logDirectory, LOG_FILE_NAME);
    try {
      writer = new BufferedWriter(new FileWriter(logFile, true));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

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
    try {
      writer.close();
      if (logFile.exists()) {
        logFile.delete();
      }
      logFile.createNewFile();
      writer = new BufferedWriter(new FileWriter(logFile));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void log(Priority priority, String tag, String message) {
    StringBuilder logEntry = new StringBuilder();
    String[] lines = message.split("\\r?\\n");
    for (String line : lines) {
      logEntry.append(String.format("%s [%s]: %s\n", priority.name(), tag, line));
    }
    try {
      writer.write(logEntry.toString());
      writer.flush();
      notifyObserver();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void notifyObserver() {
    if (observer != null) {
      try {
        BufferedReader reader = new BufferedReader(new FileReader(logFile));
        List<String> logs = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
          logs.add(line);
        }
        reader.close();
        observer.onLogUpdated(logs);
      } catch (IOException e) {
        e.printStackTrace();
      }
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
