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

package com.teixeira.vcspace.ui.screens.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.teixeira.vcspace.activities.AboutActivity
import com.teixeira.vcspace.activities.PluginsActivity
import com.teixeira.vcspace.app.BaseApplication
import com.teixeira.vcspace.app.strings
import com.teixeira.vcspace.extensions.isNotNull
import com.teixeira.vcspace.extensions.isNull
import com.teixeira.vcspace.extensions.open
import com.teixeira.vcspace.github.User
import com.teixeira.vcspace.github.auth.Api
import com.teixeira.vcspace.resources.R
import com.teixeira.vcspace.ui.navigateSingleTop
import com.teixeira.vcspace.ui.screens.SettingScreens
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.preference
import me.zhanghai.compose.preference.preferenceCategory

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val navController = rememberNavController()

    var user: User? by remember { mutableStateOf(null) }
    LaunchedEffect(key1 = true) {
        user = Api.getUserInfo()?.user
    }

    NavHost(navController, startDestination = SettingScreens.Default) {
        composable<SettingScreens.Default> {
            ProvidePreferenceLocals {
                LazyColumn(modifier = modifier.fillMaxSize()) {
                    preferenceCategory(
                        key = "pref_category_configure",
                        title = { Text(stringResource(strings.pref_category_configure)) }
                    )

                    preference(
                        key = "pref_configure_general_key",
                        title = { Text(stringResource(strings.pref_configure_general)) },
                        summary = { Text(stringResource(strings.pref_configure_general_summary)) },
                        onClick = {
                            navController.navigateSingleTop(SettingScreens.General)
                        }
                    )

                    preference(
                        key = "pref_configure_editor_key",
                        title = { Text(stringResource(strings.pref_configure_editor)) },
                        summary = { Text(stringResource(strings.pref_configure_editor_summary)) },
                        onClick = {
                            navController.navigateSingleTop(SettingScreens.Editor)
                        }
                    )

                    preference(
                        key = "pref_configure_file_key",
                        title = { Text(stringResource(strings.pref_configure_file_explorer)) },
                        summary = { Text(stringResource(strings.pref_configure_file_explorer_summary)) },
                        onClick = {
                            navController.navigateSingleTop(SettingScreens.File)
                        }
                    )

                    preference(
                        key = "pref_configure_plugins_key",
                        title = { Text(stringResource(strings.pref_configure_plugins)) },
                        summary = { Text(stringResource(strings.pref_configure_plugins_summary)) },
                        onClick = {
                            context.open(PluginsActivity::class.java)
                        }
                    )

                    preference(
                        key = "pref_configure_git_key",
                        title = {
                            Text(
                                text = if (user.isNull()) {
                                    stringResource(R.string.login_with_github)
                                } else {
                                    stringResource(
                                        R.string.logged_in_as,
                                        user!!.username,
                                        user!!.name ?: ""
                                    )
                                }
                            )
                        },
                        icon = if (user.isNotNull()) {
                            {
                                AsyncImage(
                                    model = user!!.avatarUrl,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(end = 16.dp)
                                        .clip(CircleShape)
                                        .size(40.dp)
                                )
                            }
                        } else null,
                        summary = if (user.isNotNull()) {
                            {
                                Text(
                                    text = user!!.email ?: ""
                                )
                            }
                        } else null,
                        onClick = if (user.isNull()) {
                            {
                                Api.startLogin(uriHandler)
                            }
                        } else {
                            {}
                        }
                    )

                    item { HorizontalDivider(thickness = 2.dp) }

                    preferenceCategory(
                        key = "pref_category_about",
                        title = { Text(stringResource(strings.pref_category_about)) }
                    )

                    preference(
                        key = "pref_about_github_key",
                        title = { Text(stringResource(strings.pref_about_github)) },
                        summary = { Text(stringResource(strings.pref_about_github_summary)) },
                        onClick = {
                            uriHandler.openUri(BaseApplication.REPO_URL)
                        }
                    )

                    preference(
                        key = "about_vcspace",
                        title = { Text(stringResource(strings.open_source_licences)) },
                        onClick = {
                            context.open(AboutActivity::class.java)
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
                    onNavigateUp = navController::navigateUp,
                    onNavigateToMonacoEditorSettings = {
                        navController.navigateSingleTop(
                            SettingScreens.MonacoEditor
                        )
                    }
                )
            }
        }

        composable<SettingScreens.MonacoEditor> {
            ProvidePreferenceLocals {
                MonacoEditorSettingsScreen(
                    modifier = modifier,
                    onNavigateUp = { navController.navigateSingleTop(SettingScreens.Editor) }
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