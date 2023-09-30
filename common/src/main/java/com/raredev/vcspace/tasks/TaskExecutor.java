package com.raredev.vcspace.tasks;

import com.blankj.utilcode.util.ThreadUtils;
import com.raredev.vcspace.callback.PushCallback;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class TaskExecutor {

  public static <R> CompletableFuture executeAsync(Callable<R> callable, PushCallback<R> callback) {
    return CompletableFuture.supplyAsync(
            () -> {
              try {
                return callable.call();
              } catch (Throwable throwable) {
                return null;
              }
            })
        .whenComplete(
            (result, throwable) -> {
              ThreadUtils.runOnUiThread(() -> callback.onComplete(result));
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

  public interface CallbackWithError<R> {
    void complete(R result, Throwable throwable);
  }
}
