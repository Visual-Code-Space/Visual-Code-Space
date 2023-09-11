package com.raredev.vcspace.utils;

import android.util.Log;
import java.util.Map;
import java.util.WeakHashMap;

public class Logger {

  private static final Map<String, Logger> map = new WeakHashMap<>();
  private final String tag;

  private Logger(String tag) {
    this.tag = tag;
  }

  public static synchronized Logger newInstance(String tag) {
    var logger = map.get(tag);
    if (logger == null) {
      logger = new Logger(tag);
      map.put(tag, logger);
    }
    return logger;
  }

  public void d(String message) {
    log(Priority.DEBUG, tag, message);
  }

  public void w(String message) {
    log(Priority.WARNING, tag, message);
  }

  public void e(String message, Throwable e) {
    log(Priority.ERROR, tag, message + "\n" + Log.getStackTraceString(e));
  }

  public void e(Throwable e) {
    log(Priority.ERROR, tag, Log.getStackTraceString(e));
  }

  public void e(String message) {
    log(Priority.ERROR, tag, message);
  }

  public void i(String message) {
    log(Priority.INFO, tag, message);
  }

  public void v(String message) {
    log(Priority.VERBOSE, tag, message);
  }

  private void log(Priority priority, String tag, String message) {
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
