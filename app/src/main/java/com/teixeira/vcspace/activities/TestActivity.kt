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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.teixeira.vcspace.activities.base.BaseComposeActivity
import kotlinx.coroutines.launch

class TestActivity : BaseComposeActivity() {
  @SuppressLint("SetJavaScriptEnabled")
  @Composable
  override fun MainScreen() {
    val compositionContext = rememberCompositionContext()
    val scope = rememberCoroutineScope()

    Box(
      modifier = Modifier
        .fillMaxSize()
        .systemBarsPadding()
    ) {
      Button(onClick = {
        scope.launch {
        }
      }) {
        Text("Hello")
      }
    }
  }
}
