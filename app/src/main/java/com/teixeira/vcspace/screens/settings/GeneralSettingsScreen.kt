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

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.teixeira.vcspace.core.settings.Settings.General.rememberFollowSystemTheme
import com.teixeira.vcspace.core.settings.Settings.General.rememberIsAmoledMode
import com.teixeira.vcspace.core.settings.Settings.General.rememberIsDarkMode
import com.teixeira.vcspace.core.settings.Settings.General.rememberIsDynamicColor
import com.teixeira.vcspace.resources.R
import me.zhanghai.compose.preference.preferenceCategory
import me.zhanghai.compose.preference.switchPreference

@Composable
fun GeneralSettingsScreen(
  modifier: Modifier = Modifier,
  onNavigateUp: () -> Unit
) {
  val isSystemInDarkTheme = isSystemInDarkTheme()
  val isSystemDarkTheme by remember(isSystemInDarkTheme) { mutableStateOf(isSystemInDarkTheme) }

  val followSystemTheme = rememberFollowSystemTheme()
  val darkMode = rememberIsDarkMode()
  val amoledMode = rememberIsAmoledMode()
  val dynamicColor = rememberIsDynamicColor()

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
      key = "general_settings_category",
      title = { Text(text = stringResource(R.string.general)) }
    )

    switchPreference(
      key = "follow_system_theme_preference",
      title = { Text(text = stringResource(R.string.follow_system_theme_title)) },
      summary = {
        Text(
          text = if (it) stringResource(R.string.follow_system_theme_summary_true)
          else stringResource(R.string.follow_system_theme_summary_false)
        )
      },
      rememberState = { followSystemTheme },
      defaultValue = followSystemTheme.value,
      icon = {
        Icon(
          imageVector = Icons.Default.Palette,
          contentDescription = null
        )
      },
      modifier = Modifier
        .clip(PreferenceShape.Top)
        .background(backgroundColor)
    )

    switchPreference(
      key = "dark_mode_preference",
      title = { Text(text = stringResource(R.string.use_dark_mode_title)) },
      summary = {
        Text(
          text = if (it || (followSystemTheme.value && isSystemDarkTheme))
            stringResource(R.string.use_dark_mode_summary_enabled)
          else stringResource(R.string.use_dark_mode_summary_disabled)
        )
      },
      rememberState = { darkMode },
      defaultValue = darkMode.value,
      enabled = { !followSystemTheme.value },
      icon = {
        Icon(
          imageVector = Icons.Default.Brightness4,
          contentDescription = null
        )
      },
      modifier = Modifier
        .clip(PreferenceShape.Middle)
        .background(backgroundColor)
    )

    switchPreference(
      key = "amoled_mode_preference",
      title = { Text(text = stringResource(R.string.use_amoled_mode_title)) },
      summary = {
        Text(
          text = if ((it && darkMode.value) || (followSystemTheme.value && isSystemDarkTheme && it))
            stringResource(R.string.use_amoled_mode_summary_enabled)
          else stringResource(R.string.use_amoled_mode_summary_disabled)
        )
      },
      rememberState = { amoledMode },
      defaultValue = amoledMode.value,
      enabled = { darkMode.value || (followSystemTheme.value && isSystemDarkTheme) },
      icon = {
        Icon(
          imageVector = Icons.Default.InvertColors,
          contentDescription = null
        )
      },
      modifier = Modifier
        .clip(
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PreferenceShape.Middle
          } else {
            PreferenceShape.Bottom
          }
        )
        .background(backgroundColor)
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      switchPreference(
        key = "dynamic_colors_preference",
        title = { Text(text = stringResource(R.string.dynamic_colors_title)) },
        summary = {
          Text(
            text = if (it) stringResource(R.string.dynamic_colors_summary_enabled)
            else stringResource(R.string.dynamic_colors_summary_disabled)
          )
        },
        rememberState = { dynamicColor },
        defaultValue = dynamicColor.value,
        icon = {
          Icon(
            imageVector = Icons.Default.Palette,
            contentDescription = null
          )
        },
        modifier = Modifier
          .clip(PreferenceShape.Bottom)
          .background(backgroundColor)
      )
    }
  }
}