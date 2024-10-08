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

package com.teixeira.vcspace.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.AccessibilityManager
import androidx.compose.ui.platform.LocalAccessibilityManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.teixeira.vcspace.app.noLocalProvidedFor
import com.teixeira.vcspace.ui.extensions.harmonizeWithPrimary
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.resume
import kotlin.math.min

val LocalToastHostState = compositionLocalOf<ToastHostState> { noLocalProvidedFor("LocalToastHostState") }

@Composable
fun rememberToastHostState() = remember { ToastHostState() }

@Composable
fun ToastHost(
  modifier: Modifier = Modifier,
  hostState: ToastHostState = LocalToastHostState.current,
  alignment: Alignment = Alignment.BottomCenter,
  toast: @Composable (ToastData) -> Unit = { Toast(it) }
) {
  val currentToastData = hostState.currentToastData
  val accessibilityManager = LocalAccessibilityManager.current
  LaunchedEffect(currentToastData) {
    if (currentToastData != null) {
      val duration = currentToastData.visuals.duration.toMillis(accessibilityManager)
      delay(duration)
      currentToastData.dismiss()
    }
  }

  AnimatedContent(
    targetState = currentToastData,
    transitionSpec = { ToastDefaults.transition },
    label = ""
  ) {
    Box(modifier = modifier.fillMaxSize()) {
      Box(modifier = Modifier.align(alignment)) {
        it?.let { toast(it) }
      }
    }
  }
}

@Composable
fun Toast(
  toastData: ToastData,
  modifier: Modifier = Modifier,
  shape: Shape = ToastDefaults.shape,
  containerColor: Color = ToastDefaults.color,
  contentColor: Color = ToastDefaults.contentColor,
) {
  val configuration = LocalConfiguration.current
  val sizeMin = min(configuration.screenWidthDp, configuration.screenHeightDp).dp

  Card(
    colors = CardDefaults.cardColors(
      containerColor = containerColor,
      contentColor = contentColor
    ),
    modifier = if (modifier != Modifier) modifier else
      Modifier
        .heightIn(min = 48.dp)
        .widthIn(min = 0.dp, max = (sizeMin * 0.7f))
        .padding(
          bottom = sizeMin * 0.2f,
          top = 24.dp,
          start = 12.dp,
          end = 12.dp
        )
        .imePadding()
        .systemBarsPadding()
        .alpha(0.95f),
    shape = shape
  ) {
    Row(
      Modifier.padding(15.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      toastData.visuals.icon?.let { Icon(it, null, tint = contentColor) }
      Spacer(modifier = Modifier.size(8.dp))
      Text(
        style = MaterialTheme.typography.bodySmall,
        text = toastData.visuals.message,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(end = 5.dp)
      )
    }
  }
}

@Stable
class ToastHostState {
  private val mutex = Mutex()

  var currentToastData by mutableStateOf<ToastData?>(null)
    private set

  @OptIn(ExperimentalMaterial3Api::class)
  suspend fun showToast(
    message: String,
    icon: ImageVector? = null,
    duration: ToastDuration = ToastDuration.Short
  ) = showToast(ToastVisualsImpl(message, icon, duration))

  @ExperimentalMaterial3Api
  suspend fun showToast(visuals: ToastVisuals) = mutex.withLock {
    try {
      suspendCancellableCoroutine { continuation ->
        currentToastData = ToastDataImpl(visuals, continuation)
      }
    } finally {
      currentToastData = null
    }
  }

  private class ToastVisualsImpl(
    override val message: String,
    override val icon: ImageVector? = null,
    override val duration: ToastDuration
  ) : ToastVisuals {

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other == null || this::class != other::class) return false

      other as ToastVisualsImpl

      if (message != other.message) return false
      if (icon != other.icon) return false
      if (duration != other.duration) return false
      return true
    }

    override fun hashCode(): Int {
      var result = message.hashCode()
      result = 31 * result + icon.hashCode()
      result = 31 * result + duration.hashCode()
      return result
    }
  }

  private class ToastDataImpl(
    override val visuals: ToastVisuals,
    private val continuation: CancellableContinuation<Unit>
  ) : ToastData {

    override fun dismiss() {
      if (continuation.isActive) continuation.resume(Unit)
    }

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other == null || this::class != other::class) return false

      other as ToastDataImpl

      if (visuals != other.visuals) return false
      if (continuation != other.continuation) return false

      return true
    }

    override fun hashCode(): Int {
      var result = visuals.hashCode()
      result = 31 * result + continuation.hashCode()
      return result
    }
  }
}

@Stable
interface ToastData {
  val visuals: ToastVisuals
  fun dismiss()
}

@Stable
interface ToastVisuals {
  val message: String
  val icon: ImageVector?
  val duration: ToastDuration
}

enum class ToastDuration { Short, Long }

object ToastDefaults {
  val transition: ContentTransform
    get() = (fadeIn(tween(300)) + scaleIn(
      tween(500),
      transformOrigin = TransformOrigin(0.5f, 1f)
    ) + slideInVertically(
      tween(500)
    ) { it / 2 }).togetherWith(
      fadeOut(tween(250)) + slideOutVertically(
        tween(500)
      ) { it / 2 } + scaleOut(
        tween(750),
        transformOrigin = TransformOrigin(0.5f, 1f)
      )
    )
  val contentColor: Color @Composable get() = MaterialTheme.colorScheme.inverseOnSurface.harmonizeWithPrimary()
  val color: Color @Composable get() = MaterialTheme.colorScheme.inverseSurface.harmonizeWithPrimary()
  val shape: Shape @Composable get() = MaterialTheme.shapes.extraLarge
}

private fun ToastDuration.toMillis(
  accessibilityManager: AccessibilityManager?
): Long {
  val original = when (this) {
    ToastDuration.Long -> 6500L
    ToastDuration.Short -> 3000L
  }
  return accessibilityManager?.calculateRecommendedTimeoutMillis(
    original,
    containsIcons = false,
    containsText = true
  ) ?: original
}
