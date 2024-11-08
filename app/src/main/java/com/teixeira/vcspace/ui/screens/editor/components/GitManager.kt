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

package com.teixeira.vcspace.ui.screens.editor.components

import android.webkit.URLUtil
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.blankj.utilcode.util.ClipboardUtils
import com.teixeira.vcspace.activities.Editor.LocalEditorDrawerNavController
import com.teixeira.vcspace.activities.base.LocalLifecycleScope
import com.teixeira.vcspace.app.strings
import com.teixeira.vcspace.ui.LocalToastHostState
import com.teixeira.vcspace.ui.components.git.CloneRepositoryDialog
import com.teixeira.vcspace.ui.navigateSingleTop
import com.teixeira.vcspace.ui.screens.EditorDrawerScreens
import com.teixeira.vcspace.ui.screens.file.FileExplorerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun GitManager(
  modifier: Modifier = Modifier,
  fileExplorerViewModel: FileExplorerViewModel
) {
  val scope = rememberCoroutineScope()
  val lifecycleScope = LocalLifecycleScope.current
  val toastHostState = LocalToastHostState.current

  var showGitCloneDialog by remember { mutableStateOf(false) }

  Column(
    modifier = modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Button(onClick = {
      showGitCloneDialog = true
    }) {
      Text(stringResource(strings.git_clone))
    }
  }

  if (showGitCloneDialog) {
    val clip = ClipboardUtils.getText().toString()
    val navController = LocalEditorDrawerNavController.current

    CloneRepositoryDialog(
      url = if (URLUtil.isValidUrl(clip)) clip else "",
      fileExplorerViewModel = fileExplorerViewModel,
      onDismissRequest = { showGitCloneDialog = false },
      onSuccessfulClone = {
        scope.launch {
          toastHostState.showToast(
            message = "Successfully cloned",
            icon = Icons.Rounded.Check
          )
        }
        lifecycleScope.launch(Dispatchers.Main) {
          navController.navigateSingleTop(EditorDrawerScreens.FileExplorer)
        }
      },
      onFailedClone = {
        showGitCloneDialog = false
        it.printStackTrace()
        scope.launch {
          toastHostState.showToast(
            message = it.message ?: "Error",
            icon = Icons.Rounded.ErrorOutline
          )
        }
      }
    )
  }
}