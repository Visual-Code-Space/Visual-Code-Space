/*
 *  This file is part of AndroidIDE.
 *
 *  AndroidIDE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  AndroidIDE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *   along with AndroidIDE.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.raredev.vcspace.tasks

import com.blankj.utilcode.util.ThreadUtils
import com.raredev.vcspace.utils.Logger
import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException

object TaskExecutor {
  private val log = Logger.newInstance("TaskExecutor")

  @JvmOverloads
  @JvmStatic
  fun <R> executeAsync(
    callable: Callable<R>,
    callback: (result: R?) -> Unit = {}
  ): CompletableFuture<R?> {
    return CompletableFuture.supplyAsync {
        try {
          return@supplyAsync callable.call()
        } catch (th: Throwable) {
          log.e("An error occurred while executing Callable in background thread.", th)
          return@supplyAsync null
        }
      }
      .whenComplete { result, _ -> ThreadUtils.runOnUiThread { callback(result) } }
  }

  @JvmOverloads
  @JvmStatic
  fun <R> executeAsyncProvideError(
    callable: Callable<R>,
    callback: (result: R?, error: Throwable?) -> Unit = { _, _ -> }
  ): CompletableFuture<R?> {
    return CompletableFuture.supplyAsync {
        try {
          return@supplyAsync callable.call()
        } catch (th: Throwable) {
          log.e("An error occurred while executing Callable in background thread.", th)
          throw CompletionException(th)
        }
      }
      .whenComplete { result, throwable ->
        ThreadUtils.runOnUiThread { callback(result, throwable) }
      }
  }
}
