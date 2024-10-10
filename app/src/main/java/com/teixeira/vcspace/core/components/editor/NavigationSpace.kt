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

package com.teixeira.vcspace.core.components.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun NavigationSpace(
  modifier: Modifier = Modifier,
  state: NavigationSpaceState = rememberNavigationSpaceState(),
  onItemClick: (NavigationSpaceItem) -> Unit
) {
  val items = remember { state.items }

  Row(
    modifier = modifier
      .height(52.dp)
      .fillMaxWidth()
      .padding(horizontal = 2.dp, vertical = 2.dp),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    repeat(items.size) {
      val item = items[it]

      ProvideTextStyle(
        MaterialTheme.typography.labelSmall
      ) {
        Card(
          modifier = Modifier
            .fillMaxSize()
            .padding(3.dp)
            .weight(1f),
          colors = CardDefaults.cardColors().copy(
            containerColor = Color.Transparent
          ),
          onClick = { onItemClick(item) }
        ) {
          Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = item.icon,
              contentDescription = item.title,
              modifier = Modifier.size(20.dp)
            )

            Text(text = item.title)
          }
        }
      }
    }
  }
}

@Composable
fun rememberNavigationSpaceState(
  items: SnapshotStateList<NavigationSpaceItem> = mutableStateListOf()
) = remember {
  NavigationSpaceState(items = items)
}

data class NavigationSpaceItem(
  val id: Int,
  val icon: ImageVector,
  val title: String,
)

class NavigationSpaceState(
  val items: SnapshotStateList<NavigationSpaceItem>
) {
  fun add(item: NavigationSpaceItem) {
    items.add(item)
  }
}