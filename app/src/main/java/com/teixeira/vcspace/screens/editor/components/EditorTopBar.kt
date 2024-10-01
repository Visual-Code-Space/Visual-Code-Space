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

package com.teixeira.vcspace.screens.editor.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.teixeira.vcspace.resources.R.string
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorTopBar(
  modifier: Modifier = Modifier,
  drawerState: DrawerState
) {
  val scope = rememberCoroutineScope()

  TopAppBar(
    modifier = modifier,
    title = {
      Text(stringResource(string.app_name))
    },
    navigationIcon = {
      TooltipBox(
        tooltip = { RichTooltip { Text(stringResource(string.open_drawer)) } },
        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
        state = rememberTooltipState()
      ) {
        IconButton(onClick = {
          scope.launch {
            drawerState.apply {
              if (isOpen) close() else open()
            }
          }
        }) {
          Icon(
            imageVector = Icons.Rounded.Menu,
            contentDescription = stringResource(string.open_drawer)
          )
        }
      }
    }
  )
}