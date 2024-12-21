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

package com.teixeira.vcspace.ui.components.ai

import android.view.View
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.blankj.utilcode.util.ToastUtils
import com.google.ai.client.generativeai.type.asTextOrNull
import com.itsvks.monaco.MonacoEditor
import com.teixeira.vcspace.app.strings
import com.teixeira.vcspace.core.ai.Gemini
import com.teixeira.vcspace.ui.screens.editor.components.view.CodeEditorView
import com.teixeira.vcspace.utils.launchWithProgressDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun GenerateContentDialog(
  editor: View,
  modifier: Modifier = Modifier,
  fileExtension: String? = null,
) {
  val scope = rememberCoroutineScope()
  val context = LocalContext.current
  var prompt by remember { mutableStateOf("") }
  var showDialog by remember { mutableStateOf(true) }

  if (showDialog) {
    AlertDialog(
      modifier = modifier,
      onDismissRequest = { showDialog = false },
      title = {
        Text(text = stringResource(strings.generate_code))
      },
      text = {
        OutlinedTextField(
          value = prompt,
          onValueChange = { prompt = it },
          label = { Text(stringResource(strings.tell_me_what_you_want_to_generate)) },
          modifier = Modifier.fillMaxWidth()
        )
      },
      dismissButton = {
        TextButton(onClick = { showDialog = false }) {
          Text(stringResource(strings.cancel))
        }
      },
      confirmButton = {
        TextButton(onClick = {
          if (prompt.isNotEmpty()) {
            scope.launchWithProgressDialog(
              uiContext = context,
              configureBuilder = {
                it.apply {
                  setMessage(strings.generating_code)
                  setCancelable(false)
                }
              }
            ) { _, _ ->
              Gemini.generateCode(
                prompt = prompt,
                fileExtension = fileExtension
              ).onSuccess { response ->
                val text = response.candidates.first().content.parts.first().asTextOrNull()

                withContext(Dispatchers.Main) {
                  if (editor is MonacoEditor) {
                    val position = editor.position
                    editor.insert(
                      text = Gemini.removeBackticksFromMarkdownCodeBlock(text),
                      position = position
                    )
                  } else if (editor is CodeEditorView) {
                    val vcSpaceEditor = editor.editor
                    val cursor = vcSpaceEditor.cursor
                    val content = vcSpaceEditor.text
                    content.insert(
                      cursor.leftLine,
                      cursor.leftColumn,
                      Gemini.removeBackticksFromMarkdownCodeBlock(text)
                    )
                  }
                }
              }.onFailure {
                ToastUtils.showShort(it.message)
              }
            }
            showDialog = false
          } else {
            ToastUtils.showShort(context.getString(strings.enter_prompt))
          }
        }) {
          Text(stringResource(strings.generate))
        }
      }
    )
  }
}
