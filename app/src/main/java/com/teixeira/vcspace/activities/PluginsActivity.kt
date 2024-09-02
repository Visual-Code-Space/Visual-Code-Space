package com.teixeira.vcspace.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.teixeira.vcspace.app.BaseApplication
import com.teixeira.vcspace.preferences.appearanceMaterialYou
import com.teixeira.vcspace.preferences.pluginsPath
import com.teixeira.vcspace.resources.R
import com.teixeira.vcspace.ui.theme.VCSpaceTheme
import com.vcspace.plugins.Plugin
import com.vcspace.plugins.internal.PluginManager
import java.io.File

class PluginsActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      VCSpaceTheme(dynamicColor = appearanceMaterialYou) {
        PluginsScreen()
      }
    }
  }
}

@Composable
fun PluginsScreen() {
  var plugins by remember { mutableStateOf(loadPlugins()) }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TopBar { updatedPath ->
        if (updatedPath != null) {
          pluginsPath = updatedPath
          plugins = loadPlugins()
        }
      }
    }
  ) { innerPadding ->
    PluginsList(
      plugins = plugins,
      modifier = Modifier.padding(innerPadding)
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(onPathUpdated: (String?) -> Unit) {
  var showPluginSettings by remember { mutableStateOf(false) }

  TopAppBar(
    title = { TitleText("Plugins") },
    navigationIcon = { BackButton() },
    actions = { SettingsButton { showPluginSettings = !showPluginSettings } }
  )

  if (showPluginSettings) {
    PluginSettingsDialog {
      showPluginSettings = false
      onPathUpdated(it)
    }
  }
}

@Composable
fun TitleText(title: String) {
  Text(
    text = title,
    modifier = Modifier
      .padding(start = 8.dp)
      .fillMaxWidth(),
    maxLines = 1,
    overflow = TextOverflow.Ellipsis
  )
}

@Composable
fun BackButton() {
  val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
  IconButton(onClick = { backPressedDispatcher?.onBackPressed() }) {
    Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "back")
  }
}

@Composable
fun SettingsButton(onClick: () -> Unit) {
  IconButton(onClick = onClick) {
    Icon(Icons.Filled.Settings, contentDescription = "settings")
  }
}

@Composable
fun PluginsList(plugins: List<Plugin>, modifier: Modifier = Modifier) {
  if (plugins.isEmpty()) {
    NoPluginsFound()
  } else {
    LazyColumn(
      modifier = modifier.fillMaxSize(),
      contentPadding = PaddingValues(
        vertical = 3.dp,
        horizontal = 5.dp
      ),
      verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
      items(plugins.size) { index ->
        val context = LocalContext.current

        PluginItem(plugins[index]) {
          Toast.makeText(
            context,
            "Edit plugin \"${it.manifest.name}\"",
            Toast.LENGTH_SHORT
          ).show()
        }
      }
    }
  }
}

@Composable
fun NoPluginsFound() {
  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = "No plugins found",
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onBackground
    )
  }
}

@Composable
private fun PluginSettingsDialog(onDismiss: (String?) -> Unit) {
  var path by remember { mutableStateOf(pluginsPath) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var showCreateDirDialog by remember { mutableStateOf(false) }

  Dialog(
    onDismissRequest = { onDismiss(path) },
    properties = DialogProperties(
      dismissOnClickOutside = false,
      dismissOnBackPress = false
    )
  ) {
    PluginSettingsContent(
      path = path,
      errorMessage = errorMessage,
      onPathChange = { newPath ->
        path = newPath
        errorMessage = validatePath(newPath)
      },
      onConfirm = {
        val file = File(path)
        if (file.exists()) {
          onDismiss(path)
        } else {
          showCreateDirDialog = true
        }
      },
      onCancel = { onDismiss(null) }
    )
  }

  if (showCreateDirDialog) {
    ConfirmCreateDirectoryDialog(
      onCreate = {
        File(path).mkdirs()
        showCreateDirDialog = false
        onDismiss(path)
      },
      onCancel = { showCreateDirDialog = false }
    )
  }
}

@Composable
fun PluginSettingsContent(
  path: String,
  errorMessage: String?,
  onPathChange: (String) -> Unit,
  onConfirm: () -> Unit,
  onCancel: () -> Unit
) {
  ElevatedCard(
    modifier = Modifier
      .fillMaxWidth()
      .wrapContentHeight(Alignment.CenterVertically)
      .padding(16.dp),
    shape = RoundedCornerShape(16.dp)
  ) {
    Column(
      modifier = Modifier
        .wrapContentSize()
        .padding(16.dp)
    ) {
      Text(
        text = "Plugin Settings",
        style = MaterialTheme.typography.titleLarge
      )

      PluginPathTextField(
        path = path,
        errorMessage = errorMessage,
        onPathChange = onPathChange
      )

      ErrorMessageText(errorMessage)

      ActionButtons(
        onCancel = onCancel,
        onConfirm = onConfirm,
        confirmEnabled = errorMessage == null
      )
    }
  }
}

@Composable
fun PluginPathTextField(
  path: String,
  errorMessage: String?,
  onPathChange: (String) -> Unit
) {
  OutlinedTextField(
    value = path,
    onValueChange = onPathChange,
    label = { Text("Plugins Path") },
    modifier = Modifier.padding(top = 8.dp),
    isError = errorMessage != null,
    keyboardOptions = KeyboardOptions(
      autoCorrect = false,
      imeAction = ImeAction.Done
    )
  )
}

@Composable
fun ErrorMessageText(errorMessage: String?) {
  errorMessage?.let {
    Text(
      text = it,
      color = MaterialTheme.colorScheme.error,
      style = MaterialTheme.typography.bodySmall,
      modifier = Modifier.padding(top = 4.dp)
    )
  }
}

@Composable
fun ActionButtons(
  onCancel: () -> Unit,
  onConfirm: () -> Unit,
  confirmEnabled: Boolean
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.Center
  ) {
    OutlinedButton(
      onClick = onCancel,
      modifier = Modifier.padding(8.dp)
    ) { Text("Cancel") }

    Button(
      onClick = onConfirm,
      enabled = confirmEnabled,
      modifier = Modifier.padding(8.dp)
    ) { Text("Confirm") }
  }
}

@Composable
fun ConfirmCreateDirectoryDialog(
  onCreate: () -> Unit,
  onCancel: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onCancel,
    title = { Text("Directory Not Found") },
    text = { Text("The directory does not exist. Would you like to create it?") },
    confirmButton = {
      Button(onClick = onCreate) { Text("Create") }
    },
    dismissButton = {
      OutlinedButton(onClick = onCancel) { Text("Cancel") }
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
      trailingContent = { Text("v${manifest.versionName}") },
      leadingContent = {
        Icon(
          painter = painterResource(R.drawable.ic_plugin),
          contentDescription = null
        )
      }
    )
  }
}

private fun validatePath(path: String): String? {
  val file = File(path)
  return when {
    !file.isAbsolute -> "Invalid folder path"
    !file.isDirectory && file.exists() -> "Path is not a directory"
    else -> null
  }
}

private fun loadPlugins(): List<Plugin> {
  return PluginManager(BaseApplication.instance).getPlugins()
}
