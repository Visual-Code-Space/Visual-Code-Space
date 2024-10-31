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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.MailOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.teixeira.vcspace.app.drawables
import com.teixeira.vcspace.ui.extensions.harmonizeWithPrimary

@Composable
fun SocialCard(modifier: Modifier = Modifier) {
  val socials = listOf(
    SocialItem(
      icon = Icons.Rounded.Language,
      title = "Website",
      url = "https://visualcodespace.com.br/"
    ),
    SocialItem(
      icon = Icons.Rounded.MailOutline,
      title = "Email",
      url = "contact@visualcodespace.com.br"
    ),
    SocialItem(
      icon = ImageVector.vectorResource(drawables.ic_telegram),
      title = "Telegram",
      url = "https://t.me/vc_space"
    )
  )

  OutlinedCard(modifier = modifier) {
    Column {
      Text(
        text = "Socials",
        modifier = Modifier.padding(16.dp),
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.SemiBold
      )

      LazyColumn {
        items(socials) {
          SocialListItem(
            modifier = Modifier.fillMaxWidth(),
            icon = it.icon,
            title = it.title,
            url = it.url
          )
        }
      }
    }
  }
}

@Composable
fun SocialListItem(
  modifier: Modifier = Modifier,
  icon: ImageVector,
  title: String,
  url: String
) {
  val uriHandler = LocalUriHandler.current

  Row(
    modifier = modifier
      .clip(RoundedCornerShape(16.dp))
      .clickable {
        if (title == "Email") {
          uriHandler.openUri("mailto:${url}")
        } else {
          uriHandler.openUri(url)
        }
      },
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      modifier = Modifier.padding(start = 14.dp, end = 2.dp)
    )

    Column(
      modifier = Modifier.padding(10.dp)
    ) {
      Text(
        text = title,
        style = MaterialTheme.typography.bodyMedium
      )

      Text(
        text = url,
        color = Color(0xFF2A61E7).harmonizeWithPrimary(0.4f),
        style = MaterialTheme.typography.bodySmall
      )
    }
  }
}

data class SocialItem(
  val icon: ImageVector,
  val title: String,
  val url: String
)
