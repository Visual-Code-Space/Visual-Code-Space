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

package com.raredev.vcspace.extensions

import com.blankj.utilcode.util.ThreadUtils.runOnUiThread
import com.raredev.vcspace.dialogs.ProgressDialogBuilder
import com.raredev.vcspace.utils.withActivity
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

fun CoroutineScope.cancelIfActive(message: String, cause: Throwable? = null) =
  cancelIfActive(CancellationException(message, cause))

fun CoroutineScope.cancelIfActive(exception: CancellationException? = null) {
  val job = coroutineContext[Job]
  job?.cancel(exception)
}

inline fun CoroutineScope.launchWithProgressDialog(
  context: CoroutineContext = EmptyCoroutineContext,
  configureBuilder: (builder: ProgressDialogBuilder) -> Unit = {},
  crossinline invokeOnCompletion: (throwable: Throwable?) -> Unit = {},
  crossinline action: suspend CoroutineScope.(builder: ProgressDialogBuilder) -> Unit
): Job {

  val builder = withActivity { ProgressDialogBuilder(this) }
  configureBuilder(builder)

  val dialog = builder.show()
  return launch(context) { action(builder) }
    .also { job ->
      job.invokeOnCompletion { throwable ->
        runOnUiThread { dialog.dismiss() }
        invokeOnCompletion(throwable)
      }
    }
}
