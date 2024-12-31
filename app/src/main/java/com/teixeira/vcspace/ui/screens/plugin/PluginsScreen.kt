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

package com.teixeira.vcspace.ui.screens.plugin

import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ErrorOutline
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
import com.blankj.utilcode.util.UriUtils
import com.teixeira.vcspace.app.strings
import com.teixeira.vcspace.plugins.PluginLoader
import com.teixeira.vcspace.ui.LocalToastHostState
import com.teixeira.vcspace.ui.screens.PluginScreens
import com.teixeira.vcspace.ui.screens.plugin.components.InstalledPluginList
import com.teixeira.vcspace.ui.screens.plugin.components.NewPluginButton
import com.teixeira.vcspace.ui.screens.plugin.components.NewPluginSheet
import com.teixeira.vcspace.ui.screens.plugin.components.PluginTabs
import com.teixeira.vcspace.ui.screens.plugin.components.PluginTopBar
import com.teixeira.vcspace.utils.GradleJavaLibraryProjectCreator
import com.teixeira.vcspace.utils.launchWithProgressDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
  val context = LocalContext.current

  LaunchedEffect(Unit) {
    viewModel.loadInstalledPlugins(context)
  }

  val openFile = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
    if (uri != null) {
      coroutineScope.launch {
        val pluginDir = PluginLoader.extractPluginZip(UriUtils.uri2File(uri))
        viewModel.loadInstalledPlugins(
          context = context,
          onSuccessfullyLoaded = {
            toastHostState.showToast(
              message = "Plugin imported successfully",
              icon = Icons.Rounded.Check
            )
          },
          onError = {
            withContext(Dispatchers.Main) {
              toastHostState.showToast(
                message = it.message ?: "Error loading plugin",
                icon = Icons.Rounded.ErrorOutline
              )
            }

            if (pluginDir.exists()) pluginDir.deleteRecursively()
          }
        )
      }
    }
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    topBar = {
      PluginTopBar()
    },
    floatingActionButton = {
      AnimatedVisibility(
        visible = (navBackStackEntry?.destination?.route == PluginScreens.Installed.route)
      ) {
        NewPluginButton(
          expanded = expandedFab,
          onCreatePlugin = { showNewPluginDialog = true },
          onImportPlugin = {
            openFile.launch(
              arrayOf(
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("zip") ?: "application/zip"
              )
            )
          }
        )
      }
    }
  ) { innerPadding ->
    val currentRoute = navBackStackEntry?.destination?.route ?: PluginScreens.Installed.route

    Column(
      modifier = modifier.padding(innerPadding)
    ) {
      PluginTabs(
        currentRoute = currentRoute,
        navController = navController
      )

      NavHost(
        navController = navController,
        startDestination = PluginScreens.Installed.route
      ) {
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
    NewPluginSheet(
      onCreate = { pluginInfo, pluginDir ->
        coroutineScope.launchWithProgressDialog(
          uiContext = context,
          configureBuilder = {
            it.apply {
              setMessage("Creating plugin...")
              setCancelable(false)
            }
          }
        ) { _, _ ->
          GradleJavaLibraryProjectCreator.createGradleJavaLibraryProject(
            context = context,
            baseDir = pluginDir,
            packageName = pluginInfo.packageName!!,
            fullClassName = pluginInfo.mainClass!!
          )
        }.invokeOnCompletion {
          coroutineScope.launch {
            toastHostState.showToast(
              message = context.getString(strings.plugin_created_successfully),
              icon = Icons.Rounded.Check
            )
          }
        }
      },
      onDismiss = { showNewPluginDialog = false }
    )
  }
}