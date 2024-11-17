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

@file:OptIn(ExperimentalMaterial3Api::class)

package com.teixeira.vcspace.ui.git

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotInterested
import androidx.compose.material.icons.sharp.Folder
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teixeira.vcspace.app.Folder
import com.teixeira.vcspace.app.strings
import com.teixeira.vcspace.git.VCSGit
import com.teixeira.vcspace.ui.LocalToastHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun GitInitSheet(
  folder: Folder,
  onDismissRequest: () -> Unit,
  modifier: Modifier = Modifier,
  onSuccess: suspend CoroutineScope.() -> Unit = {},
  onFailure: suspend CoroutineScope.(Throwable) -> Unit = {}
) {
  val toastHostState = LocalToastHostState.current

  val ioScope = rememberCoroutineScope { Dispatchers.IO }
  var isInitializing by rememberSaveable { mutableStateOf(false) }

  val sheetState = rememberModalBottomSheetState()

  val hide = remember {
    suspend {
      ioScope.launch(Dispatchers.Main) { sheetState.hide() }.invokeOnCompletion {
        if (!sheetState.isVisible) {
          onDismissRequest()
        }
      }
    }
  }

  ModalBottomSheet(
    onDismissRequest = onDismissRequest,
    modifier = modifier,
    sheetState = sheetState
  ) {
    Column(
      modifier = Modifier
        .padding(16.dp)
        .fillMaxSize(),
      horizontalAlignment = Alignment.Start
    ) {
      Text(
        text = "Initialize a new Git repository",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 28.sp,
        letterSpacing = (-0.015).sp
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "This will create a .git folder in the selected directory. You can start tracking changes and make your first commit.",
        fontSize = 16.sp,
        lineHeight = 20.sp
      )

      Spacer(modifier = Modifier.height(16.dp))

      OutlinedTextField(
        value = folder.canonicalPath,
        onValueChange = { /* No direct input, only through the bottom sheet */ },
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = "Select folder") },
        trailingIcon = {
          IconButton(onClick = {
            ioScope.launch(Dispatchers.Main) {
              toastHostState.showToast(
                message = "Not yet implemented",
                icon = Icons.Default.NotInterested
              )
            }
          }) {
            Icon(Icons.Sharp.Folder, contentDescription = "Select Folder")
          }
        },
        readOnly = true
      )

      Spacer(modifier = Modifier.height(16.dp))

      Button(
        onClick = {
          isInitializing = true

          doInit(
            folder = folder,
            scope = ioScope,
            onSuccess = {
              ioScope.launch(Dispatchers.Main) {
                hide().also { isInitializing = false }

                withContext(Dispatchers.Main.immediate + SupervisorJob()) {
                  onSuccess()
                }
              }
            },
            onFailure = {
              ioScope.launch(Dispatchers.Main) {
                hide().also { isInitializing = false }

                onFailure(it)
              }
            }
          )
        },
        modifier = Modifier.fillMaxWidth(),
        enabled = !isInitializing
      ) {
        Text(
          text = if (isInitializing) {
            stringResource(strings.initializing)
          } else {
            stringResource(strings.initialize)
          },
          fontWeight = FontWeight.SemiBold,
          letterSpacing = (0.015).sp
        )
      }
    }
  }
}

private fun doInit(
  folder: Folder,
  scope: CoroutineScope,
  onSuccess: () -> Unit,
  onFailure: (Throwable) -> Unit
) {
  scope.launch {
    runCatching {
      VCSGit.instance.init(folder)
      VCSGit.instance.addAll()
    }.onSuccess { onSuccess() }.onFailure(onFailure)
  }
}

