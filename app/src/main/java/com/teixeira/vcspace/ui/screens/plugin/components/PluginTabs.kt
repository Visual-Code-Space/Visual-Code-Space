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

package com.teixeira.vcspace.ui.screens.plugin.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.teixeira.vcspace.ui.screens.PluginScreens

@Composable
fun PluginTabs(
  modifier: Modifier = Modifier,
  currentRoute: String,
  navController: NavController
) {
  val tabItems = listOf(PluginScreens.Installed)
  val tabIndices = mapOf(PluginScreens.Installed.route to 0)

  TabRow(
    modifier = modifier,
    selectedTabIndex = tabIndices[currentRoute] ?: 0
  ) {
    tabItems.forEach { screen ->
      Tab(
        selected = currentRoute == screen.route,
        onClick = {
          navController.navigate(screen.route) {
            popUpTo(navController.graph.startDestinationId) {
              saveState = true
            }
            launchSingleTop = true
            restoreState = true
          }
        },
        text = { Text(screen.title) },
        selectedContentColor = MaterialTheme.colorScheme.primary,
        unselectedContentColor = MaterialTheme.colorScheme.onBackground,
      )
    }
  }
}
