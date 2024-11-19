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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.sharp.Check
import androidx.compose.material.icons.sharp.ChevronRight
import androidx.compose.material.icons.sharp.Download
import androidx.compose.material.icons.sharp.ErrorOutline
import androidx.compose.material.icons.sharp.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.ToastUtils
import com.teixeira.vcspace.activities.Editor.LocalEditorDrawerNavController
import com.teixeira.vcspace.app.drawables
import com.teixeira.vcspace.app.strings
import com.teixeira.vcspace.extensions.makePluralIf
import com.teixeira.vcspace.git.ChangeStats
import com.teixeira.vcspace.git.GitActionStatus
import com.teixeira.vcspace.git.GitViewModel
import com.teixeira.vcspace.ui.LocalToastHostState
import com.teixeira.vcspace.ui.ToastDuration
import com.teixeira.vcspace.ui.extensions.harmonizeWithPrimary
import com.teixeira.vcspace.ui.git.AddRemoteSheet
import com.teixeira.vcspace.ui.git.GitCloneDialog
import com.teixeira.vcspace.ui.git.GitCommitSheet
import com.teixeira.vcspace.ui.git.GitInitSheet
import com.teixeira.vcspace.ui.navigateSingleTop
import com.teixeira.vcspace.ui.screens.EditorDrawerScreens
import com.teixeira.vcspace.ui.screens.file.FileExplorerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun GitManager(
  fileExplorerViewModel: FileExplorerViewModel,
  gitViewModel: GitViewModel = viewModel()
) {
  val scope = rememberCoroutineScope()
  val toastHostState = LocalToastHostState.current
  val navController = LocalEditorDrawerNavController.current

  val isGitRepo by fileExplorerViewModel.isGitRepo.collectAsStateWithLifecycle()
  val openedFolder by fileExplorerViewModel.openedFolder.collectAsStateWithLifecycle()

  LaunchedEffect(key1 = true) {
    fileExplorerViewModel.checkIfGitRepo()
  }

  val workingTree by gitViewModel.workingTree.collectAsStateWithLifecycle()
  val changes by gitViewModel.changes.collectAsStateWithLifecycle(context = Dispatchers.IO)

  LaunchedEffect(key1 = isGitRepo, workingTree) {
    if (isGitRepo && workingTree != null) {
      gitViewModel.loadChangeStats {
        scope.launch {
          val errorMessage =
            "Failed to retrieve uncommitted changes stats: ${it.message ?: "Unknown error occurred"}."
          toastHostState.showToast(
            message = errorMessage,
            duration = ToastDuration.Long
          )
        }
      }
    }
  }

  LaunchedEffect(key1 = isGitRepo, workingTree) {
    if (isGitRepo && workingTree != null) {
      withContext(Dispatchers.IO) {
        gitViewModel.loadRepoName()
        gitViewModel.loadUnpushedCommits()
        gitViewModel.loadChanges()
      }
    }
  }

  LaunchedEffect(openedFolder, isGitRepo) {
    if (isGitRepo && openedFolder != null) {
      gitViewModel.open(openedFolder ?: return@LaunchedEffect)
    }
  }

  var showGitCloneDialog by remember { mutableStateOf(false) }
  var showGitInitDialog by remember { mutableStateOf(false) }

  openedFolder?.let {
    if (isGitRepo) {
      GitManagerContent(gitViewModel = gitViewModel)
    } else {
      NoRepoFound(
        onInitClick = { showGitInitDialog = true },
        onCloneClick = { showGitCloneDialog = true }
      )
    }
  } ?: run {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier.fillMaxSize()
    ) {
      CloneButton { showGitCloneDialog = true }
    }
  }

  if (showGitCloneDialog) {
    val clip = ClipboardUtils.getText().toString()

    GitCloneDialog(
      remoteUrl = if (URLUtil.isValidUrl(clip)) clip else "",
      initialFolder = openedFolder,
      onDismissRequest = { showGitCloneDialog = false },
      onCloneSuccess = {
        scope.launch {
          toastHostState.showToast(
            message = "Successfully cloned",
            icon = Icons.Rounded.Check
          )
          fileExplorerViewModel.openFolder(it)

          withContext(Dispatchers.Main) {
            navController.navigateSingleTop(EditorDrawerScreens.FileExplorer)
          }
        }
      },
      onCloneFailure = {
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

  if (showGitInitDialog) {
    openedFolder?.let { folder ->
      val successMessage =
        stringResource(strings.initialized_empty_git_repo_in, folder.absolutePath)

      GitInitSheet(
        folder = folder,
        onDismissRequest = { showGitInitDialog = false },
        onSuccess = {
          navController.navigateSingleTop(EditorDrawerScreens.FileExplorer)
          fileExplorerViewModel.refreshFolder()

          toastHostState.showToast(
            message = successMessage,
            icon = Icons.Sharp.Check
          )
        },
        onFailure = { throwable ->
          toastHostState.showToast(
            message = throwable.message ?: "Error",
            icon = Icons.Sharp.ErrorOutline
          )
        }
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GitManagerContent(
  gitViewModel: GitViewModel
) {
  val repoName by gitViewModel.repoName.collectAsStateWithLifecycle(context = Dispatchers.IO)
  val unpushedCommits by gitViewModel.unpushedCommits.collectAsStateWithLifecycle(context = Dispatchers.IO)
  val gitChangeStats by gitViewModel.changeStats.collectAsStateWithLifecycle(context = Dispatchers.IO)

  val gitActionStatus by gitViewModel.gitStatus.collectAsStateWithLifecycle()

  var showSetRemoteDialog by remember { mutableStateOf(false) }
  var showCommitDialog by remember { mutableStateOf(false) }

  val scope = rememberCoroutineScope { Dispatchers.Main }
  val toastHostState = LocalToastHostState.current

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = repoName ?: "remote not set",
              fontWeight = FontWeight.SemiBold,
              textAlign = TextAlign.Start,
              modifier = Modifier.clickable {
                if (repoName == null) {
                  showSetRemoteDialog = true
                }
              },
            )

            if (gitActionStatus is GitActionStatus.Loading) {
              val (progress, message) = gitActionStatus as GitActionStatus.Loading

              Text(
                text = message,
                fontSize = 12.sp
              )

              if (progress != null) {
                LinearProgressIndicator(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp),
                  progress = { progress / 100f }
                )
              } else {
                LinearProgressIndicator(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp)
                )
              }
            }
          }
        }
      )
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      // Git Status Items
      Column {
        val (filesChanged, totalAdditions, totalDeletions) = gitChangeStats.changeStats
          ?: ChangeStats(0, 0, 0)
        val loadingChangeStats = gitChangeStats.isLoading

        val changeText = if (loadingChangeStats) {
          "... file"
        } else "$filesChanged file" makePluralIf (filesChanged > 1)

        val additionsText = if (loadingChangeStats) {
          "... insertion"
        } else "$totalAdditions insertion" makePluralIf (totalAdditions > 1)

        val deletionsText = if (loadingChangeStats) {
          "... deletion"
        } else "$totalDeletions deletion" makePluralIf (totalDeletions > 1)

        val statusItems = listOf(
          StatusItem(
            title = "Uncommitted changes",
            subtitle = "$changeText, $additionsText, $deletionsText",
            icon = if (filesChanged != 0) Icons.Sharp.Check else Icons.Sharp.ErrorOutline,
            onClick = {
              if (repoName == null) {
                scope.launch {
                  toastHostState.showToast(
                    message = "Set remote first",
                    icon = Icons.Sharp.ErrorOutline
                  )
                }
              } else {
                showCommitDialog = true
              }
            }
          ),
          StatusItem(
            title = "Unpushed commits",
            subtitle = "${unpushedCommits.size} commit" makePluralIf (unpushedCommits.size > 1),
            icon = if (unpushedCommits.isNotEmpty()) Icons.Sharp.Check else Icons.Sharp.ErrorOutline,
            onClick = {
              scope.launch { toastHostState.showToast("Not yet implemented") }
            }
          ),
        )

        statusItems.forEach {
          StatusRow(item = it)
        }
      }

      // Git Actions
      Column(modifier = Modifier.padding(top = 16.dp)) {
        val gitActions = listOf(
          GitAction("Refresh", Icons.Sharp.Refresh) {
            scope.launch { toastHostState.showToast("Not yet implemented") }
          },
          GitAction("Pull", ImageVector.vectorResource(drawables.source_pull)) {
            scope.launch { toastHostState.showToast("Not yet implemented") }
          },
          GitAction("Fetch", Icons.Sharp.Download) {
            scope.launch { toastHostState.showToast("Not yet implemented") }
          }
        )

        gitActions.forEach { action ->
          GitActionButton(action = action)
        }
      }
    }
  }

  if (showSetRemoteDialog) {
    AddRemoteSheet(
      onDismissRequest = { showSetRemoteDialog = false },
      onSuccess = { gitViewModel.loadRepoName() },
      onFailure = {
        ToastUtils.showLong(it.message ?: "Error")
      }
    )
  }

  if (showCommitDialog) {
    GitCommitSheet(
      onDismissRequest = { showCommitDialog = false }
    )
  }
}

@Composable
private fun GitActionButton(action: GitAction) {
  FilledTonalButton(
    onClick = action.onClick,
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = (0.5).dp),
    shape = MaterialTheme.shapes.small,
    colors = ButtonDefaults.filledTonalButtonColors(
      containerColor = MaterialTheme.colorScheme.secondary.harmonizeWithPrimary(),
      contentColor = MaterialTheme.colorScheme.onSecondary
    )
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(action.text, modifier = Modifier.weight(1f))
      Icon(action.icon, contentDescription = action.text)
    }
  }
}

data class GitAction(
  val text: String,
  val icon: ImageVector,
  val onClick: () -> Unit
)

data class StatusItem(
  val title: String,
  val subtitle: String,
  val icon: ImageVector,
  val onClick: () -> Unit
)

@Composable
fun StatusRow(item: StatusItem) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 4.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
    ),
    onClick = item.onClick
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(text = item.title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Text(text = item.subtitle, fontSize = 14.sp)
      }
      Icon(item.icon, contentDescription = item.title)
    }
  }
}

@Composable
private fun CloneButton(onClick: () -> Unit) {
  Button(onClick = onClick) {
    Text(stringResource(strings.git_clone))
  }
}

@Composable
fun NoRepoFound(
  modifier: Modifier = Modifier,
  onInitClick: () -> Unit = {},
  onCloneClick: () -> Unit = {}
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp)
  ) {
    Text(
      text = "Initialize a new git repository or clone an existing one",
      fontWeight = FontWeight.SemiBold,
      style = MaterialTheme.typography.titleLarge
    )

    Spacer(modifier = Modifier.height(12.dp))

    val options = listOf(
      "Initialize a new git repository" to onInitClick,
      "Clone a repository from the internet" to onCloneClick
    )

    options.forEach { (text, onClick) ->
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(MaterialTheme.shapes.small)
          .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = text,
          fontSize = 16.sp,
          fontWeight = FontWeight.Medium,
          modifier = Modifier
            .weight(1f)
            .padding(16.dp)
        )
        Icon(Icons.Sharp.ChevronRight, contentDescription = null)
      }
    }
  }
}
