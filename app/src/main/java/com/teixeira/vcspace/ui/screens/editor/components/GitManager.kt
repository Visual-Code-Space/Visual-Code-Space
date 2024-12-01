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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.sharp.Check
import androidx.compose.material.icons.sharp.ChevronRight
import androidx.compose.material.icons.sharp.Download
import androidx.compose.material.icons.sharp.ErrorOutline
import androidx.compose.material.icons.sharp.NotInterested
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blankj.utilcode.util.ToastUtils
import com.teixeira.vcspace.activities.Editor.LocalEditorDrawerNavController
import com.teixeira.vcspace.app.drawables
import com.teixeira.vcspace.app.strings
import com.teixeira.vcspace.compose.clipUrl
import com.teixeira.vcspace.extensions.makePluralIf
import com.teixeira.vcspace.git.GitActionStatus
import com.teixeira.vcspace.git.GitManager.Companion.instance
import com.teixeira.vcspace.git.GitViewModel
import com.teixeira.vcspace.ui.LocalToastHostState
import com.teixeira.vcspace.ui.ToastDuration
import com.teixeira.vcspace.ui.extensions.harmonizeWithPrimary
import com.teixeira.vcspace.ui.git.AddRemoteSheet
import com.teixeira.vcspace.ui.git.GitCloneDialog
import com.teixeira.vcspace.ui.git.GitCommitSheet
import com.teixeira.vcspace.ui.git.GitInitSheet
import com.teixeira.vcspace.ui.git.PushChangesSheet
import com.teixeira.vcspace.ui.navigateSingleTop
import com.teixeira.vcspace.ui.screens.EditorDrawerScreens
import com.teixeira.vcspace.ui.screens.file.FileExplorerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.Status
import kotlin.time.Duration.Companion.milliseconds

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
    val url = clipUrl()

    GitCloneDialog(
      remoteUrl = url ?: "",
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

          runCatching { instance.addMainBranch() }.onFailure {
            toastHostState.showToast(
              message = it.message ?: "Error",
              icon = Icons.Sharp.ErrorOutline
            )
          }
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
  val workingTree by gitViewModel.workingTree.collectAsStateWithLifecycle(context = Dispatchers.IO)

  val gitActionStatus by gitViewModel.gitStatus.collectAsStateWithLifecycle()
  var showSuccessMessage by remember { mutableStateOf(false) }
  var successMessage by remember { mutableStateOf("") }

  LaunchedEffect(gitActionStatus) {
    if (gitActionStatus is GitActionStatus.Success) {
      successMessage = (gitActionStatus as GitActionStatus.Success).message
      showSuccessMessage = true
    }

    delay(700.milliseconds)
    showSuccessMessage = false
  }

  var status: Status? by remember { mutableStateOf(null) }
  LaunchedEffect(key1 = true) {
    withContext(Dispatchers.IO) {
      status = instance.git.status().call()
    }
  }

  var showSetRemoteSheet by remember { mutableStateOf(false) }
  var showPushChangesSheet by remember { mutableStateOf(false) }
  var showCommitDialog by remember { mutableStateOf(false) }

  val scope = rememberCoroutineScope { Dispatchers.Main }
  val toastHostState = LocalToastHostState.current

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = {
          Column {
            Row(
              horizontalArrangement = Arrangement.spacedBy(3.dp),
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
            ) {
              Text(
                text = repoName ?: "remote not set",
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start,
                modifier = Modifier.clickable {
                  if (repoName == null) {
                    showSetRemoteSheet = true
                  }
                },
              )

              if (repoName != null) {
                Text(
                  text = instance.getDefaultBranch()?.let { "($it)" } ?: "",
                  fontSize = 16.sp
                )
              }
            }

            if (showSuccessMessage && (gitActionStatus !is GitActionStatus.Loading) && (gitActionStatus !is GitActionStatus.Failure)) {
              Text(
                text = successMessage.ifEmpty { "Success" },
                fontSize = 12.sp,
                color = Color.Green.harmonizeWithPrimary()
              )
            }

            if (gitActionStatus is GitActionStatus.Loading) {
              val (progress, message) = gitActionStatus as GitActionStatus.Loading
              val msgFormat = if (progress != null) "$message ($progress%)" else message

              Text(
                text = msgFormat,
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
            } else if (gitActionStatus is GitActionStatus.Failure) {
              val (error) = gitActionStatus as GitActionStatus.Failure

              error.message?.let {
                Text(
                  text = it,
                  fontSize = 12.sp,
                  color = MaterialTheme.colorScheme.error,
                  style = MaterialTheme.typography.bodySmall,
                  modifier = Modifier.verticalScroll(rememberScrollState())
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
        var statusMessage by remember { mutableStateOf("") }

        LaunchedEffect(status) {
          status?.let { gitStatus ->
            if (workingTree == null) return@LaunchedEffect

            statusMessage = if (gitStatus.hasUncommittedChanges()) {
              val fileCount = gitStatus.uncommittedChanges.size

              "${"$fileCount file" makePluralIf (fileCount > 1)} changed"
            } else {
              "No uncommitted changes"
            }
          }
        }

        val statusItems = listOf(
          StatusItem(
            title = "Uncommitted changes",
            subtitle = statusMessage.ifEmpty { "Loading..." },
            icon = if (status?.hasUncommittedChanges() == true) Icons.Sharp.Check else Icons.Sharp.ErrorOutline,
            onClick = {
              if (repoName == null) {
                scope.launch {
                  toastHostState.showToast(
                    message = "Set remote first",
                    icon = Icons.Sharp.ErrorOutline
                  )
                }
              } else if ((status?.hasUncommittedChanges() == false) || statusMessage.isEmpty() || (status?.isClean == true)) {
                scope.launch {
                  toastHostState.showToast(
                    message = "Nothing to commit",
                    icon = Icons.Sharp.NotInterested
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
              if (unpushedCommits.isEmpty()) {
                scope.launch {
                  toastHostState.showToast(
                    message = "Nothing to push",
                    icon = Icons.Sharp.NotInterested
                  )
                }
              } else {
                showPushChangesSheet = true
              }
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
            scope.launch {
              withContext(Dispatchers.IO) {
                status = instance.git.status().call()
              }
              gitViewModel.refresh()
            }
          },
          GitAction("Pull", ImageVector.vectorResource(drawables.source_pull)) {
            scope.launch { gitViewModel.pull() }
          },
          GitAction("Fetch", Icons.Sharp.Download) {
            scope.launch { gitViewModel.fetch() }
          }
        )

        gitActions.forEach { action ->
          GitActionButton(action = action)
        }
      }
    }
  }

  if (showSetRemoteSheet) {
    AddRemoteSheet(
      onDismissRequest = { showSetRemoteSheet = false },
      onSuccess = {
        gitViewModel.refresh()
        // gitViewModel.pull()
      },
      onFailure = {
        ToastUtils.showLong(it.message ?: "Error")
      }
    )
  }

  if (showPushChangesSheet) {
    PushChangesSheet(
      onDismissRequest = { showPushChangesSheet = false },
      commits = unpushedCommits,
      onPushClick = {
        scope.launch {
          gitViewModel.push()
        }
      }
    )
  }

  if (showCommitDialog) {
    GitCommitSheet(
      onDismissRequest = { showCommitDialog = false },
      gitViewModel = gitViewModel,
      onSuccess = {
        gitViewModel.refresh()
      },
      onFailure = {
        toastHostState.showToast(
          message = it.message ?: "Error",
          icon = Icons.Sharp.ErrorOutline
        )
      }
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
          .clickable(onClick = onClick),
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
