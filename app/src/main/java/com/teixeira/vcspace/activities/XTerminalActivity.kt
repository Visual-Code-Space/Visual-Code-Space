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

package com.teixeira.vcspace.activities

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LifecycleStartEffect
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.ThreadUtils
import com.teixeira.vcspace.activities.TerminalActivity.Companion.KEY_PYTHON_FILE_PATH
import com.teixeira.vcspace.activities.TerminalActivity.Companion.KEY_WORKING_DIRECTORY
import com.teixeira.vcspace.activities.base.BaseComposeActivity
import com.teixeira.vcspace.terminal.Terminal
import com.teixeira.vcspace.terminal.service.TerminalService
import com.teixeira.vcspace.utils.TerminalPythonCommands
import com.termux.view.TerminalView

class XTerminalActivity : BaseComposeActivity() {
  var terminalBinder: TerminalService.TerminalBinder? = null
  var isBound by mutableStateOf(false)

  private val serviceConnection = object : ServiceConnection {
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
      terminalBinder = service as TerminalService.TerminalBinder
      isBound = true
    }

    override fun onServiceDisconnected(name: ComponentName?) {
      terminalBinder = null
      isBound = false
    }
  }

  val workingDirectory: String
    get() {
      val extras = intent.extras
      if (extras != null && extras.containsKey(KEY_WORKING_DIRECTORY)) {
        val directory = extras.getString(KEY_WORKING_DIRECTORY, null)
        return if (directory != null && directory.trim { it <= ' ' }.isNotEmpty()) directory
        else PathUtils.getRootPathExternalFirst()
      }
      return PathUtils.getRootPathExternalFirst()
    }

  @Composable
  override fun MainScreen() {
    LifecycleStartEffect(key1 = Unit, lifecycleOwner = this) {
      Intent(this@XTerminalActivity, TerminalService::class.java).also { intent ->
        bindService(
          intent,
          serviceConnection,
          Context.BIND_AUTO_CREATE
        )
      }

      onStopOrDispose {
        if (isBound) {
          unbindService(serviceConnection)
          isBound = false
        }
      }
    }

    Surface(
      modifier = Modifier
        .fillMaxSize()
        .systemBarsPadding()
    ) {
      if (isBound) {
        Terminal(terminalActivity = this)
      } else {
        Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.Center
        ) {
          CircularProgressIndicator()
        }
      }
    }
  }

  fun compilePython(terminal: TerminalView) {
    val filePath = intent?.extras?.getString(KEY_PYTHON_FILE_PATH, null) ?: return
    if (filePath.trim { it <= ' ' }.isNotEmpty()) {
      ThreadUtils.getMainHandler().post {
        terminal.mTermSession.write(
          "${TerminalPythonCommands.getInterpreterCommand(this, filePath)}\r"
        )
      }
    }
  }
}

