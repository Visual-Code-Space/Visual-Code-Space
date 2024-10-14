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

package com.teixeira.vcspace.core.components.file

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import com.teixeira.vcspace.providers.FileIconProvider
import com.teixeira.vcspace.resources.R
import com.teixeira.vcspace.ui.screens.editor.EditorViewModel
import java.io.File
import java.text.SimpleDateFormat
import kotlin.math.max

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("SimpleDateFormat")
@Composable
fun FileList(
  files: List<File>,
  selectedFile: EditorViewModel.OpenedFile? = null,
  modifier: Modifier = Modifier,
  itemModifier: Modifier = Modifier,
  onFileLongClick: ((File) -> Unit)? = null,
  onFileClick: (File) -> Unit,
) {
  val context = LocalContext.current
  val haptics = LocalHapticFeedback.current

  val listState = rememberLazyListState()

  LaunchedEffect(files, selectedFile) {
    val index = files.indexOf(selectedFile?.file)

    listState.animateScrollToItem(if (index != -1) index else 0)
  }

  if (files.isEmpty()) {
    Box(
      modifier = modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = stringResource(R.string.file_empty_folder)
      )
    }
  } else {
    LazyColumn(
      modifier = modifier.fillMaxWidth(),
      state = listState
    ) {
      items(files) { file ->
        val isSelectedFile = selectedFile?.file == file

        val itemBackgroundModifier = if (isSelectedFile) {
          Modifier.background(color = MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
        } else Modifier

        val icon = if (file.isFile) {
          ImageVector.vectorResource(FileIconProvider.findFileIconResource(file))
        } else Icons.Rounded.Folder

        Surface(
          modifier = itemModifier
            .then(itemBackgroundModifier)
            .semantics(mergeDescendants = true) {}
            .combinedClickable(
              onClick = { onFileClick(file) },
              onLongClick = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onFileLongClick?.invoke(file)
              }
            ),
          color = Color.Transparent
        ) {
          val edgeWidth = 8.dp

          val fileName = @Composable {
            ProvideTextStyle(MaterialTheme.typography.bodyMedium) {
              fun ContentDrawScope.drawFadedEdge(leftEdge: Boolean) {
                val edgeWidthPx = edgeWidth.toPx()
                drawRect(
                  topLeft = Offset(if (leftEdge) 0f else size.width - edgeWidthPx, 0f),
                  size = Size(edgeWidthPx, size.height),
                  brush = Brush.horizontalGradient(
                    colors = listOf(Color.Transparent, Color.Black),
                    startX = if (leftEdge) 0f else size.width,
                    endX = if (leftEdge) edgeWidthPx else size.width - edgeWidthPx
                  ),
                  blendMode = BlendMode.DstIn
                )
              }

              Text(
                text = file.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                  .fillMaxWidth()
                  .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                  .drawWithContent {
                    drawContent()
                    drawFadedEdge(leftEdge = true)
                    drawFadedEdge(leftEdge = false)
                  }
                  .basicMarquee(
                    // Animate forever.
                    iterations = Int.MAX_VALUE,
                    spacing = MarqueeSpacing(0.dp)
                  )
                  .padding(start = edgeWidth)
              )
            }
          }

          val modifiedIn = @Composable {
            ProvideTextStyle(MaterialTheme.typography.labelSmall) {
              Text(
                text = context.getString(
                  R.string.file_modified_in,
                  SimpleDateFormat("yy/MM/dd").format(file.lastModified()),
                ),
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = edgeWidth)
              )
            }
          }

          val fileIcon = @Composable {
            Box {
              CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant
              ) {
                Icon(
                  imageVector = icon,
                  contentDescription = file.name,
                  modifier = Modifier.size(24.dp)
                )
              }
            }
          }

          Layout(
            contents = listOf(fileName, modifiedIn, fileIcon),
            measurePolicy = { measurables, constraints ->
              val (fileNameMeasurable, modifiedInMeasurable, fileIconMeasurable) = measurables
              var currentTotalWidth = 0
              var currentTotalHeight = 0

              val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)
              val startPadding = 8.dp
              val endPadding = 8.dp
              val horizontalPadding = (startPadding + endPadding).roundToPx()

              val paddedLooseConstraints = looseConstraints.offset(
                horizontal = -horizontalPadding,
                vertical = 0,
              )

              val fileIconPlaceable = fileIconMeasurable
                .firstOrNull()?.measure(paddedLooseConstraints)
              currentTotalWidth += widthOrZero(fileIconPlaceable)

              val fileNamePlaceable = fileNameMeasurable.firstOrNull()?.measure(
                paddedLooseConstraints.offset(
                  horizontal = -currentTotalWidth
                )
              )
              currentTotalHeight += heightOrZero(fileNamePlaceable)

              val modifiedInPlaceable = modifiedInMeasurable.firstOrNull()?.measure(
                paddedLooseConstraints.offset(
                  horizontal = -currentTotalWidth,
                  vertical = -currentTotalHeight
                )
              )
              currentTotalHeight += heightOrZero(modifiedInPlaceable)

              val width = calculateWidth(
                iconWidth = widthOrZero(fileIconPlaceable),
                nameWidth = widthOrZero(fileNamePlaceable),
                descriptionWidth = widthOrZero(modifiedInPlaceable),
                horizontalPadding = horizontalPadding,
                constraints = constraints,
              )
              val height = calculateHeight(
                iconHeight = heightOrZero(fileIconPlaceable),
                nameHeight = heightOrZero(fileNamePlaceable),
                descriptionHeight = heightOrZero(modifiedInPlaceable),
                verticalPadding = 0,
                constraints = constraints
              )

              layout(width, height) {
                fileIconPlaceable?.let {
                  it.placeRelative(
                    x = startPadding.roundToPx(),
                    y = CenterVertically.align(it.height, height)
                  )
                }

                val mainContentX = startPadding.roundToPx() + widthOrZero(fileIconPlaceable)
                val totalHeight =
                  heightOrZero(fileNamePlaceable) + heightOrZero(modifiedInPlaceable)
                val mainContentY = CenterVertically.align(totalHeight, height)
                var currentY = mainContentY

                fileNamePlaceable?.placeRelative(mainContentX, currentY)
                currentY += heightOrZero(fileNamePlaceable)

                modifiedInPlaceable?.placeRelative(mainContentX, currentY)
              }
            }
          )
        }
      }
    }
  }
}

internal fun widthOrZero(placeable: Placeable?) = placeable?.width ?: 0

internal fun heightOrZero(placeable: Placeable?) = placeable?.height ?: 0

private fun IntrinsicMeasureScope.calculateWidth(
  iconWidth: Int,
  nameWidth: Int,
  descriptionWidth: Int,
  horizontalPadding: Int,
  constraints: Constraints,
): Int {
  if (constraints.hasBoundedWidth) {
    return constraints.maxWidth
  }
  // Fallback behavior if width constraints are infinite
  val mainContentWidth = maxOf(nameWidth, descriptionWidth)
  return horizontalPadding + iconWidth + mainContentWidth
}

private fun IntrinsicMeasureScope.calculateHeight(
  iconHeight: Int,
  nameHeight: Int,
  descriptionHeight: Int,
  verticalPadding: Int,
  constraints: Constraints,
): Int {
  val defaultMinHeight = 48.dp
  val minHeight = max(constraints.minHeight, defaultMinHeight.roundToPx())

  val mainContentHeight = nameHeight + descriptionHeight

  return max(minHeight, verticalPadding + maxOf(iconHeight, mainContentHeight))
    .coerceAtMost(constraints.maxHeight)
}