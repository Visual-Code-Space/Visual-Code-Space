package com.teixeira.vcspace.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.blankj.utilcode.util.FileUtils
import com.google.gson.GsonBuilder
import com.teixeira.vcspace.activities.editor.EditorActivity
import com.teixeira.vcspace.activities.editor.EditorHandlerActivity
import com.teixeira.vcspace.app.BaseApplication
import com.teixeira.vcspace.extensions.getEmptyActivityBundle
import com.teixeira.vcspace.preferences.appearanceMaterialYou
import com.teixeira.vcspace.preferences.appearanceUIMode
import com.teixeira.vcspace.preferences.pluginsPath
import com.teixeira.vcspace.resources.R
import com.teixeira.vcspace.resources.R.string
import com.teixeira.vcspace.screens.PluginScreen
import com.teixeira.vcspace.ui.ToastHost
import com.teixeira.vcspace.ui.ToastHostState
import com.teixeira.vcspace.ui.rememberToastHostState
import com.teixeira.vcspace.ui.theme.VCSpaceTheme
import com.teixeira.vcspace.utils.isDarkMode
import com.vcspace.plugins.Manifest
import com.vcspace.plugins.Plugin
import com.vcspace.plugins.Script
import com.vcspace.plugins.internal.PluginManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

class PluginsActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val darkTheme = when (appearanceUIMode) {
      AppCompatDelegate.MODE_NIGHT_NO -> false
      AppCompatDelegate.MODE_NIGHT_YES -> true
      AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> isDarkMode()
      else -> isDarkMode()
    }

    enableEdgeToEdge(
      statusBarStyle = when (darkTheme) {
        true -> SystemBarStyle.dark(Color.TRANSPARENT)
        false -> SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
      }
    )
    setContent {
      VCSpaceTheme(
        dynamicColor = appearanceMaterialYou,
        darkTheme = darkTheme
      ) {
        PluginsScreen()
      }
    }
  }
}

@Composable
fun PluginsScreen() {
  val coroutineScope = rememberCoroutineScope()
  val listState = rememberLazyListState()
  val toastHostState = rememberToastHostState()
  val expandedFab by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }
  var plugins by remember { mutableStateOf<List<Plugin>>(emptyList()) }
  var isLoading by remember { mutableStateOf(true) }

  val navController = rememberNavController()
  var selectedTabIndex by remember { mutableIntStateOf(0) }

  LaunchedEffect(Unit) {
    coroutineScope.launch {
      plugins = PluginManager(BaseApplication.instance).getPlugins()
      isLoading = false
    }
  }

  var showNewPluginDialog by remember { mutableStateOf(false) }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TopBar { updatedPath ->
        if (updatedPath != null) {
          pluginsPath = updatedPath
          isLoading = true
          coroutineScope.launch {
            plugins = loadPlugins()
            isLoading = false
          }
        }
      }
    },
    floatingActionButton = {
      AnimatedVisibility(
        visible = selectedTabIndex == 1
      ) {
        NewPluginButton(expandedFab) { showNewPluginDialog = !showNewPluginDialog }
      }
    },
    floatingActionButtonPosition = FabPosition.EndOverlay
  ) { innerPadding ->
    Column {
      TabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = Modifier.padding(innerPadding)
      ) {
        Tab(
          selected = selectedTabIndex == 0,
          onClick = {
            selectedTabIndex = 0
            navController.navigate(PluginScreen.Explore.route)
          },
          text = { Text(PluginScreen.Explore.title) },
          icon = if (PluginScreen.Explore.icon != null) {
            { Icon(PluginScreen.Explore.icon, contentDescription = null) }
          } else {
            null
          }
        )

        Tab(
          selected = selectedTabIndex == 1,
          onClick = {
            selectedTabIndex = 1
            navController.navigate(PluginScreen.Installed.route)
          },
          text = { Text(PluginScreen.Installed.title) },
          icon = if (PluginScreen.Installed.icon != null) {
            { Icon(PluginScreen.Installed.icon, contentDescription = null) }
          } else {
            null
          }
        )
      }
      NavHost(
        navController = navController,
        startDestination = PluginScreen.Explore.route,
        modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
      ) {
        composable(PluginScreen.Explore.route) {
          Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
          ) {
            Text("Nothing to show here yet")
          }
        }
        composable(PluginScreen.Installed.route) {
          if (isLoading) {
            LoadingIndicator()
          } else {
            PluginsList(
              state = listState,
              plugins = plugins,
              scope = coroutineScope,
              toastHostState = toastHostState,
              onDelete = {
                coroutineScope.launch {
                  plugins = PluginManager(BaseApplication.instance).getPlugins()
                }
              }
            )
          }
        }
      }
    }

    ToastHost(hostState = toastHostState)
  }

  if (showNewPluginDialog) {
    NewPluginDialog { manifest ->
      showNewPluginDialog = false

      if (manifest != null) {
        val newPluginPath = "$pluginsPath/${manifest.packageName}"
        FileUtils.createOrExistsDir(newPluginPath)

        val manifestFile = File("$newPluginPath/manifest.json")
        FileUtils.createOrExistsFile(manifestFile)
        manifestFile.writeText(GsonBuilder().setPrettyPrinting().create().toJson(manifest))

        val pluginFile = File("$newPluginPath/${manifest.name.lowercase().replace(" ", "_")}.bsh")
        FileUtils.createOrExistsFile(pluginFile)
        pluginFile.writeText(
          """
          // This is a sample plugin script for Visual Code Space.
          // 
          // This script serves as the entry point for your plugin. The 'main' function is
          // the default method that will be executed when your plugin is loaded and started.
          // 
          // Follow this template to create your own plugin by adding custom logic inside the 'main' function.
          // You can use Android-specific features and classes, as well as interact with the app using
          // provided objects like 'app' and 'helper'.
          // 
          // The 'app' object refers to the Application instance of this app, allowing you to access various
          // application-level features and context. 
          // 
          // The 'helper' object provides additional utility methods to assist with plugin development.
          // 
          // Example Usage:
          // - Display a Toast message to the user
          // - Add custom behavior or UI elements to your app
          // - Interact with existing app components
          // 
          // Modifying the Entry Point:
          // - By default, the entry point is set to the 'main' function.
          // - You can change the entry point by modifying the 'entryPoint' field in the plugin's manifest.
          // - Set 'entryPoint' to the name of any other function in this script that you want to execute when the plugin is loaded.
          // - Example: To use 'startPlugin' as the entry point, set 'entryPoint: "startPlugin"' in the manifest.
      
          void main() {
            // Display a simple Toast message when the plugin is loaded
            Toast.makeText(app, "Hello from plugin!", Toast.LENGTH_SHORT).show();
            
            // Add your custom plugin logic here
            // For example, you could interact with other components of the app, create new UI elements, etc.
            
            // Additional Example 1: Print a message to the logcat
            // Log.d("Plugin", "Plugin is running!");
            
            // Additional Example 2: Start an Android Activity from the plugin
            // Intent intent = new Intent(app, YourTargetActivity.class);
            // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // app.startActivity(intent);
            
            // Remember to keep the 'main' function parameterless for it to be correctly identified and executed.
          }
      
          // Example of an alternative entry point
          void startPlugin() {
            // Custom logic that can be used as the entry point if specified in the manifest
            Toast.makeText(app, "startPlugin function executed!", Toast.LENGTH_SHORT).show();
          }
          
        """.trimIndent()
        )

        plugins = plugins + Plugin(
          manifest = manifest,
          app = BaseApplication.instance,
          fullPath = "$pluginsPath/${manifest.packageName}"
        )

        coroutineScope.launch {
          toastHostState.showToast(
            message = "Plugin created successfully",
            icon = Icons.Rounded.Check
          )
        }
      } else {
        coroutineScope.launch {
          toastHostState.showToast(
            message = "Plugin creation canceled",
            icon = Icons.Rounded.Close
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PluginActionsSheet(
  plugin: Plugin,
  sheetState: SheetState = rememberModalBottomSheetState(),
  scope: CoroutineScope = rememberCoroutineScope(),
  toastHostState: ToastHostState = rememberToastHostState(),
  onDismissSheet: () -> Unit,
  onDelete: () -> Unit
) {
  var showDeleteDialog by remember { mutableStateOf(false) }

  ModalBottomSheet(
    onDismissRequest = onDismissSheet,
    sheetState = sheetState
  ) {
    ElevatedCard(
      modifier = Modifier.padding(16.dp),
      onClick = { showDeleteDialog = !showDeleteDialog }
    ) {
      ListItem(
        headlineContent = { Text("Delete Plugin") },
        leadingContent = { Icon(Icons.Rounded.Delete, contentDescription = null) }
      )
    }
  }

  if (showDeleteDialog) {
    DeletePluginDialog(
      onConfirm = {
        showDeleteDialog = false
        deletePlugin(plugin)
        onDismissSheet()
        onDelete()
        scope.launch {
          toastHostState.showToast(
            message = "Plugin deleted successfully"
          )
        }
      },
      onDismiss = { showDeleteDialog = false }
    )
  }
}

@Composable
fun LoadingIndicator() {
  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    CircularProgressIndicator()
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(onPathUpdated: (String?) -> Unit) {
  var showPluginSettings by remember { mutableStateOf(false) }

  TopAppBar(
    title = { TitleText(stringResource(string.pref_configure_plugins)) },
    navigationIcon = { BackButton() },
    actions = { SettingsButton { showPluginSettings = true } }
  )

  if (showPluginSettings) {
    PluginSettingsDialog {
      showPluginSettings = false
      onPathUpdated(it)
    }
  }
}

@Composable
fun NewPluginButton(expanded: Boolean, onClick: () -> Unit) {
  ExtendedFloatingActionButton(
    onClick = onClick,
    text = { Text("New Plugin") },
    icon = { Icon(Icons.Rounded.Add, contentDescription = "add plugin") },
    expanded = expanded,
    modifier = Modifier
      .imePadding()
  )
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
    Icon(Icons.Rounded.Settings, contentDescription = "settings")
  }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PluginsList(
  modifier: Modifier = Modifier,
  plugins: List<Plugin>,
  state: LazyListState = rememberLazyListState(),
  scope: CoroutineScope = rememberCoroutineScope(),
  toastHostState: ToastHostState = rememberToastHostState(),
  onDelete: () -> Unit = {}
) {
  val pluginIcon = painterResource(R.drawable.ic_plugin)
  var selectedPlugin by remember { mutableStateOf<Plugin?>(null) }
  val context = LocalContext.current

  if (plugins.isEmpty()) {
    NoPluginsFound()
  } else {
    LazyColumn(
      state = state,
      modifier = modifier.fillMaxSize(),
      contentPadding = PaddingValues(vertical = 5.dp, horizontal = 5.dp),
      verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
      items(plugins, key = { it.fullPath }) { plugin ->
        PluginItem(
          plugin,
          modifier = Modifier.animateItemPlacement(),
          onLongClick = { selectedPlugin = it },
          onClick = {
            val intent = Intent(context, EditorActivity::class.java).apply {
              putExtra(EditorHandlerActivity.EXTRA_KEY_PLUGIN_MANIFEST, it.manifest)
              flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            context.startActivity(intent, context.getEmptyActivityBundle())
          },
          onEnabledOrDisabledCallback = {
            scope.launch {
              toastHostState.showToast(
                message = "Restart application",
                icon = Icons.Rounded.Refresh
              )
            }
          }
        )
      }
    }
  }

  selectedPlugin?.let {
    PluginActionsSheet(
      plugin = it,
      scope = scope,
      toastHostState = toastHostState,
      onDismissSheet = { selectedPlugin = null },
      onDelete = onDelete
    )
  }
}

@Composable
fun NoPluginsFound() {
  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = stringResource(string.no_plugins_found),
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
        text = stringResource(string.plugin_settings),
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
    label = { Text(stringResource(string.plugins_path)) },
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
    modifier = Modifier
      .fillMaxWidth()
      .padding(
        vertical = 8.dp,
        horizontal = 0.dp
      ),
    horizontalArrangement = Arrangement.End
  ) {
    OutlinedButton(
      onClick = onCancel,
      modifier = Modifier.padding(end = 4.dp)
    ) { Text(stringResource(string.cancel)) }

    Button(
      onClick = onConfirm,
      enabled = confirmEnabled,
      modifier = Modifier.padding(start = 4.dp)
    ) { Text(stringResource(string.confirm)) }
  }
}

@Composable
fun ConfirmCreateDirectoryDialog(
  onCreate: () -> Unit,
  onCancel: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onCancel,
    title = { Text(stringResource(string.directory_not_found)) },
    text = { Text(stringResource(string.the_directory_does_not_exist_would_you_like_to_create_it)) },
    confirmButton = {
      Button(onClick = onCreate) { Text(stringResource(string.create)) }
    },
    dismissButton = {
      OutlinedButton(onClick = onCancel) { Text(stringResource(string.cancel)) }
    }
  )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PluginItem(
  plugin: Plugin,
  modifier: Modifier = Modifier,
  onLongClick: (Plugin) -> Unit = {},
  onClick: (Plugin) -> Unit = {},
  onEnabledOrDisabledCallback: () -> Unit = {}
) {
  val manifest = plugin.manifest
  val haptics = LocalHapticFeedback.current
  var enabled by remember { mutableStateOf(manifest.enabled) }

  ElevatedCard(
    modifier = modifier
      .clip(CardDefaults.elevatedShape)
      .combinedClickable(
        onClick = { onClick(plugin) },
        onLongClick = {
          haptics.performHapticFeedback(HapticFeedbackType.LongPress)
          onLongClick(plugin)
        },
        interactionSource = remember { MutableInteractionSource() },
        indication = rememberRipple(bounded = true)
      )
  ) {
    ListItem(
      headlineContent = { Text(manifest.name) },
      supportingContent = { Text(manifest.description) },
      trailingContent = {
        Switch(
          checked = enabled,
          onCheckedChange = {
            enabled = it
            val newManifest = manifest.copy(enabled = enabled)
            plugin.saveManifest(newManifest)
            onEnabledOrDisabledCallback()
          },
          thumbContent = if (enabled) {
            {
              Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                modifier = Modifier.size(SwitchDefaults.IconSize)
              )
            }
          } else null
        )
      },
      leadingContent = {
        Icon(
          painter = painterResource(R.drawable.ic_plugin),
          contentDescription = null
        )
      }
    )
  }
}

@Composable
fun NewPluginDialog(onDismiss: (Manifest?) -> Unit) {
  val defaultPackageName = "com.example.plugin"
  val defaultAuthorName = "Unknown"
  val defaultDescription = "No description provided"

  var name by remember { mutableStateOf("") }
  var packageName by remember { mutableStateOf(defaultPackageName) }
  var author by remember { mutableStateOf(defaultAuthorName) }
  var description by remember { mutableStateOf("") }
  var isValid by remember { mutableStateOf(false) }
  var isValidPackageName by remember { mutableStateOf(true) }

  LaunchedEffect(name, packageName) {
    val packageFile = File("$pluginsPath/$packageName")
    isValidPackageName = !packageFile.exists() && packageName.isNotBlank()
    isValid = isValidPackageName && name.isNotBlank()
  }

  Dialog(onDismissRequest = { onDismiss(null) }) {
    ElevatedCard(
      modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight(Alignment.CenterVertically)
        .padding(16.dp),
      shape = RoundedCornerShape(16.dp)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
          text = "New Plugin",
          style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        InputField(
          label = "Name",
          value = name,
          onValueChange = { name = it }
        )

        InputField(
          label = "Package Name",
          value = packageName,
          placeholder = { Text(defaultPackageName) },
          onValueChange = { packageName = it },
          isError = !isValidPackageName
        )

        ErrorMessageText(if (!isValidPackageName) "Package name already exists" else null)

        InputField(
          label = "Author",
          value = author,
          placeholder = { Text(defaultAuthorName) },
          onValueChange = { author = it }
        )

        InputField(
          label = "Description",
          value = description,
          placeholder = { Text(defaultDescription) },
          onValueChange = { description = it },
          singleLine = false,
          maxLines = 3
        )

        Spacer(modifier = Modifier.height(16.dp))

        DialogActions(
          isValid = isValid,
          onDismiss = onDismiss,
          name = name,
          packageName = packageName.ifBlank { defaultPackageName },
          author = author.ifBlank { defaultAuthorName },
          description = description.ifBlank { defaultDescription }
        )
      }
    }
  }
}

@Composable
fun InputField(
  label: String,
  value: String,
  placeholder: @Composable (() -> Unit)? = null,
  onValueChange: (String) -> Unit,
  singleLine: Boolean = true,
  maxLines: Int = 1,
  isError: Boolean = false
) {
  OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    label = { Text(label) },
    placeholder = placeholder,
    isError = isError,
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    singleLine = singleLine,
    maxLines = maxLines,
    keyboardOptions = KeyboardOptions(
      imeAction = if (singleLine) ImeAction.Next else ImeAction.Default
    )
  )
}

@Composable
fun DialogActions(
  isValid: Boolean,
  onDismiss: (Manifest?) -> Unit,
  name: String,
  packageName: String,
  author: String,
  description: String
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.End
  ) {
    OutlinedButton(
      onClick = { onDismiss(null) },
      modifier = Modifier.padding(end = 8.dp)
    ) {
      Text("Cancel")
    }

    Button(
      onClick = {
        if (isValid) {
          val newManifest = Manifest(
            name = name,
            packageName = packageName,
            author = author,
            description = description,
            scripts = arrayOf(
              Script(
                name = "${name.lowercase().replace(" ", "_")}.bsh",
                entryPoint = "main"
              )
            )
          )
          onDismiss(newManifest)
        }
      },
      enabled = isValid
    ) {
      Text("Create")
    }
  }
}

@Composable
fun DeletePluginDialog(
  onConfirm: () -> Unit,
  onDismiss: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Delete Plugin") },
    text = { Text("Are you sure you want to delete this plugin?") },
    confirmButton = {
      Button(onClick = onConfirm) { Text(stringResource(string.confirm)) }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) { Text(stringResource(string.cancel)) }
    }
  )
}

private fun deletePlugin(plugin: Plugin) {
  FileUtils.delete(File(plugin.fullPath))
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
