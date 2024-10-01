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

package com.teixeira.vcspace.core.components.editor

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.teixeira.vcspace.editor.CodeEditorView
import java.io.File

@Composable
fun rememberCodeEditorState(
  initialFile: File
): CodeEditorState {
  return remember {
    CodeEditorState(initialFile = initialFile)
  }.apply {
    file = initialFile
  }
}

@Composable
fun CodeEditor(
  modifier: Modifier = Modifier,
  file: File
) {
  val context = LocalContext.current

  var content by mutableStateOf("")

  LaunchedEffect(file) {
    content = file.readText()
  }

  val editorView = remember(file) {
    setCodeEditorFactory(
      context = context,
      file = file
    )
  }

  AndroidView(
    factory = { editorView },
    modifier = modifier,
    onRelease = { it.release() }
  )
}

private fun setCodeEditorFactory(
  context: Context,
  file: File
) = CodeEditorView(context, file)

data class CodeEditorState(
  var editorView: CodeEditorView? = null,
  val initialFile: File
) {
  var file by mutableStateOf(initialFile)
}
