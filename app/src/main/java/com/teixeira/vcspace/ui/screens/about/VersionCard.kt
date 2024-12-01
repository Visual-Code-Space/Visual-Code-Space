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

package com.teixeira.vcspace.ui.screens.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.blankj.utilcode.util.ClipboardUtils
import com.teixeira.vcspace.BuildConfig
import com.teixeira.vcspace.resources.R
import com.teixeira.vcspace.app.drawables
import com.teixeira.vcspace.app.strings
import com.teixeira.vcspace.ui.extensions.harmonizeWithPrimary

@Composable
fun VersionCard(modifier: Modifier = Modifier) {
  OutlinedCard(modifier = modifier) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth()
    ) {
      Image(
        ImageBitmap.imageResource(drawables.vcspace_icon),
        contentDescription = null,
        modifier = Modifier
          .padding(16.dp)
          .clip(CircleShape)
          .size(64.dp)
      )

      Text(
        text = stringResource(strings.app_name),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.SansSerif
      )
      Text(
        text = stringResource(R.string.app_description),
        style = MaterialTheme.typography.titleMedium,
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Light
      )

      Text(
        text = buildAnnotatedString {
          append("v${BuildConfig.VERSION_NAME} (")
          withStyle(
            style = SpanStyle(
              color = (if (BuildConfig.DEBUG) Color(0xFFFF1E1E)
              else Color(0xFF19E319)).harmonizeWithPrimary(0.2f)
            )
          ) {
            append(BuildConfig.BUILD_TYPE)
          }
          append(")")
        },
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
          .padding(5.dp)
          .pointerInput(Unit) {
            detectTapGestures(
              onTap = {
                ClipboardUtils.copyText("Version: ${BuildConfig.VERSION_NAME}")
              }
            )
          }
      )
    }
  }
}