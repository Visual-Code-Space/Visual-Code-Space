package com.teixeira.vcspace.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.teixeira.vcspace.app.BaseApplication
import com.teixeira.vcspace.preferences.appearanceMaterialYou
import com.teixeira.vcspace.ui.theme.VCSpaceTheme
import com.vcspace.plugins.Manifest
import com.vcspace.plugins.Plugin
import com.vcspace.plugins.internal.PluginManager

class PluginsActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      VCSpaceTheme(dynamicColor = appearanceMaterialYou) {
        Scaffold(
          modifier = Modifier.fillMaxSize(),
          topBar = { TopBar() }
        ) { innerPadding ->
          val plugins = PluginManager(BaseApplication.instance).getPlugins()
          LazyColumn(
            modifier = Modifier
              .padding(innerPadding)
              .fillMaxSize(),
            contentPadding = PaddingValues(
              vertical = 3.dp,
              horizontal = 5.dp
            ),
            verticalArrangement = Arrangement.spacedBy(3.dp)
          ) {
            items(plugins.size) {
              val plugin = plugins[it]
              val context = LocalContext.current

              PluginItem(plugin) {
                Toast.makeText(
                  context,
                  "Edit plugin \"${plugin.manifest.name}\"",
                  Toast.LENGTH_SHORT
                ).show()
              }
            }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
  TopAppBar(
    title = {
      Text(
        text = "Plugins",
        modifier = Modifier
          .padding(start = 8.dp)
          .fillMaxWidth(),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    },
    navigationIcon = {
      val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
      IconButton(onClick = {
        backPressedDispatcher?.onBackPressed()
      }) {
        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "back")
      }
    }
  )
}

@Composable
fun PluginItem(plugin: Plugin, onClick: (Plugin) -> Unit = {}) {
  val manifest = plugin.manifest
  ElevatedCard(onClick = { onClick(plugin) }) {
    ListItem(
      headlineContent = { Text(manifest.name) },
      supportingContent = { Text(manifest.description) },
      trailingContent = { Text(manifest.versionName) }
    )
    // HorizontalDivider()
  }
}
