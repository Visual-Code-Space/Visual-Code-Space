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

package com.teixeira.vcspace.compose.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.teixeira.vcspace.ui.screens.editor.EditorViewModel
import kiwi.orbit.compose.icons.Icons
import kiwi.orbit.compose.ui.controls.Icon
import kiwi.orbit.compose.ui.controls.Separator
import kiwi.orbit.compose.ui.controls.Tab
import kiwi.orbit.compose.ui.controls.Text

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorTab(
    files: List<EditorViewModel.OpenedFile>,
    selectedFileIndex: Int,
    onTabSelected: (Int) -> Unit,
    onTabClose: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onTabReselected: (Int) -> Unit = {}
) {
    PrimaryScrollableTabRow(
        selectedTabIndex = selectedFileIndex,
        modifier = modifier.fillMaxWidth(),
        edgePadding = 0.dp,
        divider = {}
    ) {
        files.forEachIndexed { index, file ->
            Tab(
                selected = index == selectedFileIndex,
                onClick = {
                    if (index == selectedFileIndex) {
                        onTabReselected(index)
                    } else {
                        onTabSelected(index)
                    }
                }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "${if (file.isModified) "*" else ""}${file.file.name}",
                        color = TabRowDefaults.primaryContentColor
                    )

                    Icon(
                        Icons.Close,
                        contentDescription = null,
                        tint = TabRowDefaults.primaryContentColor,
                        modifier = Modifier.clickable {
                            onTabClose(index)
                        }
                    )
                }
            }
        }
    }

    Separator(
        color = MaterialTheme.colorScheme.outline,
        thickness = 1.dp,
        modifier = Modifier.fillMaxWidth()
    )
}
