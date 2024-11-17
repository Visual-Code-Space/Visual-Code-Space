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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.teixeira.vcspace.KEY_GIT_PASSWORD
import com.teixeira.vcspace.KEY_GIT_USERNAME
import com.teixeira.vcspace.git.ChangeStats
import com.teixeira.vcspace.git.GitViewModel
import com.teixeira.vcspace.preferences.defaultPrefs
import com.teixeira.vcspace.ui.LocalToastHostState
import com.teixeira.vcspace.ui.rememberSheetState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.teixeira.vcspace.git.GitManager.Companion.instance as git

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GitCommitSheet(
  onDismissRequest: () -> Unit,
  modifier: Modifier = Modifier,
  gitViewModel: GitViewModel = viewModel(),
  onSuccess: suspend CoroutineScope.() -> Unit = {},
  onFailure: suspend CoroutineScope.(Throwable) -> Unit = {}
) {
  val username = defaultPrefs.getString(KEY_GIT_USERNAME, "") ?: ""
  val password = defaultPrefs.getString(KEY_GIT_PASSWORD, "") ?: ""

  var isCredentialError by remember { mutableStateOf(false) }
  LaunchedEffect(Unit) {
    if (username.isEmpty() || password.isEmpty()) {
      isCredentialError = true
    }
  }

  if (isCredentialError) {
    AlertDialog(
      onDismissRequest = { isCredentialError = false },
      title = { Text(text = "Credential Error") },
      text = { Text(text = "Please set your Git username and password in settings.") },
      confirmButton = {
        TextButton(onClick = { isCredentialError = false }) {
          Text(text = "OK")
        }
      }
    )
  }

  var commitMessage by rememberSaveable { mutableStateOf("") }

  val changes by gitViewModel.changes.collectAsStateWithLifecycle(context = Dispatchers.IO)
  val changesToBeCommited = remember { mutableStateListOf<String>() }
  LaunchedEffect(key1 = true) {
    withContext(Dispatchers.IO) {
      gitViewModel.loadChanges()
      changesToBeCommited.clear()
      changesToBeCommited.addAll(changes.fileChanges)
    }
  }

  var changeStats: ChangeStats? by remember { mutableStateOf(null) }
  var loadingChangeStats by remember { mutableStateOf(false) }
  LaunchedEffect(key1 = true) {
    loadingChangeStats = true
    runCatching {
      git.getUncommittedChangesStats()
    }.onSuccess { changeStats = it }.onFailure {
      it.printStackTrace()
      commitMessage = it.message.toString()
    }
    loadingChangeStats = false
  }

  var amendCommit by remember { mutableStateOf(false) }
  var signOff by remember { mutableStateOf(false) }

  val sheetState = rememberSheetState(
    initialValue = SheetValue.Expanded,
    skipHiddenState = false,
    skipPartiallyExpanded = true
  )
  val scope = rememberCoroutineScope()
  val toastHostState = LocalToastHostState.current

  val hide = remember {
    suspend {
      scope.launch(Dispatchers.Main) { sheetState.hide() }.invokeOnCompletion {
        if (!sheetState.isVisible) {
          onDismissRequest()
        }
      }
    }
  }

  ModalBottomSheet(
    onDismissRequest = onDismissRequest,
    modifier = modifier,
    sheetState = sheetState,
    dragHandle = {}
  ) {
    Scaffold(
      topBar = {
        TopAppBar(
          title = { Text("Commit") },
          navigationIcon = {
            IconButton(onClick = { scope.launch { hide() } }) {
              Icon(Icons.AutoMirrored.Sharp.ArrowBack, contentDescription = null)
            }
          }
        )
      },
      modifier = Modifier.fillMaxSize(),
      contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
      Column(
        modifier = Modifier
          .padding(innerPadding)
          .padding(16.dp)
          .verticalScroll(rememberScrollState())
          .fillMaxSize(),
        horizontalAlignment = Alignment.Start
      ) {
        Text(
          "Changes to be committed",
          fontSize = 22.sp,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
          val (filesChanged, totalAdditions, totalDeletions) = changeStats ?: ChangeStats(0, 0, 0)

          Checkbox(
            checked = true,
            onCheckedChange = { }
          )

          val changeText =
            if (loadingChangeStats) "..." else "$filesChanged file${if (filesChanged > 1) "s" else ""} changed"
          val additionsText = if (loadingChangeStats) "..." else "$totalAdditions insertions(+)"
          val deletionsText = if (loadingChangeStats) "..." else "$totalDeletions deletions(-)"

          Text(text = "$changeText, $additionsText, $deletionsText")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Review changes", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))

        if (changes.isLoading) {
          LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        } else {
          repeat(changes.fileChanges.size) { index ->
            val change = changes.fileChanges[index]

            CheckableFileRow(
              name = "${gitViewModel.workingTree.value!!.name}/$change",
              checked = changesToBeCommited.contains(change),
              onCheckedChange = { checked ->
                if (checked) {
                  changesToBeCommited.add(change)
                } else {
                  changesToBeCommited.remove(change)
                }
              }
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Options", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))

        OptionRow(
          text = "Amend previous commit",
          description = "Amend the last commit with the current changes",
          checked = amendCommit,
          onCheckedChange = { /*amendCommit = it*/ }
        )
        OptionRow(
          text = "Sign-off",
          description = "Record a sign-off line in the commit log.",
          checked = signOff,
          onCheckedChange = {
            /*signOff = it*/
            scope.launch { toastHostState.showToast("Not yet implemented") }
          }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
          value = commitMessage,
          onValueChange = { commitMessage = it },
          label = { Text("Message") },
          modifier = Modifier.fillMaxWidth(),
          placeholder = { Text("Enter the commit message") },
          minLines = 6
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = {
            scope.launch(Dispatchers.IO) {
              doCommit(
                message = commitMessage,
                amend = amendCommit,
                sign = signOff,
                only = changesToBeCommited.toTypedArray(),
                onSuccess = {
                  scope.launch {
                    hide()

                    withContext(Dispatchers.Main.immediate + SupervisorJob()) {
                      onSuccess()
                    }
                  }
                },
                onFailure = {
                  scope.launch {
                    hide()

                    onFailure(it)
                  }
                }
              )
            }
          },
          modifier = Modifier.fillMaxWidth(),
          enabled = commitMessage.isNotEmpty()
        ) {
          Text("Commit", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
private fun CheckableFileRow(
  name: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit = {}
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .toggleable(
        value = checked,
        onValueChange = onCheckedChange
      ),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(
      modifier = Modifier
        .padding(horizontal = 16.dp)
        .weight(1f, fill = false)
    ) {
      Text(
        text = name.substringAfterLast("/"),
        style = MaterialTheme.typography.bodyLarge,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1
      )
      Text(
        text = name.substringBeforeLast("/"),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Light,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1
      )
    }

    Checkbox(
      checked = checked,
      onCheckedChange = onCheckedChange
    )
  }
}

@Composable
private fun OptionRow(
  text: String,
  description: String,
  checked: Boolean = false,
  onCheckedChange: (Boolean) -> Unit = {}
) {
  ListItem(
    headlineContent = { Text(text = text) },
    supportingContent = { Text(text = description) },
    trailingContent = {
      Switch(
        checked = checked,
        onCheckedChange = onCheckedChange
      )
    },
    modifier = Modifier
      .padding(0.dp)
      .toggleable(
        value = checked,
        onValueChange = onCheckedChange
      )
  )
}

private fun doCommit(
  message: String,
  amend: Boolean,
  sign: Boolean,
  only: Array<String>,
  onSuccess: () -> Unit = {},
  onFailure: (Throwable) -> Unit = {}
) {
  runCatching {
    git.commit(
      message = message,
      amend = amend,
      sign = sign,
      only = only
    )
  }.onSuccess { onSuccess() }.onFailure(onFailure)
}