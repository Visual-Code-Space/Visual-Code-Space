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

package com.teixeira.vcspace.ui.screens.editor.components.drawer

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.documentfile.provider.DocumentFile
import com.blankj.utilcode.util.UriUtils
import com.teixeira.vcspace.PreferenceKeys
import com.teixeira.vcspace.resources.R
import com.teixeira.vcspace.app.strings
import com.teixeira.vcspace.preferences.defaultPrefs
import com.teixeira.vcspace.ui.screens.file.FileExplorerViewModel
import com.teixeira.vcspace.utils.showShortToast
import java.io.File

@Composable
fun OpenFolderActions(
  modifier: Modifier = Modifier,
  fileExplorerViewModel: FileExplorerViewModel
) {
  val context = LocalContext.current

  val openFolder = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.OpenDocumentTree()
  ) { uri ->
    if (uri != null) DocumentFile.fromTreeUri(context, uri)?.let {
      fileExplorerViewModel.openFolder(UriUtils.uri2File(it.uri))
    }
  }

  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Button(onClick = { openFolder.launch(null) }) {
      Text(text = stringResource(strings.open_folder))
    }

    Button(onClick = {
      val recentFolder = defaultPrefs.getString(PreferenceKeys.RECENT_FOLDER, "") ?: ""
      if (recentFolder.isNotEmpty()) {
        val treeUri = DocumentFile.fromFile(File(recentFolder)).uri
        fileExplorerViewModel.openFolder(UriUtils.uri2File(treeUri))
      } else {
        showShortToast(context, context.getString(R.string.no_recent_folder_found))
      }
    }) {
      Text(text = stringResource(strings.open_recent))
    }
  }
}