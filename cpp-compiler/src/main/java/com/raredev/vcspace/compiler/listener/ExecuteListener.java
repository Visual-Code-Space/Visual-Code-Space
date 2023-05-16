package com.raredev.vcspace.compiler.listener;

public interface ExecuteListener {
  /** Program created successfully */
  void onExecuteStart(Process process);

  /** Error output */
  void printStderr(final Throwable error);

  /** Normal output */
  void printStdout(final CharSequence out);

  /**
   * Execution completed
   *
   * @param waitFor
   * @param i
   */
  void onExecuteFinish(int waitFor, int exitValue);
}
