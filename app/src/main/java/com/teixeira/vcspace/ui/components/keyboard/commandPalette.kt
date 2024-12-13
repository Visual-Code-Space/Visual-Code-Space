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

package com.teixeira.vcspace.ui.components.keyboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.teixeira.vcspace.keyboard.model.Command
import com.teixeira.vcspace.keyboard.CommandPaletteManager
import com.teixeira.vcspace.ui.extensions.harmonizeWithPrimary

@Composable
fun CommandPalette(
  commands: List<Command>,
  recentlyUsedCommands: List<Command>,
  modifier: Modifier = Modifier,
  onCommandSelected: (Command) -> Unit = {},
  onDismissRequest: () -> Unit
) {
  val focusRequester = remember { FocusRequester() }

  // Combine recently used commands with the rest of the commands, removing duplicates
  val sortedCommands = remember {
    val allCommands = recentlyUsedCommands + commands.filter {
      it !in recentlyUsedCommands
    }.toMutableList().apply { sortBy { it.name.lowercase() } }

    allCommands
  }

  val currentCompositonContext = rememberCompositionContext()

  Popup(
    onDismissRequest = onDismissRequest,
    properties = PopupProperties(
      focusable = true,
      dismissOnBackPress = true,
      dismissOnClickOutside = true,
      excludeFromSystemGesture = true
    ),
    alignment = Alignment.TopCenter
  ) {
    ElevatedCard(
      modifier = modifier
        .fillMaxWidth(0.9f)
        .wrapContentHeight()
        .imePadding()
        .heightIn(min = 100.dp, max = 500.dp)
    ) {
      var searchQuery by remember { mutableStateOf("") }

      TextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        modifier = Modifier
          .fillMaxWidth()
          .focusable()
          .focusRequester(focusRequester),
        placeholder = { Text("Type command") }
      )

      val filteredCommands = sortedCommands.filter {
        it.name.contains(searchQuery, ignoreCase = true)
      }

      LazyColumn(
        modifier = Modifier.padding(3.dp)
      ) {
        items(filteredCommands) { command ->
          val isRecentlyUsed = command in recentlyUsedCommands

          CommandItem(command, isRecentlyUsed = isRecentlyUsed) {
            CommandPaletteManager.instance.addRecentlyUsedCommand(command)

            it.action(command, currentCompositonContext)
            onCommandSelected(it)
          }
        }
      }
    }
  }
}

@Composable
private fun LazyItemScope.CommandItem(
  command: Command,
  isRecentlyUsed: Boolean = false,
  onCommandSelected: (Command) -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .wrapContentHeight()
      .clip(CardDefaults.elevatedShape)
      .clickable { onCommandSelected(command) }
  ) {
    Row(
      modifier = Modifier.padding(5.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = command.name,
        modifier = if (isRecentlyUsed) Modifier else Modifier
          .fillMaxWidth()
          .weight(1f),
        fontFamily = FontFamily.SansSerif,
        fontSize = 14.sp
      )

      if (isRecentlyUsed) {
        Text(
          text = "Recently used",
          modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp)
            .weight(1f),
          fontSize = 9.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant.harmonizeWithPrimary(0.6f)
        )
      }

      Text(
        text = command.keybinding,
        color = Color(0xFF1369FF).harmonizeWithPrimary(fraction = 0.5f),
        fontSize = 11.sp,
        fontFamily = FontFamily.Monospace
      )
    }
  }
}