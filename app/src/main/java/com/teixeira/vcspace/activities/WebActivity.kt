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

package com.teixeira.vcspace.activities

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.ToastUtils
import com.teixeira.vcspace.activities.base.BaseComposeActivity
import com.teixeira.vcspace.editor.monaco.MonacoEditor
import com.teixeira.vcspace.editor.monaco.MonacoLanguage
import com.teixeira.vcspace.editor.monaco.MonacoTheme
import com.teixeira.vcspace.editor.monaco.option.TextEditorCursorStyle
import com.teixeira.vcspace.editor.monaco.option.WordWrap
import com.teixeira.vcspace.editor.monaco.option.minimap.MinimapOptions
import kotlinx.coroutines.launch

class WebActivity : BaseComposeActivity() {
  @SuppressLint("SetJavaScriptEnabled")
  @Composable
  override fun MainScreen() {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .systemBarsPadding()
    ) {
      AndroidView(
        modifier = Modifier
          .fillMaxSize()
          .imePadding(),
        factory = {
          MonacoEditor(it).apply {
            layoutParams = ViewGroup.LayoutParams(
              ViewGroup.LayoutParams.MATCH_PARENT,
              ViewGroup.LayoutParams.MATCH_PARENT
            )
          }
        },
        update = {
          it.apply {
            addOnEditorLoadCallback {
              setText("function example() {\n\tconsole.log('Hello, World!');\n}")
              setLanguage(MonacoLanguage.CSS)
              setFontSize(14)
              setTheme(MonacoTheme.HighContrastLight)
              setWordWrap(WordWrap.On)
              setReadOnly(false)
              setMinimapOptions(MinimapOptions(enabled = false))
              setCursorStyle(TextEditorCursorStyle.Line)
              ToastUtils.showShort(it.getText())
            }
          }
        }
      )
    }
  }
}
