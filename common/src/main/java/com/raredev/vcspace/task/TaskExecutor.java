package com.raredev.vcspace.task;

import com.blankj.utilcode.util.ThreadUtils;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class TaskExecutor {

  public static <R> CompletableFuture executeAsync(Callable<R> callable, Callback callback) {
    return CompletableFuture.supplyAsync(
            () -> {
              try {
                return callable.call();
              } catch (Throwable throwable) {
                return false;
              }
            })
        .whenComplete(
            (result, throwable) -> {
              ThreadUtils.runOnUiThread(() -> callback.complete(result));
            });
  }

  public static <R> CompletableFuture<R> executeAsyncProvideError(Callable<R> callable) {
    return executeAsyncProvideError(callable, (result, throwable) -> {});
  }

  public static <R> CompletableFuture<R> executeAsyncProvideError(
      Callable<R> callable, CallbackWithError<R> callback) {
    return CompletableFuture.supplyAsync(
            () -> {
              try {
                return callable.call();
              } catch (Throwable throwable) {
                throw new CompletionException(throwable);
              }
            })
        .whenComplete(
            (result, throwable) -> {
              ThreadUtils.runOnUiThread(() -> callback.complete(result, throwable));
            });
  }

  public interface Callback<R> {
    void complete(R result);
  }

  public interface CallbackWithError<R> {
    void complete(R result, Throwable throwable);
  }
}
