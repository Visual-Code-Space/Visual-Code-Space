package com.raredev.vcspace.util;

import android.util.Log;
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

  public static void error(String tag, String message, Throwable e) {
    log(Priority.ERROR, tag, message + "\n" + Log.getStackTraceString(e));
  }

  public static void error(String tag, Throwable e) {
    log(Priority.ERROR, tag, Log.getStackTraceString(e));
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

  public static void a(String tag, String message) {
    log(Priority.ASSERT, tag, message);
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
      observer.onLogUpdated(logFile);
    }
  }

  public enum Priority {
    DEBUG,
    WARNING,
    ERROR,
    INFO,
    VERBOSE,
    ASSERT;
  }

  public interface Observer {
    void onLogUpdated(File logFile);
  }
}
