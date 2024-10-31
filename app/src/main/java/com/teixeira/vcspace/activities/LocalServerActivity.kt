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
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.teixeira.vcspace.activities.base.BaseComposeActivity

class LocalServerActivity : BaseComposeActivity() {
  companion object {
    const val SERVER_URI = "server_uri"
  }

  @SuppressLint("SetJavaScriptEnabled")
  @Composable
  override fun MainScreen() {
    val serverUri = intent.getStringExtra(SERVER_URI) ?: ""

    var progress by remember { mutableStateOf(0f) }

    Column(
      modifier = Modifier
        .fillMaxSize()
        .systemBarsPadding()
    ) {
      if (progress < 1f) {
        LinearProgressIndicator(
          progress = { progress },
          modifier = Modifier.fillMaxWidth()
        )
      }

      TextField(
        value = serverUri,
        onValueChange = { },
        modifier = Modifier.fillMaxWidth()
      )

      AndroidView(
        factory = { WebView(it) },
        modifier = Modifier.fillMaxSize(),
        update = { webView ->
          webView.apply {
            webChromeClient = object : WebChromeClient() {
              override fun onProgressChanged(view: WebView, newProgress: Int) {
                progress = newProgress / 100f
              }

              override fun onReceivedTitle(view: WebView, title: String) {

              }

              override fun onReceivedIcon(view: WebView, icon: Bitmap) {

              }
            }

            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            loadUrl(serverUri)
          }
        }
      )
    }
  }
}