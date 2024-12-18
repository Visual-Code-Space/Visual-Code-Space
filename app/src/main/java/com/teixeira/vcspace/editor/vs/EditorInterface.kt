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

package com.teixeira.vcspace.editor.vs

import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.teixeira.vcspace.utils.showLongToast

class EditorInterface(private val webView: WebView) {
  @JavascriptInterface
  fun openFile(content: String, fileName: String) {
    showLongToast(webView.context, "Opening file: $content")
  }
}