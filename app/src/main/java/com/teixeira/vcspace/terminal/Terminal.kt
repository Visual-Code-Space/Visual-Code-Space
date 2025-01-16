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

import android.content.Intent
import android.graphics.Typeface
import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.teixeira.vcspace.activities.XTerminalActivity
import com.teixeira.vcspace.terminal.service.TerminalService
import com.teixeira.vcspace.ui.virtualkeys.VirtualKeysConstants
import com.teixeira.vcspace.ui.virtualkeys.VirtualKeysInfo
import com.teixeira.vcspace.ui.virtualkeys.VirtualKeysListener
import com.teixeira.vcspace.ui.virtualkeys.VirtualKeysView
import com.termux.view.TerminalView
import java.lang.ref.WeakReference

// https://github.com/RohitKushvaha01/ReTerminal/blob/main/app/src/main/java/com/rk/terminal/terminal/Terminal.kt

private var terminalView = WeakReference<TerminalView?>(null)
var virtualKeysId = View.generateViewId()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Terminal(modifier: Modifier = Modifier, terminalActivity: XTerminalActivity) {
  val backgroundColor = MaterialTheme.colorScheme.surface.toArgb()
  val foregroundColor = MaterialTheme.colorScheme.onSurface.toArgb()
  val context = LocalContext.current

  LaunchedEffect(Unit) {
    context.startService(Intent(context, TerminalService::class.java))
  }

  Box(modifier = Modifier.imePadding()) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
      drawerState = drawerState,
      gesturesEnabled = drawerState.isOpen,
      drawerContent = {
        ModalDrawerSheet {

        }
      },
      content = {
        Scaffold(topBar = {
          TopAppBar(
            title = { Text(text = "Terminal") },
//            navigationIcon = {
//              IconButton(onClick = {
//                scope.launch { drawerState.open() }
//              }) {
//                Icon(Icons.Default.Menu, null)
//              }
//            }
          )
        }) { paddingValues ->
          Column(modifier = Modifier.padding(paddingValues)) {
            AndroidView(
              factory = { context ->
                TerminalView(context, null).apply {
                  terminalView = WeakReference(this)
                  val client = TerminalBackend(this, terminalActivity)
                  setTextSize(23)
                  setTerminalViewClient(client)
                  val session = Session.createSession(
                    terminalActivity,
                    client,
                    null
                  )
                  session.updateTerminalSessionClient(client)
                  attachSession(session)
                  setTypeface(
                    Typeface.createFromAsset(
                      context.assets,
                      "fonts/JetBrainsMono-Regular.ttf"
                    )
                  )

                  post {
                    session.write("export PS1='\$(pwd | sed \"s|^\$HOME|~|\")\\\$ '\r")
                    session.write("cd && clear\r")
                    terminalActivity.compilePython(terminal = this)
                    setBackgroundColor(backgroundColor)
                    keepScreenOn = true
                    requestFocus()
                    setFocusableInTouchMode(true)

                    mEmulator?.mColors?.mCurrentColors?.apply {
                      set(256, foregroundColor)
                      set(258, foregroundColor)
                    }
                  }
                }
              },
              modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
              update = { terminalView -> terminalView.onScreenUpdated() },
            )

            AndroidView(
              factory = { context ->
                VirtualKeysView(context, null).apply {
                  id = virtualKeysId
                  virtualKeysViewClient =
                    terminalView.get()?.mTermSession?.let { VirtualKeysListener(it) }
                  buttonTextColor = foregroundColor
                  setBackgroundColor(backgroundColor)

                  reload(
                    VirtualKeysInfo(
                      VIRTUAL_KEYS, "", VirtualKeysConstants.CONTROL_CHARS_ALIASES
                    )
                  )
                }
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(75.dp)
            )
          }
        }
      }
    )
  }
}

const val VIRTUAL_KEYS =
  ("[" +
    "\n  [" +
    "\n    \"ESC\"," +
    "\n    {" +
    "\n      \"key\": \"/\"," +
    "\n      \"popup\": \"\\\\\"" +
    "\n    }," +
    "\n    {" +
    "\n      \"key\": \"-\"," +
    "\n      \"popup\": \"|\"" +
    "\n    }," +
    "\n    \"HOME\"," +
    "\n    \"UP\"," +
    "\n    \"END\"," +
    "\n    \"PGUP\"" +
    "\n  ]," +
    "\n  [" +
    "\n    \"TAB\"," +
    "\n    \"CTRL\"," +
    "\n    \"ALT\"," +
    "\n    \"LEFT\"," +
    "\n    \"DOWN\"," +
    "\n    \"RIGHT\"," +
    "\n    \"PGDN\"" +
    "\n  ]" +
    "\n]")
