/*
 * This file is part of Visual Code Space.
 *
 * Visual Code Space is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Visual Code Space is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Visual Code Space.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package com.teixeira.vcspace.utils

import android.content.Context
import com.blankj.utilcode.util.ThreadUtils.runOnUiThread
import com.teixeira.vcspace.dialogs.ProgressDialogBuilder
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Calls [CoroutineScope.cancel] only if a job is active in the scope.
 *
 * @param message Optional message describing the cause of the cancellation.
 * @param cause Optional cause of the cancellation.
 * @see cancelIfActive
 * @author Akash Yadav
 */
fun CoroutineScope.cancelIfActive(message: String, cause: Throwable? = null) =
  cancelIfActive(CancellationException(message, cause))

/**
 * Calls [CoroutineScope.cancel] only if a job is active in the scope.
 *
 * @param exception Optional cause of the cancellation.
 * @author Akash Yadav
 */
fun CoroutineScope.cancelIfActive(exception: CancellationException? = null) {
  val job = coroutineContext[Job]
  job?.cancel(exception)
}

/**
 * Launches a new coroutine without blocking the current thread. This method displays a progress
 * dialog while the [action] is executing. The dialog is automatically dismissed after the action
 * completes, regardless of whether it fails or succeeds.
 *
 * @param uiContext The context of the activity or fragment to show the dialog.
 * @param context The coroutine context [EmptyCoroutineContext] is empty by default.
 * @param configureBuilder Function to configure the progress dialog builder.
 * @param invokeOnCompletion The function is called when the [action] completes, either successfully
 *   or with an error.
 * @param action Function of the action to be executed when launching the coroutine.
 */
inline fun CoroutineScope.launchWithProgressDialog(
  uiContext: Context,
  context: CoroutineContext = EmptyCoroutineContext,
  configureBuilder: (builder: ProgressDialogBuilder) -> Unit = {},
  crossinline invokeOnCompletion: (throwable: Throwable?) -> Unit = {},
  crossinline action: suspend CoroutineScope.(builder: ProgressDialogBuilder) -> Unit,
): Job {

  val builder = ProgressDialogBuilder(uiContext)
  configureBuilder(builder)

  runOnUiThread { builder.show() }

  return launch(context) { action(builder) }
    .also { job ->
      job.invokeOnCompletion { throwable ->
        runOnUiThread { builder.dismiss() }
        invokeOnCompletion(throwable)
      }
    }
}
