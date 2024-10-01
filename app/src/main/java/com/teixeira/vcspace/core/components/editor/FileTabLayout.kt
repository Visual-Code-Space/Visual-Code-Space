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

package com.teixeira.vcspace.core.components.editor

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.teixeira.vcspace.viewmodel.editor.EditorViewModel

@Composable
fun FileTabLayout(
  modifier: Modifier = Modifier,
  editorViewModel: EditorViewModel
) {
  val uiState by editorViewModel.uiState.collectAsStateWithLifecycle()

  val selectedFileIndex = uiState.selectedFileIndex
  val openedFiles = uiState.openedFiles

  if (openedFiles.isEmpty()) return

  ScrollableTabRow(
    selectedTabIndex = selectedFileIndex.coerceIn(0, openedFiles.size - 1),
    modifier = modifier.fillMaxWidth(),
    edgePadding = 0.dp,
    indicator = { tabPositions ->
      TabRowDefaults.SecondaryIndicator(
        Modifier.tabIndicatorOffset(
          tabPositions[selectedFileIndex.coerceIn(tabPositions.indices)]
        )
      )
    },
    divider = {}
  ) {
    openedFiles.fastForEachIndexed { index, file ->
      Tab(
        selected = index == selectedFileIndex.coerceIn(0, openedFiles.size - 1),
        onClick = { editorViewModel.selectFile(index) },
        text = { Text(file.name) }
      )
    }
  }

  HorizontalDivider(thickness = 1.dp)
}
