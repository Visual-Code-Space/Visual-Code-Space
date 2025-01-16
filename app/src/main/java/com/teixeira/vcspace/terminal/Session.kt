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

package com.teixeira.vcspace.terminal

import android.content.Context
import com.termux.terminal.TerminalEmulator
import com.termux.terminal.TerminalSession
import com.termux.terminal.TerminalSessionClient

// https://github.com/RohitKushvaha01/ReTerminal/blob/main/app/src/main/java/com/rk/terminal/terminal/MkSession.kt
object Session {
  fun createSession(
    context: Context,
    sessionClient: TerminalSessionClient,
    cwd: String?
  ): TerminalSession {
    with(context) {
      val envVariables = mapOf(
        "ANDROID_ART_ROOT" to System.getenv("ANDROID_ART_ROOT"),
        "ANDROID_DATA" to System.getenv("ANDROID_DATA"),
        "ANDROID_I18N_ROOT" to System.getenv("ANDROID_I18N_ROOT"),
        "ANDROID_ROOT" to System.getenv("ANDROID_ROOT"),
        "ANDROID_RUNTIME_ROOT" to System.getenv("ANDROID_RUNTIME_ROOT"),
        "ANDROID_TZDATA_ROOT" to System.getenv("ANDROID_TZDATA_ROOT"),
        "BOOTCLASSPATH" to System.getenv("BOOTCLASSPATH"),
        "DEX2OATBOOTCLASSPATH" to System.getenv("DEX2OATBOOTCLASSPATH"),
        "EXTERNAL_STORAGE" to System.getenv("EXTERNAL_STORAGE")
      )

      val env = mutableListOf(
        "HOME=" + filesDir.absolutePath,
        "PUBLIC_HOME=" + getExternalFilesDir(null)?.absolutePath,
        "COLORTERM=truecolor",
        "TERM=xterm-256color",
      )

      env.addAll(envVariables.map { "${it.key}=${it.value}" })

      val shell = "/system/bin/sh"
      val args = arrayOf<String>()

      return TerminalSession(
        shell,
        cwd ?: filesDir.absolutePath,
        args,
        env.toTypedArray(),
        TerminalEmulator.DEFAULT_TERMINAL_TRANSCRIPT_ROWS,
        sessionClient
      )
    }
  }
}
