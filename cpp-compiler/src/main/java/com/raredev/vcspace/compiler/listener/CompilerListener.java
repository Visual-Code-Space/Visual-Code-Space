package com.raredev.vcspace.compiler.listener;

public abstract class CompilerListener {
  /**
   * Compile to class file successfully
   *
   * @param path Compiled class file path
   */
  public abstract void onSuccess(String path);

  /**
   * Compilation failed
   *
   * @param error
   */
  public abstract void onError(Throwable error);

  /**
   * Compilation progress
   *
   * @param task task name
   * @param progress
   */
  public void onProgress(String task, int progress) {}
}
