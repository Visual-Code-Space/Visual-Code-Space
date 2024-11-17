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

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalUseFallbackRippleImplementation
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlin.math.floor
import kotlin.math.max

@Composable
fun TriStateCheckbox(
  state: ToggleableState,
  onClick: (() -> Unit)?,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  size: Dp = CheckboxSize,
  colors: CheckboxColors = CheckboxDefaults.colors(),
  interactionSource: MutableInteractionSource? = null
) {
  val toggleableModifier =
    if (onClick != null) {
      Modifier.triStateToggleable(
        state = state,
        onClick = onClick,
        enabled = enabled,
        role = Role.Checkbox,
        interactionSource = interactionSource,
        indication = rippleOrFallbackImplementation(
          bounded = false,
          radius = size
        )
      )
    } else {
      Modifier
    }
  CheckboxImpl(
    enabled = enabled,
    value = state,
    modifier =
    modifier
      .then(
        if (onClick != null) {
          Modifier.minimumInteractiveComponentSize()
        } else {
          Modifier
        }
      )
      .then(toggleableModifier),
    colors = colors,
    size = size
  )
}

@Suppress("DEPRECATION_ERROR")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun rippleOrFallbackImplementation(
  bounded: Boolean = true,
  radius: Dp = Dp.Unspecified,
  color: Color = Color.Unspecified
): Indication {
  return if (LocalUseFallbackRippleImplementation.current) {
    androidx.compose.material.ripple.rememberRipple(bounded, radius, color)
  } else {
    ripple(bounded, radius, color)
  }
}

@Composable
private fun CheckboxImpl(
  enabled: Boolean,
  value: ToggleableState,
  modifier: Modifier,
  colors: CheckboxColors,
  size: Dp
) {
  val transition = updateTransition(value)
  val checkDrawFraction =
    transition.animateFloat(
      transitionSpec = {
        when {
          initialState == ToggleableState.Off -> tween(CheckAnimationDuration)
          targetState == ToggleableState.Off -> snap(BoxOutDuration)
          else -> spring()
        }
      }
    ) {
      when (it) {
        ToggleableState.On -> 1f
        ToggleableState.Off -> 0f
        ToggleableState.Indeterminate -> 1f
      }
    }

  val checkCenterGravitationShiftFraction =
    transition.animateFloat(
      transitionSpec = {
        when {
          initialState == ToggleableState.Off -> snap()
          targetState == ToggleableState.Off -> snap(BoxOutDuration)
          else -> tween(durationMillis = CheckAnimationDuration)
        }
      }
    ) {
      when (it) {
        ToggleableState.On -> 0f
        ToggleableState.Off -> 0f
        ToggleableState.Indeterminate -> 1f
      }
    }
  val checkCache = remember { CheckDrawingCache() }
  val checkColor = colors.checkmarkColor(value)
  val boxColor = colors.boxColor(enabled, value)
  val borderColor = colors.borderColor(enabled, value)
  Canvas(
    modifier
      .wrapContentSize(Alignment.Center)
      .requiredSize(size)
  ) {
    val strokeWidthPx = floor(StrokeWidth.toPx())
    drawBox(
      boxColor = boxColor.value,
      borderColor = borderColor.value,
      radius = RadiusSize.toPx(),
      strokeWidth = strokeWidthPx
    )
    drawCheck(
      checkColor = checkColor.value,
      checkFraction = checkDrawFraction.value,
      crossCenterGravitation = checkCenterGravitationShiftFraction.value,
      strokeWidthPx = strokeWidthPx,
      drawingCache = checkCache
    )
  }
}

private fun DrawScope.drawBox(
  boxColor: Color,
  borderColor: Color,
  radius: Float,
  strokeWidth: Float
) {
  val halfStrokeWidth = strokeWidth / 2.0f
  val stroke = Stroke(strokeWidth)
  val checkboxSize = size.width
  if (boxColor == borderColor) {
    drawRoundRect(
      boxColor,
      size = Size(checkboxSize, checkboxSize),
      cornerRadius = CornerRadius(radius),
      style = Fill
    )
  } else {
    drawRoundRect(
      boxColor,
      topLeft = Offset(strokeWidth, strokeWidth),
      size = Size(checkboxSize - strokeWidth * 2, checkboxSize - strokeWidth * 2),
      cornerRadius = CornerRadius(max(0f, radius - strokeWidth)),
      style = Fill
    )
    drawRoundRect(
      borderColor,
      topLeft = Offset(halfStrokeWidth, halfStrokeWidth),
      size = Size(checkboxSize - strokeWidth, checkboxSize - strokeWidth),
      cornerRadius = CornerRadius(radius - halfStrokeWidth),
      style = stroke
    )
  }
}

private fun DrawScope.drawCheck(
  checkColor: Color,
  checkFraction: Float,
  crossCenterGravitation: Float,
  strokeWidthPx: Float,
  drawingCache: CheckDrawingCache
) {
  val stroke = Stroke(width = strokeWidthPx, cap = StrokeCap.Square)
  val width = size.width
  val checkCrossX = 0.4f
  val checkCrossY = 0.7f
  val leftX = 0.2f
  val leftY = 0.5f
  val rightX = 0.8f
  val rightY = 0.3f

  val gravitatedCrossX = lerp(checkCrossX, 0.5f, crossCenterGravitation)
  val gravitatedCrossY = lerp(checkCrossY, 0.5f, crossCenterGravitation)
  // gravitate only Y for end to achieve center line
  val gravitatedLeftY = lerp(leftY, 0.5f, crossCenterGravitation)
  val gravitatedRightY = lerp(rightY, 0.5f, crossCenterGravitation)

  with(drawingCache) {
    checkPath.reset()
    checkPath.moveTo(width * leftX, width * gravitatedLeftY)
    checkPath.lineTo(width * gravitatedCrossX, width * gravitatedCrossY)
    checkPath.lineTo(width * rightX, width * gravitatedRightY)
    pathMeasure.setPath(checkPath, false)
    pathToDraw.reset()
    pathMeasure.getSegment(0f, pathMeasure.length * checkFraction, pathToDraw, true)
  }
  drawPath(drawingCache.pathToDraw, checkColor, style = stroke)
}

@Immutable
private class CheckDrawingCache(
  val checkPath: Path = Path(),
  val pathMeasure: PathMeasure = PathMeasure(),
  val pathToDraw: Path = Path()
)

@Composable
private fun CheckboxColors.checkmarkColor(state: ToggleableState): State<Color> {
  val target =
    if (state == ToggleableState.Off) {
      uncheckedCheckmarkColor
    } else {
      checkedCheckmarkColor
    }

  val duration = if (state == ToggleableState.Off) BoxOutDuration else BoxInDuration
  return animateColorAsState(target, tween(durationMillis = duration))
}

/**
 * Represents the color used for the box (background) of the checkbox, depending on [enabled]
 * and [state].
 *
 * @param enabled whether the checkbox is enabled or not
 * @param state the [ToggleableState] of the checkbox
 */
@Composable
private fun CheckboxColors.boxColor(enabled: Boolean, state: ToggleableState): State<Color> {
  val target =
    if (enabled) {
      when (state) {
        ToggleableState.On,
        ToggleableState.Indeterminate -> checkedBoxColor

        ToggleableState.Off -> uncheckedBoxColor
      }
    } else {
      when (state) {
        ToggleableState.On -> disabledCheckedBoxColor
        ToggleableState.Indeterminate -> disabledIndeterminateBoxColor
        ToggleableState.Off -> disabledUncheckedBoxColor
      }
    }

  // If not enabled 'snap' to the disabled state, as there should be no animations between
  // enabled / disabled.
  return if (enabled) {
    val duration =
      if (state == ToggleableState.Off) BoxOutDuration else BoxInDuration
    animateColorAsState(target, tween(durationMillis = duration))
  } else {
    rememberUpdatedState(target)
  }
}

@Composable
private fun CheckboxColors.borderColor(enabled: Boolean, state: ToggleableState): State<Color> {
  val target =
    if (enabled) {
      when (state) {
        ToggleableState.On,
        ToggleableState.Indeterminate -> checkedBorderColor

        ToggleableState.Off -> uncheckedBorderColor
      }
    } else {
      when (state) {
        ToggleableState.Indeterminate -> disabledIndeterminateBorderColor
        ToggleableState.On -> disabledBorderColor
        ToggleableState.Off -> disabledUncheckedBorderColor
      }
    }

  // If not enabled 'snap' to the disabled state, as there should be no animations between
  // enabled / disabled.
  return if (enabled) {
    val duration =
      if (state == ToggleableState.Off) BoxOutDuration else BoxInDuration
    animateColorAsState(target, tween(durationMillis = duration))
  } else {
    rememberUpdatedState(target)
  }
}

private const val BoxInDuration = 50
private const val BoxOutDuration = 100
private const val CheckAnimationDuration = 100

private val CheckboxDefaultPadding = 2.dp
private val CheckboxSize = 20.dp
private val StrokeWidth = 2.dp
private val RadiusSize = 2.dp