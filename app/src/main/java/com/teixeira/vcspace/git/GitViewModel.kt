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

package com.teixeira.vcspace.git

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import org.eclipse.jgit.revwalk.RevCommit
import java.io.File
import com.teixeira.vcspace.git.GitManager.Companion.instance as git

private typealias OnFailureListener = (Throwable) -> Unit

class GitViewModel : ViewModel() {
  private val _workingTree = MutableStateFlow<File?>(null)
  val workingTree get() = _workingTree.asStateFlow()

  private val _gitStatus = MutableStateFlow<GitActionStatus>(GitActionStatus.Idle)
  val gitStatus get() = _gitStatus.asStateFlow()

  fun open(folder: File, onFailure: OnFailureListener = {}) {
    _workingTree.value = folder
    runCatching { git.initialize(folder) }.onFailure(onFailure)
  }

  fun close() {
    _workingTree.value = null
    git.close()
  }

  private val _changes = MutableStateFlow(GitChanges())
  val changes get() = _changes.asStateFlow()

  suspend fun loadChanges(onFailure: OnFailureListener = {}) {
    _changes.update {
      it.copy(isLoading = true)
    }
    _gitStatus.update { GitActionStatus.Loading(null, "Loading changes") }

    withContext(Dispatchers.IO) {
      runCatching {
        git.getChanges()
      }.onSuccess { changes ->
        _changes.update {
          it.copy(isLoading = false, fileChanges = changes)
        }
        _gitStatus.update { GitActionStatus.Success }
      }.onFailure { throwable ->
        withContext(Dispatchers.Main) {
          onFailure(throwable)
        }

        _changes.update {
          it.copy(isLoading = false, fileChanges = emptyList())
        }
        _gitStatus.update { GitActionStatus.Failure(throwable) }
      }
    }
  }

  private var _repoName = MutableStateFlow<String?>(null)
  val repoName get() = _repoName.asStateFlow()

  suspend fun loadRepoName(onFailure: OnFailureListener = {}) {
    _gitStatus.update { GitActionStatus.Loading(null, "Loading repository name") }

    withContext(Dispatchers.IO) {
      runCatching {
        git.getRepositoryName()
      }.onSuccess { name ->
        _repoName.update { name }

        _gitStatus.update { GitActionStatus.Success }
      }.onFailure { throwable ->
        _gitStatus.update { GitActionStatus.Failure(throwable) }

        withContext(Dispatchers.Main) {
          onFailure(throwable)
        }
      }
    }
  }

  private var _unpushedCommits = MutableStateFlow<List<RevCommit>>(emptyList())
  val unpushedCommits get() = _unpushedCommits.asStateFlow()

  suspend fun loadUnpushedCommits(onFailure: OnFailureListener = {}) {
    withContext(Dispatchers.IO) {
      runCatching {
        git.getUnpushedCommits()
      }.onSuccess { commits ->
        _unpushedCommits.update { commits }
      }.onFailure { throwable ->
        withContext(Dispatchers.Main) {
          onFailure(throwable)
        }
      }
    }
  }

  private val _changeStats = MutableStateFlow(GitChangeStats())
  val changeStats get() = _changeStats.asStateFlow()

  suspend fun loadChangeStats(onFailure: OnFailureListener = {}) {
    _changeStats.update {
      it.copy(isLoading = true)
    }
    _gitStatus.update { GitActionStatus.Loading(null, "Loading changes stats") }

    withContext(Dispatchers.IO) {
      runCatching {
        git.getUncommittedChangesStats()
      }.onSuccess { stats ->
        _changeStats.update {
          it.copy(isLoading = false, changeStats = stats)
        }
        _gitStatus.update { GitActionStatus.Success }
      }.onFailure { throwable ->
        _changeStats.update {
          it.copy(isLoading = false)
        }
        _gitStatus.update { GitActionStatus.Failure(throwable) }

        withContext(Dispatchers.Main) {
          onFailure(throwable)
        }
      }
    }
  }
}

data class GitChanges(
  val isLoading: Boolean = false,
  val fileChanges: List<String> = emptyList()
)

data class GitChangeStats(
  val isLoading: Boolean = false,
  val changeStats: ChangeStats? = null
)
