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

package com.teixeira.vcspace.ui.screens.editor

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.teixeira.vcspace.activities.Editor.LocalCommandPaletteManager
import com.teixeira.vcspace.activities.Editor.LocalEditorDrawerState
import com.teixeira.vcspace.keyboard.CommandPaletteManager
import com.teixeira.vcspace.core.components.editor.FileTabLayout
import com.teixeira.vcspace.core.settings.Settings.Editor.rememberColorScheme
import com.teixeira.vcspace.core.settings.Settings.Editor.rememberDeleteIndentOnBackspace
import com.teixeira.vcspace.core.settings.Settings.Editor.rememberDeleteLineOnBackspace
import com.teixeira.vcspace.core.settings.Settings.Editor.rememberFontFamily
import com.teixeira.vcspace.core.settings.Settings.Editor.rememberFontLigatures
import com.teixeira.vcspace.core.settings.Settings.Editor.rememberFontSize
import com.teixeira.vcspace.core.settings.Settings.Editor.rememberIndentSize
import com.teixeira.vcspace.core.settings.Settings.Editor.rememberLineNumber
import com.teixeira.vcspace.core.settings.Settings.Editor.rememberStickyScroll
import com.teixeira.vcspace.core.settings.Settings.Editor.rememberUseTab
import com.teixeira.vcspace.core.settings.Settings.Editor.rememberWordWrap
import com.teixeira.vcspace.core.settings.Settings.File.rememberLastOpenedFile
import com.teixeira.vcspace.core.settings.Settings.File.rememberShowHiddenFiles
import com.teixeira.vcspace.core.settings.Settings.General.rememberFollowSystemTheme
import com.teixeira.vcspace.core.settings.Settings.General.rememberIsDarkMode
import com.teixeira.vcspace.core.settings.Settings.General.rememberIsDynamicColor
import com.teixeira.vcspace.editor.VCSpaceEditor
import com.teixeira.vcspace.resources.R
import com.teixeira.vcspace.ui.components.keyboard.CommandPalette
import com.teixeira.vcspace.ui.screens.file.FileExplorerViewModel
import com.teixeira.vcspace.ui.theme.atLeastS
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun EditorScreen(
  viewModel: EditorViewModel = viewModel(),
  fileExplorerViewModel: FileExplorerViewModel = viewModel(),
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val editorConfigMap = remember { viewModel.editorConfigMap }

  val openedFiles = uiState.openedFiles
  val selectedFileIndex = uiState.selectedFileIndex

  LaunchedEffect(selectedFileIndex) {
    viewModel.rememberLastFiles()
  }

  val openLastFiles by rememberLastOpenedFile()
  val showHiddenFiles by rememberShowHiddenFiles()
  val isDynamicColor by rememberIsDynamicColor()

  DisposableEffect(openLastFiles) {
    /*if (openLastFiles) {*/
    for (file in viewModel.lastOpenedFiles()) {
      viewModel.addFile(file)
      fileExplorerViewModel.setCurrentPath(file.absolutePath, showHiddenFiles)
    }
    /*}*/

    onDispose {
      viewModel.rememberLastFiles()
    }
  }

  val context = LocalContext.current
  val commandPaletteManager = LocalCommandPaletteManager.current

  Column(modifier = modifier.onKeyEvent {
    if (it.isCtrlPressed && it.isShiftPressed && it.key == Key.P) {
      println("Ctrl + Shift + P is pressed")
      commandPaletteManager.show()
      return@onKeyEvent true
    }

    if (it.type == KeyEventType.KeyDown) {
      CommandPaletteManager.instance.applyKeyBindings(it)
      return@onKeyEvent true
    }

    false
  }) {
    FileTabLayout(
      editorViewModel = viewModel,
      fileExplorerViewModel = fileExplorerViewModel
    )

    val openedFile = openedFiles.getOrNull(selectedFileIndex)

    openedFile?.let { fileEntry ->
      val editorView = viewModel.getEditorForFile(context, fileEntry.file)

      key(editorConfigMap[fileEntry.file.path]) {
        configureEditor(editorView.editor)
        if (isDynamicColor && atLeastS) {
          editorView.applyDynamicColor(context as Activity)
        }
        viewModel.setEditorConfiguredForFile(fileEntry.file)
      }

      key(fileEntry.file.path) {
        AndroidView(
          factory = { editorView },
          modifier = Modifier.fillMaxSize()
        )
      }
    } ?: run {
      val tempFile = File(context.cacheDir, "untitled.txt")
      viewModel.addFile(tempFile)

      // NoOpenedFiles()
    }

    if (commandPaletteManager.showCommandPalette.value) {
      CommandPalette(
        commands = commandPaletteManager.allCommands,
        recentlyUsedCommands = commandPaletteManager.recentlyUsedCommands,
        onCommandSelected = { command ->
          commandPaletteManager.hide()

          // do something
        },
        onDismissRequest = { commandPaletteManager.hide() }
      )
    }
  }
}

@Composable
fun NoOpenedFiles() {
  val drawerState = LocalEditorDrawerState.current
  val scope = rememberCoroutineScope()

  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Text(
      text = "No opened files",
      style = MaterialTheme.typography.titleLarge
    )

    Spacer(modifier = Modifier.height(5.dp))

    ElevatedButton(onClick = {
      scope.launch { drawerState.open() }
    }) {
      Text("Open files")
    }
  }
}

@Composable
private fun configureEditor(editor: VCSpaceEditor) {
  configureFontSettings(editor)
  configureColorScheme(editor)
  configureIndentation(editor)
  configureMiscSettings(editor)
}

@Composable
private fun configureFontSettings(editor: VCSpaceEditor) {
  val fontFamily by rememberFontFamily()
  val fontSize by rememberFontSize()

  val context = LocalContext.current

  LaunchedEffect(fontFamily, fontSize) {
    editor.apply {
      val font = ResourcesCompat.getFont(
        context, when (fontFamily) {
          context.getString(R.string.pref_editor_font_value_firacode) -> R.font.firacode_regular
          context.getString(R.string.pref_editor_font_value_jetbrains) -> R.font.jetbrains_mono
          else -> R.font.jetbrains_mono
        }
      )

      typefaceText = font
      typefaceLineNumber = font
      setTextSize(fontSize)
    }
  }
}

@Composable
private fun configureColorScheme(editor: VCSpaceEditor) {
  val colorScheme by rememberColorScheme()
  val isDarkTheme = isSystemInDarkTheme()

  val followSystemTheme by rememberFollowSystemTheme()
  val isDarkMode by rememberIsDarkMode()

  val context = LocalContext.current

  LaunchedEffect(colorScheme, isDarkTheme, followSystemTheme, isDarkMode) {
    editor.apply {
      ThemeRegistry.getInstance().setTheme(
        when (colorScheme) {
          context.getString(R.string.pref_editor_colorscheme_value_followui) -> if ((followSystemTheme && isDarkTheme) || isDarkMode) "darcula" else "quietlight"
          "Quietlight" -> "quietlight"
          "Darcula" -> "darcula"
          "Abyss" -> "abyss"
          "Solarized Dark" -> "solarized_drak"
          else -> if ((followSystemTheme && isDarkTheme) || isDarkMode) "darcula" else "quietlight"
        }
      ).also {
        setText(text.toString()) // Required to update colors correctly
      }
    }
  }
}

@Composable
private fun configureIndentation(editor: VCSpaceEditor) {
  val indentSize by rememberIndentSize()
  val useTab by rememberUseTab()

  LaunchedEffect(indentSize, useTab) {
    editor.apply {
      (editorLanguage as? TextMateLanguage)?.tabSize = indentSize
      (editorLanguage as? TextMateLanguage)?.useTab(useTab)
      tabWidth = indentSize
    }
  }
}

@Composable
private fun configureMiscSettings(editor: VCSpaceEditor) {
  val stickyScroll by rememberStickyScroll()
  val fontLigatures by rememberFontLigatures()
  val wordWrap by rememberWordWrap()
  val lineNumber by rememberLineNumber()
  val deleteLineOnBackspace by rememberDeleteLineOnBackspace()
  val deleteIndentOnBackspace by rememberDeleteIndentOnBackspace()

  LaunchedEffect(
    stickyScroll,
    fontLigatures,
    wordWrap,
    lineNumber,
    deleteLineOnBackspace,
    deleteIndentOnBackspace
  ) {
    editor.apply {
      props.stickyScroll = stickyScroll
      isLigatureEnabled = fontLigatures
      isWordwrap = wordWrap
      isLineNumberEnabled = lineNumber
      props.deleteEmptyLineFast = deleteLineOnBackspace
      props.deleteMultiSpaces = if (deleteIndentOnBackspace) -1 else 1
    }
  }
}
