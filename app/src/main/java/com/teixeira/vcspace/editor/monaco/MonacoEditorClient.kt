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

package com.teixeira.vcspace.editor.monaco

import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewAssetLoader.AssetsPathHandler
import androidx.webkit.WebViewClientCompat
import kotlinx.coroutines.launch

class MonacoEditorClient(private val editor: MonacoEditor) : WebViewClientCompat() {
  private val assetLoader = WebViewAssetLoader.Builder()
    .addPathHandler("/assets/", AssetsPathHandler(editor.context))
    .build()

  override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
    return false
  }

  override fun shouldInterceptRequest(
    view: WebView,
    request: WebResourceRequest
  ): WebResourceResponse? {
    return assetLoader.shouldInterceptRequest(request.url)
  }

  override fun onPageFinished(view: WebView, url: String) {
    super.onPageFinished(view, url)
    view.requestFocus(View.FOCUS_DOWN)
    editor.apply {
      editorScope.launch {
        onEditorLoadCallbacks.forEach { callback -> callback(this@apply) }
      }
    }
  }
}
