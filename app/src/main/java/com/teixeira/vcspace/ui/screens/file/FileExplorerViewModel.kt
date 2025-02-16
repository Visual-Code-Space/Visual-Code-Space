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
import com.teixeira.vcspace.events.OnOpenFolderEvent
import com.teixeira.vcspace.events.OnRefreshFolderEvent
import com.teixeira.vcspace.file.File
import com.teixeira.vcspace.git.GitManager
import com.teixeira.vcspace.preferences.defaultPrefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.greenrobot.eventbus.EventBus

class FileExplorerViewModel : ViewModel() {
    private val _openedFolder = MutableStateFlow<File?>(null)
    val openedFolder get() = _openedFolder.asStateFlow()

    private val _isGitRepo = MutableStateFlow(false)
    val isGitRepo get() = _isGitRepo.asStateFlow()

    fun openFolder(path: File) {
        defaultPrefs.edit(commit = true) {
            putString(
                PreferenceKeys.RECENT_FOLDER_5,
                defaultPrefs.getString(PreferenceKeys.RECENT_FOLDER_4, "")
            )
            putString(
                PreferenceKeys.RECENT_FOLDER_4,
                defaultPrefs.getString(PreferenceKeys.RECENT_FOLDER_3, "")
            )
            putString(
                PreferenceKeys.RECENT_FOLDER_3,
                defaultPrefs.getString(PreferenceKeys.RECENT_FOLDER_2, "")
            )
            putString(
                PreferenceKeys.RECENT_FOLDER_2,
                defaultPrefs.getString(PreferenceKeys.RECENT_FOLDER_1, "")
            )
            putString(PreferenceKeys.RECENT_FOLDER_1, path.absolutePath)
        }
        _openedFolder.update { path }
        EventBus.getDefault().post(OnOpenFolderEvent(path))
    }

    fun closeFolder() {
        _openedFolder.update { null }
    }

    private fun updateGitRepoStatus(file: File) {
        _isGitRepo.update {
            file.asRawFile()?.let { jfile ->
                GitManager.isGitRepository(jfile)
                    .also { if (it) GitManager.instance.initialize(jfile) }
            } ?: false
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