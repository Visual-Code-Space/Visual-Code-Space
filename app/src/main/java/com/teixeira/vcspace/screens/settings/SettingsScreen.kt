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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.teixeira.vcspace.activities.plugin.PluginsActivity
import com.teixeira.vcspace.app.BaseApplication
import com.teixeira.vcspace.extensions.open
import com.teixeira.vcspace.resources.R.string
import com.teixeira.vcspace.screens.SettingScreens
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.preference
import me.zhanghai.compose.preference.preferenceCategory

private fun <T : Any> NavHostController.navigateSingleTop(route: T) {
  navigate(route) {
    launchSingleTop = true
    restoreState = true
  }
}

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
  val context = LocalContext.current
  val uriHandler = LocalUriHandler.current
  val navController = rememberNavController()

  NavHost(navController, startDestination = SettingScreens.Default) {
    composable<SettingScreens.Default> {
      ProvidePreferenceLocals {
        LazyColumn(modifier = modifier.fillMaxSize()) {
          preferenceCategory(
            key = "pref_category_configure",
            title = { Text(stringResource(string.pref_category_configure)) }
          )

          preference(
            key = "pref_configure_general_key",
            title = { Text(stringResource(string.pref_configure_general)) },
            summary = { Text(stringResource(string.pref_configure_general_summary)) },
            onClick = {
              navController.navigateSingleTop(SettingScreens.General)
            }
          )

          preference(
            key = "pref_configure_editor_key",
            title = { Text(stringResource(string.pref_configure_editor)) },
            summary = { Text(stringResource(string.pref_configure_editor_summary)) },
            onClick = {
              navController.navigateSingleTop(SettingScreens.Editor)
            }
          )

          preference(
            key = "pref_configure_file_key",
            title = { Text(stringResource(string.pref_configure_file_explorer)) },
            summary = { Text(stringResource(string.pref_configure_file_explorer_summary)) },
            onClick = {
              navController.navigateSingleTop(SettingScreens.File)
            }
          )

          preference(
            key = "pref_configure_plugins_key",
            title = { Text(stringResource(string.pref_configure_plugins)) },
            summary = { Text(stringResource(string.pref_configure_plugins_summary)) },
            onClick = {
              context.open(PluginsActivity::class.java)
            }
          )

          item { HorizontalDivider(thickness = 2.dp) }

          preferenceCategory(
            key = "pref_category_about",
            title = { Text(stringResource(string.pref_category_about)) }
          )

          preference(
            key = "pref_about_github_key",
            title = { Text(stringResource(string.pref_about_github)) },
            summary = { Text(stringResource(string.pref_about_github_summary)) },
            onClick = {
              uriHandler.openUri(BaseApplication.REPO_URL)
            }
          )
        }
      }
    }

    composable<SettingScreens.General> {
      ProvidePreferenceLocals {
        GeneralSettingsScreen(
          modifier = modifier,
          onNavigateUp = navController::navigateUp
        )
      }
    }

    composable<SettingScreens.File> {
      ProvidePreferenceLocals {
        FileSettingsScreen(
          modifier = modifier,
          onNavigateUp = navController::navigateUp
        )
      }
    }

    composable<SettingScreens.Editor> {
      ProvidePreferenceLocals {
        EditorSettingsScreen(
          modifier = modifier,
          onNavigateUp = navController::navigateUp
        )
      }
    }
  }
}

object PreferenceShape {
  val Top = RoundedCornerShape(
    topStart = 24.dp,
    topEnd = 24.dp,
    bottomStart = 4.dp,
    bottomEnd = 4.dp
  )

  val Middle = RoundedCornerShape(
    topStart = 4.dp,
    topEnd = 4.dp,
    bottomStart = 4.dp,
    bottomEnd = 4.dp
  )

  val Bottom = RoundedCornerShape(
    topStart = 4.dp,
    topEnd = 4.dp,
    bottomStart = 24.dp,
    bottomEnd = 24.dp
  )

  val Alone = RoundedCornerShape(24.dp)
}