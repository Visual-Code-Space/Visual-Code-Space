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

package com.teixeira.vcspace.activities.plugin

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.teixeira.vcspace.preferences.appearanceMaterialYou
import com.teixeira.vcspace.preferences.appearanceUIMode
import com.teixeira.vcspace.ui.theme.VCSpaceTheme
import com.teixeira.vcspace.utils.isDarkMode

class PluginsActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val darkTheme = when (appearanceUIMode) {
      AppCompatDelegate.MODE_NIGHT_NO -> false
      AppCompatDelegate.MODE_NIGHT_YES -> true
      AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> isDarkMode()
      else -> isDarkMode()
    }

    enableEdgeToEdge(
      statusBarStyle = when (darkTheme) {
        true -> SystemBarStyle.dark(Color.TRANSPARENT)
        false -> SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
      }
    )

    setContent {
      VCSpaceTheme(
        dynamicColor = appearanceMaterialYou,
        darkTheme = darkTheme
      ) {
        Surface(modifier = Modifier.fillMaxSize()) {
          PluginsScreen()
        }
      }
    }
  }
}