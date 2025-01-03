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

package com.teixeira.vcspace.core.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import kotlinx.coroutines.launch

@Composable
fun DraggableFloatingPanel(
  offset: Offset,
  onOffsetChange: (Offset) -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier,
  title: String? = null,
  dismissOnBackPress: Boolean = true,
  dismissOnClickOutside: Boolean = true,
  clippingEnabled: Boolean = true,
  content: @Composable () -> Unit
) {
  val hapticFeedback = LocalHapticFeedback.current
  var panelSize by remember { mutableStateOf(IntSize.Zero) }
  var currentOffset by remember { mutableStateOf(offset) }
  val alpha = remember { Animatable(0f) }
  val coroutineScope = rememberCoroutineScope()

  LaunchedEffect(Unit) {
    coroutineScope.launch {
      alpha.animateTo(1f, animationSpec = tween(300))
    }
  }

  DisposableEffect(Unit) {
    onDispose {
      coroutineScope.launch {
        alpha.animateTo(0f, animationSpec = tween(300))
      }.invokeOnCompletion {
        onDismiss()
      }
    }
  }

  Popup(
    properties = PopupProperties(
      focusable = false,
      dismissOnBackPress = dismissOnBackPress,
      dismissOnClickOutside = dismissOnClickOutside,
      clippingEnabled = clippingEnabled
    ),
    onDismissRequest = {
      coroutineScope.launch {
        alpha.animateTo(0f, animationSpec = tween(300))
      }.invokeOnCompletion {
        onDismiss()
      }
    },
    offset = IntOffset(offset.x.toInt(), offset.y.toInt())
  ) {
    Box(
      Modifier
        .alpha(alpha.value)
        .shadow(4.dp, RoundedCornerShape(8.dp))
        .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(8.dp))
        .onGloballyPositioned { coordinates ->
          panelSize = coordinates.size
        }
        .pointerInput(Unit) {
          detectDragGesturesAfterLongPress(
            onDragStart = {
              hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            }
          ) { change, dragAmount ->
            change.consume()
            currentOffset = Offset(currentOffset.x + dragAmount.x, currentOffset.y + dragAmount.y)
            onOffsetChange(currentOffset)
          }
        }
    ) {
      Column {
        Row(
          Modifier
            .fillMaxWidth(0.8f)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
          verticalAlignment = Alignment.CenterVertically
        ) {
          CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            if (title == null) {
              Spacer(Modifier.weight(1f))
            } else {
              Text(text = title, modifier = Modifier.weight(1f).padding(start = 16.dp))
            }
            IconButton(
              onClick = {
                coroutineScope.launch {
                  alpha.animateTo(0f, animationSpec = tween(300))
                }.invokeOnCompletion {
                  onDismiss()
                }
              },
              colors = IconButtonDefaults.iconButtonColors(contentColor = LocalContentColor.current)
            ) {
              Icon(Icons.Filled.Close, contentDescription = "Close")
            }
          }
        }
        CompositionLocalProvider(LocalContentColor provides contentColorFor(MaterialTheme.colorScheme.surfaceContainer)) {
          Box(modifier = modifier) { content() }
        }
      }
    }
  }
}
