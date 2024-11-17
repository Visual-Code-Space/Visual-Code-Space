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

package com.teixeira.vcspace.ui.screens.file

import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import com.teixeira.vcspace.PreferenceKeys
import com.teixeira.vcspace.events.OnRefreshFolderEvent
import com.teixeira.vcspace.git.GitManager
import com.teixeira.vcspace.preferences.defaultPrefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.greenrobot.eventbus.EventBus
import java.io.File

class FileExplorerViewModel : ViewModel() {
  private val _openedFolder = MutableStateFlow<File?>(null)
  val openedFolder get() = _openedFolder.asStateFlow()

  private val _isGitRepo = MutableStateFlow(false)
  val isGitRepo get() = _isGitRepo.asStateFlow()

  fun openFolder(path: File) {
    defaultPrefs.edit(commit = true) {
      putString(PreferenceKeys.RECENT_FOLDER, path.absolutePath)
    }
    _openedFolder.update { path }
  }

  fun closeFolder() {
    _openedFolder.update { null }
  }

  private fun updateGitRepoStatus(file: File) {
    _isGitRepo.update {
      GitManager.isGitRepository(file).also { if (it) GitManager.instance.initialize(file) }
    }
  }

  fun checkIfGitRepo() {
    _openedFolder.value?.let { file ->
      updateGitRepoStatus(file)
    }
  }

  fun refreshFolder() {
    _openedFolder.value?.let {
      updateGitRepoStatus(it)
      EventBus.getDefault().post(OnRefreshFolderEvent(it))
    }
  }
}