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

import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricalServices
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.teixeira.vcspace.extensions.toFile
import com.teixeira.vcspace.plugins.internal.PluginInfo

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

  PluginCard(
    pluginInfo = pluginInfo,
    onClick = onClick,
    onLongClick = onLongClick,
    onEnabledOrDisabledCallback = onEnabledOrDisabledCallback
  )

//  ElevatedCard(
//    modifier = modifier
//      .clip(CardDefaults.elevatedShape)
//      .combinedClickable(
//        onClick = {
//          onClick?.invoke(pluginInfo)
//        },
//        onLongClick = {
//          haptics.performHapticFeedback(HapticFeedbackType.LongPress)
//          onLongClick?.invoke(pluginInfo)
//        }
//      )
//  ) {
//    ListItem(
//      headlineContent = { Text(pluginInfo.name.toString()) },
//      supportingContent = { Text(pluginInfo.description ?: "No description provided") },
//      trailingContent = {
//        Switch(
//          checked = enabled,
//          onCheckedChange = {
//            enabled = it
//            pluginInfo.enabled = enabled
//            onEnabledOrDisabledCallback?.invoke()
//          },
//          thumbContent = if (enabled) {
//            {
//              Icon(
//                imageVector = Icons.Rounded.Check,
//                contentDescription = null,
//                modifier = Modifier.size(SwitchDefaults.IconSize)
//              )
//            }
//          } else null
//        )
//      },
//      leadingContent = {
//        Icon(
//          painter = painterResource(R.drawable.ic_plugin),
//          contentDescription = null
//        )
//      },
//      colors = ListItemDefaults.colors(
//        containerColor = MaterialTheme.colorScheme.surfaceContainer
//      )
//    )
//  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PluginCard(
  pluginInfo: PluginInfo,
  onClick: ((PluginInfo) -> Unit)? = null,
  onLongClick: ((PluginInfo) -> Unit)? = null,
  onEnabledOrDisabledCallback: (() -> Unit)? = null
) {
  val haptics = LocalHapticFeedback.current

  var expanded by remember { mutableStateOf(false) }
  var enabled by remember { mutableStateOf(pluginInfo.enabled) }

  ElevatedCard(
    modifier = Modifier
      .fillMaxWidth()
      .clip(CardDefaults.elevatedShape)
      .combinedClickable(
        onClick = {
          expanded = !expanded
          onClick?.invoke(pluginInfo)
        },
        onLongClick = {
          haptics.performHapticFeedback(HapticFeedbackType.LongPress)
          onLongClick?.invoke(pluginInfo)
        },
        role = Role.Switch,
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
      ),
    elevation = CardDefaults.cardElevation(defaultElevation = if (expanded) 8.dp else 2.dp)
  ) {
    Column(Modifier.padding(16.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        if (pluginInfo.icon != null && pluginInfo.icon?.toFile()?.exists() == true) {
          Image(
            BitmapFactory.decodeFile(pluginInfo.icon).asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
              .size(32.dp)
              .padding(end = 12.dp)
              .clip(RoundedCornerShape(4.dp))
          )
        } else {
          Icon(
            imageVector = Icons.Filled.ElectricalServices,
            contentDescription = "info",
            modifier = Modifier.padding(end = 12.dp),
            tint = MaterialTheme.colorScheme.tertiary
          )
        }

        Column(Modifier.weight(1f)) {
          Text(
            text = pluginInfo.name ?: "Plugin name not set",
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = "v${pluginInfo.version}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
          )
        }

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
      }

      AnimatedVisibility(
        visible = expanded,
        enter = fadeIn(animationSpec = tween(300)) + expandVertically(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300)) + shrinkVertically(animationSpec = tween(300))
      ) {
        Column(Modifier.padding(top = 12.dp)) {
          DetailRow("Description", pluginInfo.description ?: "No description.")
          DetailRow("Author", pluginInfo.author ?: "Unknown")
          if (pluginInfo.website != null) {
            DetailRow("Website", pluginInfo.website!!)
          }
          DetailRow("License", pluginInfo.license ?: "License not set")
          DetailRow("Package", pluginInfo.packageName!!)
        }
      }
    }
  }
}

@Composable
private fun DetailRow(label: String, value: String) {
  Row(Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
    Text(
      text = "$label:",
      fontWeight = FontWeight.SemiBold,
      modifier = Modifier.width(100.dp)
    )
    Text(text = value, style = MaterialTheme.typography.bodyMedium)
  }
}
