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

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.teixeira.vcspace.ui.extensions.harmonizeWithPrimary

@Composable
fun LoadingDialog(
    message: String,
    onDismiss: (() -> Unit)? = null
) {
    Dialog(
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
        onDismissRequest = { onDismiss?.invoke() }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .fillMaxSize()
              .background(Color(0xAA000000))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                  .padding(16.dp)
                  .clip(RoundedCornerShape(16.dp))
                  .background(MaterialTheme.colorScheme.surface)
                  .padding(24.dp)
            ) {
                LoadingAnimation()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun LoadingAnimation(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "InfiniteTransition")

    val size by infiniteTransition.animateFloat(
        initialValue = 24f,
        targetValue = 32f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FloatAnimation"
    )

    Box(
        modifier = modifier
          .size(size.dp)
          .clip(CircleShape)
          .background(
            Brush.linearGradient(
              colors = listOf(
                Color(0xFF42A5F5).harmonizeWithPrimary(fraction = 0.5f),
                Color(0xFF1976D2).harmonizeWithPrimary(fraction = 0.5f)
              )
            )
          )
    )
}

@Composable
fun CoolLoadingDialog(
    message: String,
    onDismiss: (() -> Unit)? = null
) {
    Dialog(onDismissRequest = { onDismiss?.invoke() }) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .fillMaxSize()
              .background(Color(0xAA000000))
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                  .padding(16.dp)
                  .clip(RoundedCornerShape(16.dp))
                  .background(
                    brush = Brush.linearGradient(
                      colors = listOf(
                        Color(0xFF4DD0E1),
                        Color(0xFF1E88E5)
                      )
                    )
                  )
                  .padding(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    GlowingLoadingAnimation()
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = message,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun GlowingLoadingAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "")

    // Glow animation
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    // Circle rotation animation
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = ""
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(80.dp)
          .background(Color.Transparent)
    ) {
        Canvas(modifier = Modifier.size(80.dp)) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1E88E5).copy(alpha = glowAlpha),
                        Color.Transparent
                    )
                ),
                radius = 40f
            )
        }
        Canvas(modifier = Modifier.size(60.dp)) {
            rotate(angle) {
                drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(x = size.width / 4, y = size.height / 4),
                    size = size.copy(width = size.width / 2),
                    cornerRadius = CornerRadius(50f, 50f),
                    style = Stroke(4f)
                )
            }
        }
    }
}