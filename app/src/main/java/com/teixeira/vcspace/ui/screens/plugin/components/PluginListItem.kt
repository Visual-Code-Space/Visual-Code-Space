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

package com.teixeira.vcspace.ui.screens.plugin.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import com.teixeira.vcspace.plugins.internal.PluginInfo
import com.teixeira.vcspace.resources.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PluginListItem(
  modifier: Modifier = Modifier,
  pluginInfo: PluginInfo,
  onLongClick: ((PluginInfo) -> Unit)? = null,
  onClick: ((PluginInfo) -> Unit)? = null,
  onEnabledOrDisabledCallback: (() -> Unit)? = null
) {
  val haptics = LocalHapticFeedback.current
  var enabled by remember { mutableStateOf(pluginInfo.enabled) }

  ElevatedCard(
    modifier = modifier
      .clip(CardDefaults.elevatedShape)
      .combinedClickable(
        onClick = {
          onClick?.invoke(pluginInfo)
        },
        onLongClick = {
          haptics.performHapticFeedback(HapticFeedbackType.LongPress)
          onLongClick?.invoke(pluginInfo)
        }
      )
  ) {
    ListItem(
      headlineContent = { Text(pluginInfo.name.toString()) },
      supportingContent = { Text(pluginInfo.description.toString()) },
      trailingContent = {
        Switch(
          checked = enabled,
          onCheckedChange = {
            enabled = it
            pluginInfo.enabled = enabled
            onEnabledOrDisabledCallback?.invoke()
          },
          thumbContent = if (enabled) {
            {
              Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                modifier = Modifier.size(SwitchDefaults.IconSize)
              )
            }
          } else null
        )
      },
      leadingContent = {
        Icon(
          painter = painterResource(R.drawable.ic_plugin),
          contentDescription = null
        )
      },
      colors = ListItemDefaults.colors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer
      )
    )
  }
}