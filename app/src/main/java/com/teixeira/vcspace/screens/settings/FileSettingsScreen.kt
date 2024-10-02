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

package com.teixeira.vcspace.screens.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.teixeira.vcspace.core.settings.Settings.File.rememberLastOpenedFile
import com.teixeira.vcspace.core.settings.Settings.File.rememberShowHiddenFiles
import com.teixeira.vcspace.resources.R
import me.zhanghai.compose.preference.preferenceCategory
import me.zhanghai.compose.preference.switchPreference

@Composable
fun FileSettingsScreen(
  modifier: Modifier = Modifier,
  onNavigateUp: () -> Unit
) {
  val showHiddenFiles = rememberShowHiddenFiles()
  val rememberLastOpenedFile = rememberLastOpenedFile()

  BackHandler(onBack = onNavigateUp)

  val backgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)

  LazyColumn(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp)
      .padding(bottom = 12.dp),
    verticalArrangement = Arrangement.spacedBy(3.dp)
  ) {
    preferenceCategory(
      key = "file_settings_category",
      title = { Text(text = stringResource(R.string.file_settings)) }
    )

    switchPreference(
      key = "show_hidden_files_preference",
      title = { Text(text = stringResource(R.string.show_hidden_files_title)) },
      summary = {
        Text(
          text = if (it) stringResource(R.string.show_hidden_files_summary_true)
          else stringResource(R.string.show_hidden_files_summary_false)
        )
      },
      rememberState = { showHiddenFiles },
      defaultValue = showHiddenFiles.value,
      icon = {
        Icon(
          imageVector = Icons.Default.VisibilityOff,
          contentDescription = null
        )
      },
      modifier = Modifier
        .clip(PreferenceShape.Top)
        .background(backgroundColor)
    )

    switchPreference(
      key = "remember_last_opened_file_preference",
      title = { Text(text = stringResource(R.string.remember_last_opened_file_title)) },
      summary = {
        Text(
          text = if (it) stringResource(R.string.remember_last_opened_file_summary_true)
          else stringResource(R.string.remember_last_opened_file_summary_false)
        )
      },
      rememberState = { rememberLastOpenedFile },
      defaultValue = rememberLastOpenedFile.value,
      icon = {
        Icon(
          imageVector = Icons.Default.History,
          contentDescription = null
        )
      },
      modifier = Modifier
        .clip(PreferenceShape.Bottom)
        .background(backgroundColor)
    )
  }
}