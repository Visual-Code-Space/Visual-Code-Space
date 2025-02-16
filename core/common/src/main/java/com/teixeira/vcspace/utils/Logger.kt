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

import android.util.Log
import com.teixeira.vcspace.common.BuildConfig
import com.teixeira.vcspace.extensions.doIf
import java.util.WeakHashMap

class Logger private constructor(private val tag: String) {
    fun d(message: String) {
        doIf(BuildConfig.DEBUG) {
            log(Priority.DEBUG, tag, message)
        }
    }

    fun d(message: String, vararg format: Any) {
        doIf(BuildConfig.DEBUG) {
            log(Priority.DEBUG, tag, String.format(message, *format))
        }
    }

    fun w(message: String) {
        log(Priority.WARNING, tag, message)
    }

    fun w(message: String, vararg format: Any) {
        log(Priority.WARNING, tag, String.format(message, *format))
    }

    fun e(message: String) {
        log(Priority.ERROR, tag, message)
    }

    fun e(message: String, vararg format: Any) {
        log(Priority.ERROR, tag, String.format(message, *format))
    }

    fun e(message: String, e: Throwable) {
        log(Priority.ERROR, tag, "$message\n${Log.getStackTraceString(e)}")
    }

    fun e(e: Throwable) {
        log(Priority.ERROR, tag, Log.getStackTraceString(e))
    }

    fun i(message: String) {
        log(Priority.INFO, tag, message)
    }

    fun i(message: String, vararg format: Any) {
        log(Priority.INFO, tag, String.format(message, *format))
    }

    fun v(message: String) {
        log(Priority.VERBOSE, tag, message)
    }

    fun v(message: String, vararg format: Any) {
        log(Priority.VERBOSE, tag, String.format(message, *format))
    }

    private fun log(priority: Priority, tag: String, message: String) {
        when (priority) {
            Priority.DEBUG -> Log.d(tag, message)
            Priority.WARNING -> Log.w(tag, message)
            Priority.ERROR -> Log.e(tag, message)
            Priority.VERBOSE -> Log.v(tag, message)
            Priority.INFO -> Log.i(tag, message)
        }
    }

    enum class Priority {
        DEBUG,
        WARNING,
        ERROR,
        INFO,
        VERBOSE,
    }

    companion object {
        private val map: MutableMap<String, Logger> = WeakHashMap()

        val newInstance: (String) -> Logger by lazy {
            { tag: String -> map[tag] ?: Logger(tag).also { map[tag] = it } }
        }
    }
}
