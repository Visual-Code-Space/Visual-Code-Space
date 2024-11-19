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

package com.teixeira.vcspace.ui.git

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.teixeira.vcspace.compose.clipUrl
import com.teixeira.vcspace.git.GitConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.teixeira.vcspace.git.GitManager.Companion.instance as git

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRemoteSheet(
  onDismissRequest: () -> Unit,
  modifier: Modifier = Modifier,
  onSuccess: suspend CoroutineScope.() -> Unit = {},
  onFailure: suspend CoroutineScope.(Throwable) -> Unit = {}
) {
  val coroutineScope = rememberCoroutineScope()
  val sheetState = rememberModalBottomSheetState()
  val clipboardUrl = clipUrl()

  var remoteUrl by remember { mutableStateOf(clipboardUrl ?: "") }
  var remoteName by remember { mutableStateOf(GitConstants.DEFAULT_REMOTE_NAME) }
  var isLoading by remember { mutableStateOf(false) }

  val hide = remember {
    suspend {
      coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
        if (!sheetState.isVisible && !isLoading) {
          onDismissRequest()
        }
      }
    }
  }

  ModalBottomSheet(
    onDismissRequest = if (!isLoading) onDismissRequest else ({}),
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
        text = "Add Remote",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 28.sp,
        letterSpacing = (-0.015).sp
      )

      Spacer(modifier = Modifier.height(8.dp))

      if (isLoading) {
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
      }

      Text(
        text = "This will add a remote repository to your local repository. You can then push and pull changes to and from the remote repository.",
        fontSize = 16.sp,
        lineHeight = 20.sp
      )

      Spacer(modifier = Modifier.height(16.dp))

      OutlinedTextField(
        value = remoteUrl,
        onValueChange = { remoteUrl = it },
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = "Remote URL") }
      )

      Spacer(modifier = Modifier.height(16.dp))

      OutlinedTextField(
        value = remoteName,
        onValueChange = { remoteName = it },
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = "Remote Name") }
      )

      Spacer(modifier = Modifier.height(16.dp))

      Button(
        onClick = {
          coroutineScope.launch(Dispatchers.IO) {
            isLoading = true

            runCatching {
              addRemote(
                remoteUrl = remoteUrl,
                remoteName = remoteName
              )

              git.fetch(
                onUpdate = { progress, taskName ->

                }
              )
            }.onSuccess {
              isLoading = false
              hide()

              withContext(Dispatchers.Main.immediate + SupervisorJob()) {
                onSuccess()
              }
            }.onFailure {
              isLoading = false
              hide()

              withContext(Dispatchers.Main) {
                onFailure(it)
              }
            }
          }
        },
        modifier = Modifier.fillMaxWidth(),
        enabled = !isLoading
      ) {
        Text(
          text = stringResource(id = android.R.string.ok),
          fontWeight = FontWeight.SemiBold,
          letterSpacing = (0.015).em
        )
      }

      Button(
        onClick = { coroutineScope.launch { hide() } },
        modifier = Modifier.fillMaxWidth(),
        enabled = !isLoading
      ) {
        Text(
          text = stringResource(id = android.R.string.cancel),
          fontWeight = FontWeight.SemiBold,
          letterSpacing = (0.015).em
        )
      }
    }
  }
}

private fun addRemote(
  remoteUrl: String,
  remoteName: String
) {
  git.addRemote(remoteUrl, remoteName)
}
