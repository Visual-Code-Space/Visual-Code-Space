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

package com.teixeira.vcspace.editor.textaction

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Comment
import androidx.compose.material.icons.automirrored.rounded.FormatAlignLeft
import androidx.compose.material.icons.automirrored.rounded.ManageSearch
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.ContentCut
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.SelectAll
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.ui.graphics.vector.ImageVector
import com.teixeira.vcspace.resources.R

data class EditorTextActionItem(
  val id: Int,
  val icon: ImageVector,
  val description: String,
  var visible: Boolean = true,
  var clickable: Boolean = true
)

val actionItems = listOf(
  EditorTextActionItem(
    id = R.string.editor_action_comment_line,
    icon = Icons.Rounded.EditNote,
    description = "Add a comment to the current line."
  ),
  EditorTextActionItem(
    id = R.string.editor_action_select_all,
    icon = Icons.Rounded.SelectAll,
    description = "Select all text in the editor."
  ),
  EditorTextActionItem(
    id = R.string.editor_action_long_select,
    icon = Icons.Rounded.TouchApp,
    description = "Enable long selection mode."
  ),
  EditorTextActionItem(
    id = R.string.editor_action_cut,
    icon = Icons.Rounded.ContentCut,
    description = "Cut the selected text to the clipboard."
  ),
  EditorTextActionItem(
    id = R.string.editor_action_copy,
    icon = Icons.Rounded.ContentCopy,
    description = "Copy the selected text to the clipboard."
  ),
  EditorTextActionItem(
    id = R.string.editor_action_paste,
    icon = Icons.Rounded.ContentPaste,
    description = "Paste text from the clipboard."
  ),
  EditorTextActionItem(
    id = R.string.editor_action_format,
    icon = Icons.AutoMirrored.Rounded.FormatAlignLeft,
    description = "Format the selected text."
  ),
  EditorTextActionItem(
    id = R.string.editor_action_explain_code,
    icon = Icons.AutoMirrored.Rounded.Comment,
    description = "Explain the selected code."
  ),
  EditorTextActionItem(
    id = R.string.editor_action_import_components,
    icon = Icons.AutoMirrored.Rounded.ManageSearch,
    description = "Import components (Jetpack Compose)."
  )
)