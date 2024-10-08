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

package com.teixeira.vcspace.activities.plugin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.blankj.utilcode.util.FileUtils
import com.google.gson.GsonBuilder
import com.teixeira.vcspace.app.VCSpaceApplication
import com.teixeira.vcspace.preferences.pluginsPath
import com.teixeira.vcspace.screens.PluginScreens
import com.teixeira.vcspace.ui.LocalToastHostState
import com.teixeira.vcspace.viewmodel.PluginViewModel
import com.vcspace.plugins.Plugin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun PluginsScreen(
  modifier: Modifier = Modifier,
  viewModel: PluginViewModel = viewModel(),
  coroutineScope: CoroutineScope = rememberCoroutineScope()
) {
  val installedPluginListState = rememberLazyListState()
  val expandedFab by remember { derivedStateOf { installedPluginListState.firstVisibleItemIndex == 0 } }

  val navController = rememberNavController()
  val navBackStackEntry by navController.currentBackStackEntryAsState()

  var showNewPluginDialog by remember { mutableStateOf(false) }

  val toastHostState = LocalToastHostState.current

  LaunchedEffect(Unit) {
    viewModel.loadInstalledPlugins()
    viewModel.loadPlugins()
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    topBar = {
      PluginTopBar(
        onSettingsChanged = { settings ->
          pluginsPath = settings.pluginPath
          viewModel.loadInstalledPlugins()
        }
      )
    },
    floatingActionButton = {
      AnimatedVisibility(
        visible = (navBackStackEntry?.destination?.route == PluginScreens.Installed.route)
      ) {
        NewPluginButton(
          expanded = expandedFab,
          onClick = { showNewPluginDialog = true }
        )
      }
    }
  ) { innerPadding ->
    val currentRoute = navBackStackEntry?.destination?.route ?: PluginScreens.Explore.route

    Column(
      modifier = modifier.padding(innerPadding)
    ) {
      PluginTabs(
        currentRoute = currentRoute,
        navController = navController
      )

      NavHost(
        navController = navController,
        startDestination = PluginScreens.Explore.route
      ) {
        composable(PluginScreens.Explore.route) {
          ExplorePluginList(
            viewModel = viewModel,
            scope = coroutineScope
          )
        }

        composable(PluginScreens.Installed.route) {
          InstalledPluginList(
            viewModel = viewModel,
            listState = installedPluginListState,
            scope = coroutineScope
          )
        }
      }
    }
  }

  if (showNewPluginDialog) {
    val context = LocalContext.current

    NewPluginDialog(
      onCreate = { manifest ->
        showNewPluginDialog = false

        val newPluginPath = "$pluginsPath/${manifest.packageName}"
        FileUtils.createOrExistsDir(newPluginPath)

        val manifestFile = File("$newPluginPath/manifest.json")
        FileUtils.createOrExistsFile(manifestFile)
        manifestFile.writeText(GsonBuilder().setPrettyPrinting().create().toJson(manifest))

        val pluginFile = File("$newPluginPath/${manifest.name.lowercase().replace(" ", "_")}.java")
        FileUtils.createOrExistsFile(pluginFile)

        context.assets.open("plugin/main.java").bufferedReader().use {
          pluginFile.writeText(it.readText())
        }

        viewModel.addNewInstalledPlugin(
          Plugin(
            manifest = manifest,
            app = VCSpaceApplication.instance,
            fullPath = "$pluginsPath/${manifest.packageName}"
          )
        )

        coroutineScope.launch {
          toastHostState.showToast(
            message = "Plugin created successfully",
            icon = Icons.Rounded.Check
          )
        }
      },
      onDismiss = { showNewPluginDialog = false }
    )
  }
}