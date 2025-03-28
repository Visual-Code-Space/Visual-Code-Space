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

import android.graphics.Typeface
import android.view.KeyEvent
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.ErrorOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.itsvks.monaco.MonacoEditor
import com.itsvks.monaco.MonacoLanguage
import com.itsvks.monaco.MonacoTheme
import com.itsvks.monaco.option.AcceptSuggestionOnEnter
import com.itsvks.monaco.option.MatchBrackets
import com.itsvks.monaco.option.TextEditorCursorBlinkingStyle
import com.itsvks.monaco.option.TextEditorCursorStyle
import com.itsvks.monaco.option.WordBreak
import com.itsvks.monaco.option.WordWrap
import com.itsvks.monaco.option.WrappingStrategy
import com.itsvks.monaco.option.minimap.MinimapOptions
import com.itsvks.monaco.util.MonacoLanguageMapper
import com.teixeira.vcspace.activities.Editor.LocalCommandPaletteManager
import com.teixeira.vcspace.activities.Editor.LocalEditorDrawerState
import com.teixeira.vcspace.activities.Editor.LocalEditorSnackbarHostState
import com.teixeira.vcspace.compose.ui.EditorTab
import com.teixeira.vcspace.compose.ui.dialog.ConfirmDialog
import com.teixeira.vcspace.core.EventManager
import com.teixeira.vcspace.core.ai.Gemini
import com.teixeira.vcspace.core.settings.Settings.Editor.rememberColorScheme
import com.teixeira.vcspace.core.settings.Settings.Editor.rememberCurrentEditor
import com.teixeira.vcspace.core.settings.Settings.Editor.rememberDeleteIndentOnBackspace
import com.teixeira.vcspace.core.settings.Settings.Editor.rememberDeleteLineOnBackspace
import com.teixeira.vcspace.core.settings.Settings.Editor.rememberEditorTextActionWindowExpandThreshold
import com.teixeira.vcspace.core.settings.Settings.Editor.rememberFontFamily
import com.teixeira.vcspace.core.settings.Settings.Editor.rememberFontLigatures
import com.teixeira.vcspace.core.settings.Settings.Editor.rememberFontSize
import com.teixeira.vcspace.core.settings.Settings.Editor.rememberIndentSize
import com.teixeira.vcspace.core.settings.Settings.Editor.rememberLineNumber
import com.teixeira.vcspace.core.settings.Settings.Editor.rememberStickyScroll
import com.teixeira.vcspace.core.settings.Settings.Editor.rememberUseTab
import com.teixeira.vcspace.core.settings.Settings.Editor.rememberWordWrap
import com.teixeira.vcspace.core.settings.Settings.File.rememberLastOpenedFile
import com.teixeira.vcspace.core.settings.Settings.General.rememberFollowSystemTheme
import com.teixeira.vcspace.core.settings.Settings.General.rememberIsDarkMode
import com.teixeira.vcspace.core.settings.Settings.Monaco
import com.teixeira.vcspace.core.settings.Settings.Monaco.rememberAcceptSuggestionOnCommitCharacter
import com.teixeira.vcspace.core.settings.Settings.Monaco.rememberAcceptSuggestionOnEnter
import com.teixeira.vcspace.core.settings.Settings.Monaco.rememberCursorBlinkingStyle
import com.teixeira.vcspace.core.settings.Settings.Monaco.rememberCursorStyle
import com.teixeira.vcspace.core.settings.Settings.Monaco.rememberFolding
import com.teixeira.vcspace.core.settings.Settings.Monaco.rememberGlyphMargin
import com.teixeira.vcspace.core.settings.Settings.Monaco.rememberLetterSpacing
import com.teixeira.vcspace.core.settings.Settings.Monaco.rememberLineDecorationsWidth
import com.teixeira.vcspace.core.settings.Settings.Monaco.rememberLineNumbersMinChars
import com.teixeira.vcspace.core.settings.Settings.Monaco.rememberMatchBrackets
import com.teixeira.vcspace.core.settings.Settings.Monaco.rememberMonacoTheme
import com.teixeira.vcspace.core.settings.Settings.Monaco.rememberWordBreak
import com.teixeira.vcspace.core.settings.Settings.Monaco.rememberWrappingStrategy
import com.teixeira.vcspace.editor.TextActionsWindow
import com.teixeira.vcspace.editor.VCSpaceEditor
import com.teixeira.vcspace.editor.addBlockComment
import com.teixeira.vcspace.editor.addSingleComment
import com.teixeira.vcspace.editor.listener.OnExplainCodeListener
import com.teixeira.vcspace.editor.listener.OnImportComponentListener
import com.teixeira.vcspace.editor.textaction.EditorTextActionItem
import com.teixeira.vcspace.editor.textaction.actionItems
import com.teixeira.vcspace.editor.textaction.editorTextActionWindow
import com.teixeira.vcspace.file.File
import com.teixeira.vcspace.file.extension
import com.teixeira.vcspace.keyboard.CommandPaletteManager
import com.teixeira.vcspace.keyboard.createKeyEvent
import com.teixeira.vcspace.keyboard.model.toShortcut
import com.teixeira.vcspace.plugins.DialogManager
import com.teixeira.vcspace.resources.R
import com.teixeira.vcspace.ui.LocalToastHostState
import com.teixeira.vcspace.ui.components.keyboard.CommandPalette
import com.teixeira.vcspace.ui.screens.editor.ai.CodeExplanationSheet
import com.teixeira.vcspace.ui.screens.editor.ai.ImportComponentsSheet
import com.teixeira.vcspace.ui.screens.editor.components.Symbols
import com.teixeira.vcspace.ui.screens.editor.components.view.CodeEditorView
import com.teixeira.vcspace.utils.launchWithProgressDialog
import com.vcspace.plugins.event.KeyPressEvent
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun EditorScreen(
    modifier: Modifier = Modifier,
    viewModel: EditorViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val editorConfigMap = remember { viewModel.editorConfigMap }

    val openedFiles = uiState.openedFiles
    val selectedFileIndex = uiState.selectedFileIndex

    LaunchedEffect(selectedFileIndex) {
        viewModel.rememberLastFiles()
    }

    val openLastFiles by rememberLastOpenedFile()
    val currentEditor by rememberCurrentEditor()

    DisposableEffect(openLastFiles) {
        /*if (openLastFiles) {*/
        for (file in viewModel.lastOpenedFiles()) {
            viewModel.addFile(file)
        }
        /*}*/

        onDispose {
            viewModel.rememberLastFiles()
        }
    }

    val context = LocalContext.current
    val toastHostState = LocalEditorSnackbarHostState.current
    val commandPaletteManager = LocalCommandPaletteManager.current

    var codeExplanationResponse: GenerateContentResponse? by remember { mutableStateOf(null) }
    var importComponentResponse: GenerateContentResponse? by remember { mutableStateOf(null) }

    codeExplanationResponse?.let {
        CodeExplanationSheet(
            response = it,
            onDismissRequest = { codeExplanationResponse = null }
        )
    }

    importComponentResponse?.let {
        ImportComponentsSheet(
            response = it,
            onDismissRequest = { importComponentResponse = null }
        )
    }

    val compositionContext = rememberCompositionContext()

    Column(modifier = modifier.onKeyEvent {
        if (it.isCtrlPressed && it.isShiftPressed && it.key == Key.P) {
            println("Ctrl + Shift + P is pressed")
            EventManager.instance.postEvent(
                KeyPressEvent(
                    key = it.key.toShortcut(),
                    keyCode = it.key.keyCode,
                    isCtrlPressed = it.isCtrlPressed,
                    isShiftPressed = it.isShiftPressed
                )
            )
            commandPaletteManager.show()
            return@onKeyEvent true
        }

        if (it.type == KeyEventType.KeyDown) {
            CommandPaletteManager.instance.applyKeyBindings(it, compositionContext)
            return@onKeyEvent true
        }

        false
    }) {
        if (openedFiles.isNotEmpty()) {
            var closeFileIndex: Int? by remember { mutableStateOf(null) }

            closeFileIndex?.let {
                val file = openedFiles[it].file

                ConfirmDialog(
                    title = "Close File",
                    message = "Are you sure you want to close ${file.name} ?",
                    onConfirm = {
                        viewModel.closeFile(it)
                        closeFileIndex = null
                    },
                    onDismiss = { closeFileIndex = null }
                )
            }

            EditorTab(
                files = openedFiles,
                selectedFileIndex = selectedFileIndex,
                onTabSelected = viewModel::selectFile,
                onTabClose = { index -> closeFileIndex = index },
                onCloseOthers = viewModel::closeOthers,
                onCloseAll = viewModel::closeAll
            )
        }

        val openedFile = openedFiles.getOrNull(selectedFileIndex)

        openedFile?.let { fileEntry ->
            val editorView = viewModel.getEditorForFile(
                context,
                fileEntry.file,
                isAdvancedEditor = currentEditor.lowercase() == "monaco"
            )

            key(editorConfigMap[fileEntry.file.path]) {
                if (editorView is CodeEditorView) {
                    SoraEditor(
                        editorView = editorView,
                        onExplainCodeResponse = { codeExplanationResponse = it },
                        onImportComponentResponse = { importComponentResponse = it }
                    )
                } else if (editorView is MonacoEditor) {
                    val file = fileEntry.file

                    ConfigureMonacoEditor(editorView, file) { editor ->
                        viewModel.setModified(file, false)
                    }
                }
                viewModel.setEditorConfiguredForFile(fileEntry.file)
            }

            key(fileEntry.file.path) {
                if (editorView is MonacoEditor) {
                    AnimatedVisibility(
                        visible = true,
                    ) {
                        AndroidView(
                            factory = {
                                editorView.apply {
                                    layoutParams = ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                    )
                                }
                            },
                            update = {
                                it.apply {
                                    layoutParams = ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(fraction = 0.92f)
                        )
                    }

                    AnimatedVisibility(
                        visible = false,
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.TopStart
                        ) {
                            Text("Loading...")
                        }
                    }
                } else {
                    AndroidView(
                        factory = {
                            editorView.apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(fraction = 0.92f)
                    )
                }

                Symbols(editorView, modifier = Modifier.fillMaxWidth())
            }
        } ?: run {
//            val tempFile = java.io.File(context.cacheDir, "untitled.txt")
//            tempFile.deleteOnExit()
//            viewModel.addFile(tempFile.wrapFile())

            NoOpenedFiles()
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

        if (DialogManager.instance.showDialog.value) {
            val dialogManager = DialogManager.instance

            AlertDialog(
                onDismissRequest = { dialogManager.hideDialog() },
                title = { Text(dialogManager.title.value) },
                text = { Text(dialogManager.message.value) },
                dismissButton = if (dialogManager.negativeButtonText.value.isNotEmpty()) {
                    {
                        TextButton(onClick = {
                            dialogManager.negativeButtonClickListener.value?.onClick()
                        }) {
                            Text(dialogManager.negativeButtonText.value)
                        }
                    }
                } else null,
                confirmButton = {
                    TextButton(onClick = {
                        dialogManager.positiveButtonClickListener.value?.onClick()
                    }) {
                        Text(dialogManager.positiveButtonText.value)
                    }
                }
            )
        }
    }
}

@Composable
private fun NoOpenedFiles() {
    val commandPaletteManager = LocalCommandPaletteManager.current
    val drawerState = LocalEditorDrawerState.current
    val currentCompositionContext = rememberCompositionContext()
    val scope = rememberCoroutineScope()

    fun dispatchKeyEvent(keyCode: Int, metaState: Int) {
        commandPaletteManager.applyKeyBindings(
            event = androidx.compose.ui.input.key.KeyEvent(
                createKeyEvent(
                    keyCode = keyCode,
                    metaState = metaState
                )
            ),
            compositionContext = currentCompositionContext
        )
    }

    WelcomeScreen(
        onOpenFile = {
            dispatchKeyEvent(KeyEvent.KEYCODE_O, KeyEvent.META_CTRL_ON)
        },
        onNewFile = {
            dispatchKeyEvent(KeyEvent.KEYCODE_N, KeyEvent.META_CTRL_ON)
        },
        onOpenFolder = {
            dispatchKeyEvent(KeyEvent.KEYCODE_O, KeyEvent.META_CTRL_ON or KeyEvent.META_SHIFT_ON)
            scope.launch {
                delay(500)
                drawerState.open()
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun ConfigureMonacoEditor(
    editorView: MonacoEditor,
    file: File,
    onConfigure: (MonacoEditor) -> Unit = {}
) {
    val theme by rememberMonacoTheme()
    val fontSize by Monaco.rememberFontSize()
    val lineNumbersMinChars by rememberLineNumbersMinChars()
    val lineDecorationsWidth by rememberLineDecorationsWidth()
    val letterSpacing by rememberLetterSpacing()
    val matchBrackets by rememberMatchBrackets()
    val acceptSuggestionOnCommitCharacter by rememberAcceptSuggestionOnCommitCharacter()
    val acceptSuggestionOnEnter by rememberAcceptSuggestionOnEnter()
    val folding by rememberFolding()
    val glyphMargin by rememberGlyphMargin()
    val wordWrap by Monaco.rememberWordWrap()
    val wordBreak by rememberWordBreak()
    val wrappingStrategy by rememberWrappingStrategy()
    val cursorStyle by rememberCursorStyle()
    val cursorBlinkingStyle by rememberCursorBlinkingStyle()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        editorView.addOnEditorLoadCallback {
            editorView.text = "Loading..."
            editorView.setReadOnly(true)
            editorView.setLanguage(MonacoLanguage.Plaintext)

            editorView.apply {
                setTheme(MonacoTheme.fromString(theme))
                setFontSize(fontSize)
                setLineNumbersMinChars(lineNumbersMinChars)
                setLineDecorationsWidth(lineDecorationsWidth)
                setLetterSpacing(letterSpacing)
                setMatchBrackets(MatchBrackets.fromValue(matchBrackets))
                setAcceptSuggestionOnCommitCharacter(acceptSuggestionOnCommitCharacter)
                setAcceptSuggestionOnEnter(AcceptSuggestionOnEnter.fromValue(acceptSuggestionOnEnter))
                setFolding(folding)
                setGlyphMargin(glyphMargin)
                setWordWrap(WordWrap.fromValue(wordWrap))
                setWordBreak(WordBreak.fromValue(wordBreak))
                setWrappingStrategy(WrappingStrategy.fromValue(wrappingStrategy))
                setCursorStyle(TextEditorCursorStyle.fromValue(cursorStyle))
                setCursorBlinkingStyle(TextEditorCursorBlinkingStyle.fromValue(cursorBlinkingStyle))
                setMinimapOptions(MinimapOptions(enabled = false))

                if (file.exists()) {
                    setLanguage(MonacoLanguageMapper.getLanguageByExtension(file.extension))
                    setReadOnly(false)
                    text = file.asRawFile()?.readText() ?: ""
                } else {
                    text = ""
                }

                onConfigure(this)
            }
        }
    }

    LaunchedEffect(
        theme,
        fontSize,
        lineNumbersMinChars,
        lineDecorationsWidth,
        letterSpacing,
        matchBrackets,
        acceptSuggestionOnCommitCharacter,
        acceptSuggestionOnEnter,
        folding,
        glyphMargin,
        wordWrap,
        wordBreak,
        wrappingStrategy,
        cursorStyle,
        cursorBlinkingStyle
    ) {
        editorView.reload()

        editorView.apply {
            if (file.exists()) {
                setLanguage(MonacoLanguageMapper.getLanguageByExtension(file.extension))
                setReadOnly(false)
                scope.launch(Dispatchers.IO) {
                    val contents = file.readFile2String(context) ?: ""
                    withContext(Dispatchers.Main) {
                        text = contents
                    }
                }
            } else {
                text = ""
            }

            onConfigure(this)
        }
    }
}

@Composable
fun SoraEditor(
    editorView: CodeEditorView,
    onExplainCodeResponse: (GenerateContentResponse) -> Unit = {},
    onImportComponentResponse: (GenerateContentResponse) -> Unit = {}
) {
    val context = LocalContext.current
    val toastHostState = LocalToastHostState.current
    val scope = rememberCoroutineScope()

    ConfigureEditor(
        editorView.editor, onExplainCodeListener = { code ->
            scope.launchWithProgressDialog(
                context = Dispatchers.IO,
                uiContext = context,
                configureBuilder = { builder ->
                    builder.apply {
                        setMessage("Analyzing Code")
                        setCancelable(false)
                    }
                }
            ) { _, _ ->
                Gemini.explainCode(code.toString())
                    .onSuccess(onExplainCodeResponse)
                    .onFailure {
                        scope.launch {
                            toastHostState.showToast(
                                message = it.message ?: "Error",
                                icon = Icons.Sharp.ErrorOutline
                            )
                        }
                    }
            }
        },
        onImportComponentListener = { code ->
            scope.launchWithProgressDialog(
                context = Dispatchers.IO,
                uiContext = context,
                configureBuilder = { builder ->
                    builder.apply {
                        setMessage("Analyzing Code")
                        setCancelable(false)
                    }
                }
            ) { _, _ ->
                Gemini.importComponents(code.toString())
                    .onSuccess(onImportComponentResponse)
                    .onFailure {
                        scope.launch {
                            toastHostState.showToast(
                                message = it.message ?: "Error",
                                icon = Icons.Sharp.ErrorOutline
                            )
                        }
                    }
            }
        }
    )
}

@Composable
private fun ConfigureEditor(
    editor: VCSpaceEditor,
    onExplainCodeListener: OnExplainCodeListener? = null,
    onImportComponentListener: OnImportComponentListener? = null
) {
    val items = remember {
        mutableStateListOf<EditorTextActionItem>().apply {
            addAll(actionItems)
        }
    }
    val editorTextActionWindowExpandThreshold by rememberEditorTextActionWindowExpandThreshold()

    val editorTextActionWindow = editorTextActionWindow(
        items = items,
        editorTextActionWindowExpandThreshold = editorTextActionWindowExpandThreshold
    ) {
        if (it.id != R.string.editor_action_select_all) {
            editor.textActions?.dismiss()
        }

        when (it.id) {
            R.string.editor_action_comment_line -> {
                val commentRule = editor.commentRule
                if (!editor.cursor.isSelected) {
                    addSingleComment(commentRule, editor.text)
                } else {
                    addBlockComment(commentRule, editor.text)
                }
                editor.setSelection(editor.cursor.rightLine, editor.cursor.rightColumn)
            }

            R.string.editor_action_select_all -> {
                editor.selectAll()
            }

            R.string.editor_action_long_select -> editor.beginLongSelect()

            R.string.editor_action_copy -> {
                editor.copyText()
                editor.setSelection(editor.cursor.rightLine, editor.cursor.rightColumn)
            }

            R.string.editor_action_paste -> {
                editor.pasteText()
                editor.setSelection(editor.cursor.rightLine, editor.cursor.rightColumn)
            }

            R.string.editor_action_cut -> {
                if (editor.cursor.isSelected) {
                    editor.cutText()
                }
            }

            R.string.editor_action_format -> {
                editor.setSelection(editor.cursor.rightLine, editor.cursor.rightColumn)
                editor.formatCodeAsync()
            }

            R.string.editor_action_explain_code -> {
                val content = editor.text
                val cursor = content.cursor
                editor.onExplainCodeListener?.onExplain(
                    content.substring(
                        cursor.left,
                        cursor.right
                    )
                )
            }

            R.string.editor_action_import_components -> {
                val content = editor.text
                val cursor = content.cursor
                editor.onImportComponentListener?.onImport(
                    content.substring(
                        cursor.left,
                        cursor.right
                    )
                )
            }
        }
    }

    editor.onExplainCodeListener = onExplainCodeListener
    editor.onImportComponentListener = onImportComponentListener
    editor.setTextActionWindow {
        TextActionsWindow(it, editorTextActionWindow) {
            fun updateAction(index: Int, visible: Boolean, clickable: Boolean = true) {
                items[index] = items[index].copy(visible = visible, clickable = clickable)
            }

            // Comment action
            val commentRule = editor.commentRule
            updateAction(0, commentRule != null && editor.isEditable)

            // Select all action
            updateAction(1, true)

            // Long select action
            updateAction(2, editor.isEditable)

            // Cut action
            updateAction(3, editor.isEditable && editor.cursor.isSelected)

            // Copy action
            updateAction(4, editor.cursor.isSelected, editor.cursor.isSelected)

            // Paste action
            updateAction(5, true, editor.hasClip())

            // Format action
            updateAction(6, editor.isEditable)

            // Explain Code Action
            updateAction(7, editor.cursor.isSelected, editor.cursor.isSelected)

            // Import Action
            updateAction(8, editor.cursor.isSelected, editor.cursor.isSelected)
        }
    }

    ConfigureFontSettings(editor)
    ConfigureColorScheme(editor)
    ConfigureIndentation(editor)
    ConfigureMiscSettings(editor)
}

@Composable
private fun ConfigureFontSettings(editor: VCSpaceEditor) {
    val fontFamily by rememberFontFamily()
    val fontSize by rememberFontSize()

    val context = LocalContext.current

    LaunchedEffect(fontFamily, fontSize) {
        editor.apply {
            val font = with(context) {
                when (fontFamily) {
                    getString(R.string.pref_editor_font_value_firacode) -> {
                        ResourcesCompat.getFont(this, R.font.firacode_regular)
                    }

                    getString(R.string.pref_editor_font_value_jetbrains) -> {
                        Typeface.createFromAsset(assets, "fonts/JetBrainsMono-Regular.ttf")
                    }

                    else -> {
                        Typeface.createFromAsset(assets, "fonts/JetBrainsMono-Regular.ttf")
                    }
                }
            }

            typefaceText = font
            typefaceLineNumber = font
            setTextSize(fontSize)
        }
    }
}

@Composable
private fun ConfigureColorScheme(editor: VCSpaceEditor) {
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
private fun ConfigureIndentation(editor: VCSpaceEditor) {
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
private fun ConfigureMiscSettings(editor: VCSpaceEditor) {
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
