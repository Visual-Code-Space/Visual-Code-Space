package com.raredev.vcspace.util;

import android.util.Log;

public class ILogger {

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

  private static void log(Priority priority, String tag, String message) {
    switch (priority) {
      case DEBUG:
        Log.d(tag, message);
        break;
      case WARNING:
        Log.w(tag, message);
        break;
      case ERROR:
        Log.e(tag, message);
        break;
      case VERBOSE:
        Log.v(tag, message);
        break;
    }
  }

  public enum Priority {
    DEBUG,
    WARNING,
    ERROR,
    INFO,
    VERBOSE;
  }
}
