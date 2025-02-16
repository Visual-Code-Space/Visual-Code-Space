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

package com.teixeira.vcspace.editor.textaction

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TooltipState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.findViewTreeSavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.teixeira.vcspace.resources.R
import kotlin.math.abs

@Composable
fun editorTextActionWindow(
    items: List<EditorTextActionItem>,
    modifier: Modifier = Modifier,
    editorTextActionWindowExpandThreshold: Int = 10,
    onItemClick: (EditorTextActionItem) -> Unit
): FrameLayout {
    val compositionContext = rememberCompositionContext()
    val context = LocalContext.current
    val view = LocalView.current
    val viewTreeLifecycleOwner = view.findViewTreeLifecycleOwner()
    val viewTreeSavedStateRegistryOwner = view.findViewTreeSavedStateRegistryOwner()

    val composeView = ComposeView(context).apply {
        setParentCompositionContext(compositionContext)
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
        setContent {
            Card(
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline
                ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
            ) {
                EditorTextActionContent(
                    items,
                    modifier,
                    editorTextActionWindowExpandThreshold,
                    onItemClick
                )
            }
        }
    }
    val parentView = FrameLayout(context).apply {
        id = android.R.id.content
        setViewTreeLifecycleOwner(viewTreeLifecycleOwner)
        setViewTreeSavedStateRegistryOwner(viewTreeSavedStateRegistryOwner)
        layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        addView(composeView)
    }
    return parentView
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditorTextActionContent(
    items: List<EditorTextActionItem>,
    modifier: Modifier = Modifier,
    editorTextActionWindowExpandThreshold: Int,
    onItemClick: (EditorTextActionItem) -> Unit,
) {
    val tooltipStates = items.map { rememberTooltipState(isPersistent = true) }
    var isColumn by remember { mutableStateOf(false) }
    var previousHorizontalScrollOffset by remember { mutableIntStateOf(0) }
    val horizontalScrollState = rememberScrollState()

    val animatedIsColumn by animateFloatAsState(
        targetValue = if (isColumn) 1f else 0f,
        animationSpec = tween(durationMillis = 1000)
    )

    LaunchedEffect(horizontalScrollState.value) {
        val currentOffset = horizontalScrollState.value
        val offsetChange = currentOffset - previousHorizontalScrollOffset
        previousHorizontalScrollOffset = currentOffset

        if (abs(offsetChange) > editorTextActionWindowExpandThreshold) {
            isColumn = offsetChange > 0
        }
    }

    val tooltip: @Composable (EditorTextActionItem, TooltipState, @Composable () -> Unit) -> Unit =
        { item, tooltipState, content ->
            TooltipBox(
                positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(
                    spacingBetweenTooltipAndAnchor = 2.dp
                ),
                tooltip = {
                    RichTooltip(
                        title = { Text(stringResource(item.id)) },
                        action = {
                            Text(
                                text = stringResource(R.string.ok),
                                modifier = Modifier.clickable(
                                    onClick = {
                                        tooltipState.dismiss()
                                    },
                                    role = Role.Button
                                )
                            )
                        }
                    ) {
                        Text(item.description)
                    }
                },
                state = tooltipState,
                content = content
            )
        }

    val content = @Composable {
        if (isColumn.not()) {
            Row(
                modifier = modifier
                    .horizontalScroll(horizontalScrollState)
                    .width(IntrinsicSize.Max)
                    .alpha(1f - animatedIsColumn)
            ) {
                items.filter { it.visible }.forEachIndexed { index, item ->
                    tooltip(item, tooltipStates[index]) {
                        IconButton(
                            onClick = { onItemClick(item) },
                            enabled = item.clickable
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = stringResource(item.id),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = modifier
                    .alpha(animatedIsColumn)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center
            ) {
                items.filter { it.visible }.forEachIndexed { index, item ->
                    tooltip(item, tooltipStates[index]) {
                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .clip(MaterialTheme.shapes.small)
                                .clickable(
                                    onClick = { onItemClick(item) },
                                    role = Role.Button,
                                    enabled = item.clickable
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp)
                                    .padding(start = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = stringResource(item.id),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(item.id)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    content()
}