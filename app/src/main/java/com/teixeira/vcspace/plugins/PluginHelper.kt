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
package com.teixeira.vcspace.plugins

import android.os.Handler
import android.os.Looper
import com.blankj.utilcode.util.AppUtils
import java.io.File

// This file is for plugin use only.
class PluginHelper {
  private val handler = Handler(Looper.getMainLooper())

  fun runOnUiThread(runnable: Runnable) {
    handler.post(runnable)
  }

  fun runOnUiThreadDelayed(runnable: Runnable, delay: Long) {
    handler.postDelayed(runnable, delay)
  }

  fun getFileNameWithoutExtension(file: File) = file.nameWithoutExtension

  fun isAppInstalled(packageName: String) = AppUtils.isAppInstalled(packageName)
}
