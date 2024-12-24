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
import com.blankj.utilcode.util.ToastUtils
import com.itsvks.monaco.MonacoEditor
import com.itsvks.monaco.MonacoLanguage
import com.itsvks.monaco.MonacoTheme
import com.itsvks.monaco.option.TextEditorCursorBlinkingStyle
import com.itsvks.monaco.option.TextEditorCursorStyle
import com.itsvks.monaco.option.WordWrap
import com.itsvks.monaco.option.minimap.MinimapOptions
import com.teixeira.vcspace.activities.base.BaseComposeActivity

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
        update = { editor ->
          editor.apply {
            addOnEditorLoadCallback {
              text = "function example() {\n\tconsole.log('Hello, World!');\n}"
              setLanguage(MonacoLanguage.Javascript)
              setFontSize(14)
              setTheme(MonacoTheme.HighContrastDark)
              setWordWrap(WordWrap.On)
              setReadOnly(false)
              setMinimapOptions(MinimapOptions(enabled = false))
              setCursorStyle(TextEditorCursorStyle.Line)
              setCursorBlinkingStyle(TextEditorCursorBlinkingStyle.Phase)
              ToastUtils.showShort(it.text)
            }
          }
        }
      )
    }
  }
}
