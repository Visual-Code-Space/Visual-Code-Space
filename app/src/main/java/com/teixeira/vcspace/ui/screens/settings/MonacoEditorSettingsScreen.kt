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

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.AlignHorizontalLeft
import androidx.compose.material.icons.automirrored.sharp.Notes
import androidx.compose.material.icons.automirrored.sharp.WrapText
import androidx.compose.material.icons.sharp.Code
import androidx.compose.material.icons.sharp.DataObject
import androidx.compose.material.icons.sharp.FormatSize
import androidx.compose.material.icons.sharp.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.itsvks.monaco.MonacoTheme
import com.itsvks.monaco.option.AcceptSuggestionOnEnter
import com.itsvks.monaco.option.MatchBrackets
import com.itsvks.monaco.option.TextEditorCursorBlinkingStyle
import com.itsvks.monaco.option.TextEditorCursorStyle
import com.itsvks.monaco.option.WordBreak
import com.itsvks.monaco.option.WordWrap
import com.itsvks.monaco.option.WrappingStrategy
import com.teixeira.vcspace.core.settings.Settings.Monaco.ACCEPT_SUGGESTION_ON_COMMIT_CHARACTER
import com.teixeira.vcspace.core.settings.Settings.Monaco.ACCEPT_SUGGESTION_ON_ENTER
import com.teixeira.vcspace.core.settings.Settings.Monaco.CURSOR_BLINKING_STYLE
import com.teixeira.vcspace.core.settings.Settings.Monaco.CURSOR_STYLE
import com.teixeira.vcspace.core.settings.Settings.Monaco.FOLDING
import com.teixeira.vcspace.core.settings.Settings.Monaco.FONT_SIZE
import com.teixeira.vcspace.core.settings.Settings.Monaco.GLYPH_MARGIN
import com.teixeira.vcspace.core.settings.Settings.Monaco.LETTER_SPACING
import com.teixeira.vcspace.core.settings.Settings.Monaco.LINE_DECORATIONS_WIDTH
import com.teixeira.vcspace.core.settings.Settings.Monaco.LINE_NUMBERS_MIN_CHARS
import com.teixeira.vcspace.core.settings.Settings.Monaco.MATCH_BRACKETS
import com.teixeira.vcspace.core.settings.Settings.Monaco.MONACO_THEME
import com.teixeira.vcspace.core.settings.Settings.Monaco.WORD_BREAK
import com.teixeira.vcspace.core.settings.Settings.Monaco.WORD_WRAP
import com.teixeira.vcspace.core.settings.Settings.Monaco.WRAPPING_STRATEGY
import com.teixeira.vcspace.core.settings.Settings.Monaco.rememberAcceptSuggestionOnCommitCharacter
import com.teixeira.vcspace.core.settings.Settings.Monaco.rememberAcceptSuggestionOnEnter
import com.teixeira.vcspace.core.settings.Settings.Monaco.rememberCursorBlinkingStyle
import com.teixeira.vcspace.core.settings.Settings.Monaco.rememberCursorStyle
import com.teixeira.vcspace.core.settings.Settings.Monaco.rememberFolding
import com.teixeira.vcspace.core.settings.Settings.Monaco.rememberFontSize
import com.teixeira.vcspace.core.settings.Settings.Monaco.rememberGlyphMargin
import com.teixeira.vcspace.core.settings.Settings.Monaco.rememberLetterSpacing
import com.teixeira.vcspace.core.settings.Settings.Monaco.rememberLineDecorationsWidth
import com.teixeira.vcspace.core.settings.Settings.Monaco.rememberLineNumbersMinChars
import com.teixeira.vcspace.core.settings.Settings.Monaco.rememberMatchBrackets
import com.teixeira.vcspace.core.settings.Settings.Monaco.rememberMonacoTheme
import com.teixeira.vcspace.core.settings.Settings.Monaco.rememberWordBreak
import com.teixeira.vcspace.core.settings.Settings.Monaco.rememberWordWrap
import com.teixeira.vcspace.core.settings.Settings.Monaco.rememberWrappingStrategy
import com.teixeira.vcspace.extensions.capitalize
import com.teixeira.vcspace.ui.icons.CursorText
import com.teixeira.vcspace.ui.icons.FormatLetterSpacing
import com.teixeira.vcspace.ui.icons.FormatTextWrap
import com.teixeira.vcspace.ui.icons.Glyphs
import com.teixeira.vcspace.ui.icons.PromptSuggestion
import me.zhanghai.compose.preference.listPreference
import me.zhanghai.compose.preference.preferenceCategory
import me.zhanghai.compose.preference.switchPreference
import me.zhanghai.compose.preference.textFieldPreference

@Composable
fun MonacoEditorSettingsScreen(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = rememberMonacoTheme()
    val fontSize = rememberFontSize()
    val lineNumbersMinChars = rememberLineNumbersMinChars()
    val lineDecorationsWidth = rememberLineDecorationsWidth()
    val letterSpacing = rememberLetterSpacing()
    val matchBrackets = rememberMatchBrackets()
    val acceptSuggestionOnCommitCharacter = rememberAcceptSuggestionOnCommitCharacter()
    val acceptSuggestionOnEnter = rememberAcceptSuggestionOnEnter()
    val folding = rememberFolding()
    val glyphMargin = rememberGlyphMargin()
    val wordWrap = rememberWordWrap()
    val wordBreak = rememberWordBreak()
    val wrappingStrategy = rememberWrappingStrategy()
    val cursorStyle = rememberCursorStyle()
    val cursorBlinkingStyle = rememberCursorBlinkingStyle()

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
            key = "theme_category",
            title = { Text(text = "Theme") }
        )

        listPreference(
            key = MONACO_THEME.name,
            defaultValue = theme.value,
            title = { Text(text = "Theme") },
            summary = { Text(text = "Change the theme of the editor.") },
            rememberState = { theme },
            values = listOf(
                MonacoTheme.VisualStudioLight.value,
                MonacoTheme.VisualStudioDark.value,
                MonacoTheme.HighContrastLight.value,
                MonacoTheme.HighContrastDark.value
            ),
            valueToText = { AnnotatedString(MonacoTheme.fromString(it).toString()) },
            icon = { Icon(Icons.Sharp.Palette, contentDescription = null) },
            modifier = Modifier
                .clip(PreferenceShape.Alone)
                .background(backgroundColor)
        )

        preferenceCategory(
            key = "general_category",
            title = { Text(text = "General") }
        )

        textFieldPreference(
            key = FONT_SIZE.name,
            title = { Text(text = "Font size") },
            summary = { Text(text = "Change the font size of the editor.") },
            rememberState = { fontSize },
            defaultValue = 14,
            textToValue = { it.toIntOrNull() },
            icon = { Icon(Icons.Sharp.FormatSize, contentDescription = null) },
            modifier = Modifier
                .clip(PreferenceShape.Top)
                .background(backgroundColor)
        )

        textFieldPreference(
            key = LINE_NUMBERS_MIN_CHARS.name,
            title = { Text(text = "Line numbers min chars") },
            summary = { Text(text = "$it") },
            rememberState = { lineNumbersMinChars },
            defaultValue = 1,
            textToValue = { it.toIntOrNull() },
            icon = {
                Icon(
                    Icons.AutoMirrored.Sharp.AlignHorizontalLeft,
                    contentDescription = null
                )
            },
            modifier = Modifier
                .clip(PreferenceShape.Middle)
                .background(backgroundColor)
        )

        textFieldPreference(
            key = LINE_DECORATIONS_WIDTH.name,
            title = { Text(text = "Line decorations width") },
            summary = { Text(text = "$it") },
            rememberState = { lineDecorationsWidth },
            defaultValue = 1,
            textToValue = { it.toIntOrNull() },
            icon = {
                Icon(
                    Icons.AutoMirrored.Sharp.AlignHorizontalLeft,
                    contentDescription = null
                )
            },
            modifier = Modifier
                .clip(PreferenceShape.Middle)
                .background(backgroundColor)
        )

        textFieldPreference(
            key = LETTER_SPACING.name,
            title = { Text(text = "Letter spacing") },
            summary = { Text(text = "$it") },
            rememberState = { letterSpacing },
            defaultValue = 0f,
            textToValue = { it.toFloatOrNull() },
            icon = { Icon(FormatLetterSpacing, contentDescription = null) },
            modifier = Modifier
                .clip(PreferenceShape.Middle)
                .background(backgroundColor)
        )

        listPreference(
            key = MATCH_BRACKETS.name,
            defaultValue = matchBrackets.value,
            title = { Text(text = "Match brackets") },
            summary = { Text(text = it.capitalize()) },
            rememberState = { matchBrackets },
            values = listOf(
                MatchBrackets.Always.value,
                MatchBrackets.Never.value,
                MatchBrackets.Near.value
            ),
            valueToText = { AnnotatedString(it.capitalize()) },
            icon = { Icon(Icons.Sharp.DataObject, contentDescription = null) },
            modifier = Modifier
                .clip(PreferenceShape.Middle)
                .background(backgroundColor)
        )

        switchPreference(
            key = ACCEPT_SUGGESTION_ON_COMMIT_CHARACTER.name,
            title = { Text(text = "Accept suggestion on commit character") },
            summary = { Text(text = "Should accept suggestion on commit character.") },
            rememberState = { acceptSuggestionOnCommitCharacter },
            defaultValue = acceptSuggestionOnCommitCharacter.value,
            icon = { Icon(PromptSuggestion, contentDescription = null) },
            modifier = Modifier
                .clip(PreferenceShape.Middle)
                .background(backgroundColor)
        )

        listPreference(
            key = ACCEPT_SUGGESTION_ON_ENTER.name,
            defaultValue = acceptSuggestionOnEnter.value,
            title = { Text(text = "Accept suggestion on enter") },
            summary = { Text(text = it.capitalize()) },
            rememberState = { acceptSuggestionOnEnter },
            values = listOf(
                AcceptSuggestionOnEnter.On.value,
                AcceptSuggestionOnEnter.Off.value,
                AcceptSuggestionOnEnter.Smart.value
            ),
            valueToText = { AnnotatedString(it.capitalize()) },
            icon = { Icon(PromptSuggestion, contentDescription = null) },
            modifier = Modifier
                .clip(PreferenceShape.Middle)
                .background(backgroundColor)
        )

        switchPreference(
            key = FOLDING.name,
            title = { Text(text = "Folding") },
            summary = { Text(text = "Should fold code.") },
            rememberState = { folding },
            defaultValue = folding.value,
            icon = { Icon(Icons.Sharp.Code, contentDescription = null) },
            modifier = Modifier
                .clip(PreferenceShape.Middle)
                .background(backgroundColor)
        )

        switchPreference(
            key = GLYPH_MARGIN.name,
            title = { Text(text = "Glyph margin") },
            summary = { Text(text = "Should show glyph margin.") },
            rememberState = { glyphMargin },
            defaultValue = glyphMargin.value,
            icon = { Icon(Glyphs, contentDescription = null) },
            modifier = Modifier
                .clip(PreferenceShape.Bottom)
                .background(backgroundColor)
        )

        preferenceCategory(
            key = "word_wrap_category",
            title = { Text(text = "Word wrap") }
        )

        listPreference(
            key = WORD_WRAP.name,
            defaultValue = wordWrap.value,
            title = { Text(text = "Word wrap") },
            summary = { Text(text = it.capitalize()) },
            rememberState = { wordWrap },
            values = listOf(
                WordWrap.On.value,
                WordWrap.Off.value,
                WordWrap.WordWrapColumn.value,
                WordWrap.Bounded.value
            ),
            valueToText = { AnnotatedString(it.capitalize()) },
            icon = { Icon(Icons.AutoMirrored.Sharp.WrapText, contentDescription = null) },
            modifier = Modifier
                .clip(PreferenceShape.Top)
                .background(backgroundColor)
        )

        listPreference(
            key = WORD_BREAK.name,
            defaultValue = wordBreak.value,
            title = { Text(text = "Word break") },
            summary = { Text(text = it.capitalize()) },
            rememberState = { wordBreak },
            values = listOf(
                WordBreak.Normal.value,
                WordBreak.KeepAll.value
            ),
            valueToText = { AnnotatedString(it.capitalize()) },
            icon = { Icon(Icons.AutoMirrored.Sharp.Notes, contentDescription = null) },
            modifier = Modifier
                .clip(PreferenceShape.Middle)
                .background(backgroundColor)
        )

        listPreference(
            key = WRAPPING_STRATEGY.name,
            defaultValue = wrappingStrategy.value,
            title = { Text(text = "Wrapping strategy") },
            summary = { Text(text = it.capitalize()) },
            rememberState = { wrappingStrategy },
            values = listOf(
                WrappingStrategy.Simple.value,
                WrappingStrategy.Advanced.value
            ),
            valueToText = { AnnotatedString(it.capitalize()) },
            icon = { Icon(FormatTextWrap, contentDescription = null) },
            modifier = Modifier
                .clip(PreferenceShape.Bottom)
                .background(backgroundColor)
        )

        preferenceCategory(
            key = "cursor_category",
            title = { Text(text = "Cursor") }
        )

        listPreference(
            key = CURSOR_STYLE.name,
            defaultValue = cursorStyle.value,
            title = { Text(text = "Cursor style") },
            summary = { Text(text = TextEditorCursorStyle.fromValue(it).toString()) },
            rememberState = { cursorStyle },
            values = listOf(
                TextEditorCursorStyle.Line.value,
                TextEditorCursorStyle.Block.value,
                TextEditorCursorStyle.Underline.value,
                TextEditorCursorStyle.LineThin.value,
                TextEditorCursorStyle.BlockOutline.value,
                TextEditorCursorStyle.UnderlineThin.value
            ),
            valueToText = { AnnotatedString(TextEditorCursorStyle.fromValue(it).toString()) },
            icon = { Icon(CursorText, contentDescription = null) },
            modifier = Modifier
                .clip(PreferenceShape.Top)
                .background(backgroundColor)
        )

        listPreference(
            key = CURSOR_BLINKING_STYLE.name,
            defaultValue = cursorBlinkingStyle.value,
            title = { Text(text = "Cursor blinking style") },
            summary = { Text(text = TextEditorCursorBlinkingStyle.fromValue(it).toString()) },
            rememberState = { cursorBlinkingStyle },
            values = listOf(
                TextEditorCursorBlinkingStyle.Hidden.value,
                TextEditorCursorBlinkingStyle.Blink.value,
                TextEditorCursorBlinkingStyle.Smooth.value,
                TextEditorCursorBlinkingStyle.Phase.value,
                TextEditorCursorBlinkingStyle.Expand.value,
                TextEditorCursorBlinkingStyle.Solid.value,
            ),
            valueToText = {
                AnnotatedString(
                    TextEditorCursorBlinkingStyle.fromValue(it).toString()
                )
            },
            icon = { Icon(CursorText, contentDescription = null) },
            modifier = Modifier
                .clip(PreferenceShape.Bottom)
                .background(backgroundColor)
        )
    }
}
