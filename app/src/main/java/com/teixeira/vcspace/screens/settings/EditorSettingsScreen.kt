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

package com.teixeira.vcspace.screens.settings

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
import androidx.compose.material.icons.filled.FontDownload
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastRoundToInt
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
import com.teixeira.vcspace.core.settings.Settings.EditorTabs.rememberAutoSave
import com.teixeira.vcspace.resources.R
import me.zhanghai.compose.preference.listPreference
import me.zhanghai.compose.preference.preferenceCategory
import me.zhanghai.compose.preference.sliderPreference
import me.zhanghai.compose.preference.switchPreference

@Composable
fun EditorSettingsScreen(
  modifier: Modifier = Modifier,
  onNavigateUp: () -> Unit
) {
  val context = LocalContext.current

  val fontSize = rememberFontSize()
  val indentSize = rememberIndentSize()
  val fontFamily = rememberFontFamily()
  val colorScheme = rememberColorScheme()
  val fontLigatures = rememberFontLigatures()
  val stickyScroll = rememberStickyScroll()
  val wordWrap = rememberWordWrap()
  val lineNumber = rememberLineNumber()
  val useTab = rememberUseTab()
  val deleteLineOnBackspace = rememberDeleteLineOnBackspace()
  val deleteIndentOnBackspace = rememberDeleteIndentOnBackspace()

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
      key = "editor_settings_category",
      title = { Text(text = stringResource(R.string.editor_settings)) }
    )

    sliderPreference(
      key = "font_size_preference",
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
      key = "indent_size_preference",
      title = { Text(stringResource(R.string.indent_size)) },
      summary = { Text(stringResource(R.string.indent_size_summary, indentSize.value)) },
      rememberState = { indentSize },
      defaultValue = indentSize.value,
      values = (2..8 step 2).map { it },
      icon = { Icon(Icons.AutoMirrored.Filled.FormatIndentIncrease, contentDescription = null) },
      modifier = Modifier
        .clip(PreferenceShape.Middle)
        .background(backgroundColor)
    )

    listPreference(
      key = "font_family_preference",
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
      key = "color_scheme_preference",
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
        "Python Dark Mode",
      ),
      icon = { Icon(Icons.Filled.Palette, contentDescription = null) },
      modifier = Modifier
        .clip(PreferenceShape.Middle)
        .background(backgroundColor)
    )

    switchPreference(
      key = "font_ligatures_preference",
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

    switchPreference(
      key = "sticky_scroll_preference",
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
      icon = { Icon(imageVector = Icons.Default.VerticalAlignTop, contentDescription = null) },
      modifier = Modifier
        .clip(PreferenceShape.Middle)
        .background(backgroundColor)
    )

    switchPreference(
      key = "word_wrap_preference",
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
      icon = { Icon(imageVector = Icons.AutoMirrored.Filled.WrapText, contentDescription = null) },
      modifier = Modifier
        .clip(PreferenceShape.Middle)
        .background(backgroundColor)
    )

    switchPreference(
      key = "line_numbers_preference",
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
      icon = { Icon(imageVector = Icons.AutoMirrored.Filled.List, contentDescription = null) },
      modifier = Modifier
        .clip(PreferenceShape.Middle)
        .background(backgroundColor)
    )

    switchPreference(
      key = "use_tabs_preference",
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
      key = "delete_line_on_backspace_preference",
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
      icon = { Icon(imageVector = Icons.AutoMirrored.Filled.Backspace, contentDescription = null) },
      modifier = Modifier
        .clip(PreferenceShape.Middle)
        .background(backgroundColor)
    )

    switchPreference(
      key = "delete_indent_on_backspace_preference",
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
        .clip(PreferenceShape.Bottom)
        .background(backgroundColor)
    )

    preferenceCategory(
      key = "tabs_category",
      title = { Text(text = stringResource(R.string.tabs_category)) }
    )

    switchPreference(
      key = "auto_save_preference",
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
