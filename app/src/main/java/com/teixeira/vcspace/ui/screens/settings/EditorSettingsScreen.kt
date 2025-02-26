/*
 * This file is part of Visual Code Space.
 *
 * Visual Code Space is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published = the Free Software Foundation, either version 3 of
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

import android.app.DownloadManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.FormatIndentDecrease
import androidx.compose.material.icons.automirrored.filled.FormatIndentIncrease
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.WrapText
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DataArray
import androidx.compose.material.icons.filled.Expand
import androidx.compose.material.icons.filled.FontDownload
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Tab
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.VerticalAlignTop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastRoundToInt
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.blankj.utilcode.util.ToastUtils
import com.itsvks.monaco.MonacoEditor
import com.teixeira.vcspace.app.MONACO_EDITOR_ARCHIVE
import com.teixeira.vcspace.core.settings.Settings.Editor.COLOR_SCHEME
import com.teixeira.vcspace.core.settings.Settings.Editor.CURRENT_EDITOR
import com.teixeira.vcspace.core.settings.Settings.Editor.DELETE_INDENT_ON_BACKSPACE
import com.teixeira.vcspace.core.settings.Settings.Editor.DELETE_LINE_ON_BACKSPACE
import com.teixeira.vcspace.core.settings.Settings.Editor.EDITOR_TEXT_ACTION_WINDOW_EXPAND_THRESHOLD
import com.teixeira.vcspace.core.settings.Settings.Editor.FONT_FAMILY
import com.teixeira.vcspace.core.settings.Settings.Editor.FONT_LIGATURES
import com.teixeira.vcspace.core.settings.Settings.Editor.FONT_SIZE
import com.teixeira.vcspace.core.settings.Settings.Editor.INDENT_SIZE
import com.teixeira.vcspace.core.settings.Settings.Editor.LINE_NUMBER
import com.teixeira.vcspace.core.settings.Settings.Editor.SHOW_INPUT_METHOD_PICKER_AT_START
import com.teixeira.vcspace.core.settings.Settings.Editor.STICKY_SCROLL
import com.teixeira.vcspace.core.settings.Settings.Editor.SYMBOLS
import com.teixeira.vcspace.core.settings.Settings.Editor.USE_TAB
import com.teixeira.vcspace.core.settings.Settings.Editor.WORD_WRAP
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
import com.teixeira.vcspace.core.settings.Settings.Editor.rememberShowInputMethodPickerAtStart
import com.teixeira.vcspace.core.settings.Settings.Editor.rememberStickyScroll
import com.teixeira.vcspace.core.settings.Settings.Editor.rememberSymbols
import com.teixeira.vcspace.core.settings.Settings.Editor.rememberUseTab
import com.teixeira.vcspace.core.settings.Settings.Editor.rememberWordWrap
import com.teixeira.vcspace.core.settings.Settings.EditorTabs.AUTO_SAVE
import com.teixeira.vcspace.core.settings.Settings.EditorTabs.rememberAutoSave
import com.teixeira.vcspace.resources.R
import com.teixeira.vcspace.utils.runOnUiThread
import kotlinx.coroutines.launch
import me.zhanghai.compose.preference.listPreference
import me.zhanghai.compose.preference.preference
import me.zhanghai.compose.preference.preferenceCategory
import me.zhanghai.compose.preference.sliderPreference
import me.zhanghai.compose.preference.switchPreference
import me.zhanghai.compose.preference.textFieldPreference
import java.io.File
import kotlin.concurrent.thread

@Composable
fun EditorSettingsScreen(
    onNavigateUp: () -> Unit,
    onNavigateToMonacoEditorSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val coroutineScope = rememberCoroutineScope()

    val currentEditor = rememberCurrentEditor()
    val showInputMethodPickerAtStart = rememberShowInputMethodPickerAtStart()

    val fontSize = rememberFontSize()
    val indentSize = rememberIndentSize()
    val fontFamily = rememberFontFamily()
    val colorScheme = rememberColorScheme()
    val fontLigatures = rememberFontLigatures()
    val symbols = rememberSymbols()
    val stickyScroll = rememberStickyScroll()
    val wordWrap = rememberWordWrap()
    val lineNumber = rememberLineNumber()
    val useTab = rememberUseTab()
    val deleteLineOnBackspace = rememberDeleteLineOnBackspace()
    val deleteIndentOnBackspace = rememberDeleteIndentOnBackspace()
    val editorTextActionWindowExpandThreshold = rememberEditorTextActionWindowExpandThreshold()

    LaunchedEffect(currentEditor.value) {
        if (currentEditor.value.lowercase() == "monaco") {
            val internal = File(context.filesDir, "monaco-editor-main")

            if (internal.exists().not()) {
                runOnUiThread { ToastUtils.showShort("Downloading Monaco editor...") }
                val id = MonacoEditor.downloadMonaco(context, MONACO_EDITOR_ARCHIVE)
                val downloadManager = context.getSystemService(DownloadManager::class.java)
                val query = DownloadManager.Query().setFilterById(id)

                thread {
                    var downloading = true

                    while (downloading) {
                        val cursor = downloadManager.query(query)

                        if (cursor != null && cursor.moveToFirst()) {
                            val status =
                                cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                            downloading =
                                status == DownloadManager.STATUS_RUNNING || status == DownloadManager.STATUS_PAUSED || status == DownloadManager.STATUS_PENDING

                            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                downloading = false
                                val filePath =
                                    cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI))
                                runOnUiThread { ToastUtils.showShort("Monaco editor downloaded successfully") }

                                coroutineScope.launch {
                                    MonacoEditor.doSetup(context, filePath.toUri().toFile())
                                }
                            } else if (status == DownloadManager.STATUS_FAILED) {
                                downloading = false
                                val reason =
                                    cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON))
                                runOnUiThread { ToastUtils.showShort("Monaco editor download failed: $reason") }
                            }
                        }

                        cursor?.close()

                        runCatching { Thread.sleep(1000) }
                    }
                }
            }
        }
    }

    val autoSave = rememberAutoSave()

    BackHandler(onBack = onNavigateUp)

    val backgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        preferenceCategory(
            key = "editor_category",
            title = { Text(text = stringResource(R.string.editor)) }
        )

        listPreference(
            key = CURRENT_EDITOR.name,
            title = { Text(text = "${stringResource(R.string.current_editor)} ($it Editor)") },
            summary = { Text(text = getEditorDescription(it)) },
            rememberState = { currentEditor },
            defaultValue = currentEditor.value,
            values = listOf("Sora Editor", "Monaco Editor (deprecated)"),
            valueToText = { AnnotatedString(it) },
            icon = { Icon(Icons.Default.Code, contentDescription = null) },
            modifier = Modifier
                .clip(PreferenceShape.Top)
                .background(backgroundColor)
        )

        if (currentEditor.value.lowercase() == "monaco") {
            preference(
                key = "monaco_settings",
                title = { Text(text = "Monaco Editor Settings") },
                summary = { Text(text = "Open Monaco editor settings.") },
                onClick = onNavigateToMonacoEditorSettings,
                icon = { Icon(Icons.Default.Code, contentDescription = null) },
                modifier = Modifier
                    .clip(PreferenceShape.Middle)
                    .background(backgroundColor)
            )
        }

        preference(
            key = "keyboard_suggestion",
            title = {
                Text(
                    text = when (currentEditor.value) {
                        "Monaco" -> "Enhance Your Monaco Experience"
                        else -> "Typing Tip"
                    }
                )
            },
            summary = {
                Text(
                    text = when (currentEditor.value) {
                        "Monaco" -> "Consider using a keyboard with extra keys like Hacker's Keyboard for a more efficient coding workflow. (Click to learn more)"
                        else -> "For optimal typing efficiency, explore keyboard customization options."
                    }
                )
            },
            onClick = {
                if (currentEditor.value == "Monaco") {
                    uriHandler.openUri("https://play.google.com/store/apps/details?id=org.pocketworkstation.pckeyboard")
                }
            },
            icon = { Icon(Icons.Default.Keyboard, contentDescription = null) },
            modifier = Modifier
                .clip(PreferenceShape.Middle)
                .background(backgroundColor)
        )

        switchPreference(
            key = SHOW_INPUT_METHOD_PICKER_AT_START.name,
            title = { Text(text = "Show Input Method Picker at Start") },
            summary = { Text(text = "Show the input method picker at start.") },
            rememberState = { showInputMethodPickerAtStart },
            defaultValue = showInputMethodPickerAtStart.value,
            icon = { Icon(Icons.Default.Language, contentDescription = null) },
            modifier = Modifier
                .clip(PreferenceShape.Bottom)
                .background(backgroundColor)
        )

        preferenceCategory(
            key = "editor_settings_category",
            title = { Text(text = stringResource(R.string.editor_settings)) }
        )

        sliderPreference(
            key = FONT_SIZE.name,
            title = { Text(text = stringResource(R.string.font_size)) },
            defaultValue = fontSize.value,
            rememberState = { fontSize },
            valueRange = 11f..28f,
            valueSteps = 16,
            valueText = { Text(stringResource(R.string.font_size_value, it.fastRoundToInt())) },
            icon = { Icon(Icons.Default.TextFields, contentDescription = null) },
            modifier = Modifier
                .clip(PreferenceShape.Top)
                .background(backgroundColor)
        )

        listPreference(
            key = INDENT_SIZE.name,
            title = { Text(stringResource(R.string.indent_size)) },
            summary = { Text(stringResource(R.string.indent_size_summary, indentSize.value)) },
            rememberState = { indentSize },
            defaultValue = indentSize.value,
            values = (2..8 step 2).map { it },
            icon = {
                Icon(
                    Icons.AutoMirrored.Filled.FormatIndentIncrease,
                    contentDescription = null
                )
            },
            modifier = Modifier
                .clip(PreferenceShape.Middle)
                .background(backgroundColor)
        )

        listPreference(
            key = FONT_FAMILY.name,
            title = { Text(stringResource(R.string.font_family)) },
            summary = { Text(stringResource(R.string.font_family_summary)) },
            rememberState = { fontFamily },
            defaultValue = fontFamily.value,
            values = listOf(
                context.getString(R.string.pref_editor_font_value_firacode),
                context.getString(R.string.pref_editor_font_value_jetbrains),
            ),
            icon = { Icon(Icons.Filled.TextFields, contentDescription = null) },
            modifier = Modifier
                .clip(PreferenceShape.Middle)
                .background(backgroundColor)
        )

        listPreference(
            key = COLOR_SCHEME.name,
            title = { Text(stringResource(R.string.color_scheme)) },
            summary = { Text(stringResource(R.string.color_scheme_summary)) },
            rememberState = { colorScheme },
            defaultValue = colorScheme.value,
            values = listOf(
                context.getString(R.string.pref_editor_colorscheme_value_followui),
                "Quietlight",
                "Darcula",
                "Abyss",
                "Solarized Dark",
            ),
            icon = { Icon(Icons.Filled.Palette, contentDescription = null) },
            modifier = Modifier
                .clip(PreferenceShape.Middle)
                .background(backgroundColor)
        )

        switchPreference(
            key = FONT_LIGATURES.name,
            title = { Text(text = stringResource(R.string.font_ligatures)) },
            summary = {
                Text(
                    text = if (it) stringResource(R.string.font_ligatures_enabled) else stringResource(
                        R.string.font_ligatures_disabled
                    )
                )
            },
            rememberState = { fontLigatures },
            defaultValue = fontLigatures.value,
            icon = { Icon(imageVector = Icons.Default.FontDownload, contentDescription = null) },
            modifier = Modifier
                .clip(PreferenceShape.Middle)
                .background(backgroundColor)
        )

        textFieldPreference(
            key = SYMBOLS.name,
            title = { Text(text = "Symbols") },
            summary = { Text(text = it) },
            rememberState = { symbols },
            defaultValue = symbols.value,
            textToValue = { it },
            icon = {
                Icon(
                    imageVector = Icons.Default.DataArray,
                    contentDescription = null
                )
            },
            modifier = Modifier
                .clip(PreferenceShape.Middle)
                .background(backgroundColor)
        )

        switchPreference(
            key = STICKY_SCROLL.name,
            title = { Text(text = stringResource(R.string.sticky_scroll)) },
            summary = {
                Text(
                    text = if (it) stringResource(R.string.sticky_scroll_enabled) else stringResource(
                        R.string.sticky_scroll_disabled
                    )
                )
            },
            rememberState = { stickyScroll },
            defaultValue = stickyScroll.value,
            icon = {
                Icon(
                    imageVector = Icons.Default.VerticalAlignTop,
                    contentDescription = null
                )
            },
            modifier = Modifier
                .clip(PreferenceShape.Middle)
                .background(backgroundColor)
        )

        switchPreference(
            key = WORD_WRAP.name,
            title = { Text(text = stringResource(R.string.word_wrap)) },
            summary = {
                Text(
                    text = if (it) stringResource(R.string.word_wrap_enabled) else stringResource(
                        R.string.word_wrap_disabled
                    )
                )
            },
            rememberState = { wordWrap },
            defaultValue = wordWrap.value,
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.WrapText,
                    contentDescription = null
                )
            },
            modifier = Modifier
                .clip(PreferenceShape.Middle)
                .background(backgroundColor)
        )

        switchPreference(
            key = LINE_NUMBER.name,
            title = { Text(text = stringResource(R.string.line_numbers)) },
            summary = {
                Text(
                    text = if (it) stringResource(R.string.line_numbers_displayed) else stringResource(
                        R.string.line_numbers_hidden
                    )
                )
            },
            rememberState = { lineNumber },
            defaultValue = lineNumber.value,
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.List,
                    contentDescription = null
                )
            },
            modifier = Modifier
                .clip(PreferenceShape.Middle)
                .background(backgroundColor)
        )

        switchPreference(
            key = USE_TAB.name,
            title = { Text(text = stringResource(R.string.use_tabs)) },
            summary = {
                Text(
                    text = if (it) stringResource(R.string.use_tabs_enabled) else stringResource(
                        R.string.use_tabs_disabled
                    )
                )
            },
            rememberState = { useTab },
            defaultValue = useTab.value,
            icon = { Icon(imageVector = Icons.Default.Tab, contentDescription = null) },
            modifier = Modifier
                .clip(PreferenceShape.Middle)
                .background(backgroundColor)
        )

        switchPreference(
            key = DELETE_LINE_ON_BACKSPACE.name,
            title = { Text(text = stringResource(R.string.delete_line_on_backspace)) },
            summary = {
                Text(
                    text = if (it) stringResource(R.string.delete_line_on_backspace_enabled) else stringResource(
                        R.string.delete_line_on_backspace_disabled
                    )
                )
            },
            rememberState = { deleteLineOnBackspace },
            defaultValue = deleteLineOnBackspace.value,
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = null
                )
            },
            modifier = Modifier
                .clip(PreferenceShape.Middle)
                .background(backgroundColor)
        )

        switchPreference(
            key = DELETE_INDENT_ON_BACKSPACE.name,
            title = { Text(text = stringResource(R.string.delete_indent_on_backspace)) },
            summary = {
                Text(
                    text = if (it) stringResource(R.string.delete_indent_on_backspace_enabled) else stringResource(
                        R.string.delete_indent_on_backspace_disabled
                    )
                )
            },
            rememberState = { deleteIndentOnBackspace },
            defaultValue = deleteIndentOnBackspace.value,
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.FormatIndentDecrease,
                    contentDescription = null
                )
            },
            modifier = Modifier
                .clip(PreferenceShape.Middle)
                .background(backgroundColor)
        )

        textFieldPreference(
            key = EDITOR_TEXT_ACTION_WINDOW_EXPAND_THRESHOLD.name,
            title = { Text(text = stringResource(R.string.editor_text_action_window_expand_threshold)) },
            summary = { Text(text = stringResource(R.string.editor_text_action_window_expand_threshold_summary)) },
            rememberState = { editorTextActionWindowExpandThreshold },
            defaultValue = editorTextActionWindowExpandThreshold.value,
            textToValue = { it.toIntOrNull() },
            icon = {
                Icon(
                    imageVector = Icons.Default.Expand,
                    contentDescription = null
                )
            },
            modifier = Modifier
                .clip(PreferenceShape.Bottom)
                .background(backgroundColor)
        )

        preferenceCategory(
            key = "tabs_category",
            title = { Text(text = stringResource(R.string.tabs_category)) }
        )

        switchPreference(
            key = AUTO_SAVE.name,
            title = { Text(text = stringResource(R.string.auto_save)) },
            summary = {
                Text(
                    text = if (it) stringResource(R.string.auto_save_enabled) else stringResource(R.string.auto_save_disabled)
                )
            },
            rememberState = { autoSave },
            defaultValue = autoSave.value,
            icon = {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null
                )
            },
            modifier = Modifier
                .clip(PreferenceShape.Alone)
                .background(backgroundColor)
        )
    }
}

private fun getEditorDescription(editorName: String): String {
    return when (editorName.lowercase()) {
        "sora" -> "Prioritizes stability for a smooth editing experience."
        "monaco" -> "Offers more advanced features, but may be less stable. Some settings may not be fully supported."
        else -> ""
    }
}
