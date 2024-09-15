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

package com.teixeira.vcspace.screens

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.teixeira.vcspace.resources.R

sealed class PluginScreens(
  val route: String,
  val title: String,
  val icon: (@Composable () -> Unit)? = null
) {
  data object Explore : PluginScreens(
    route = "explore",
    title = "Explore",
    icon = {
      Icon(ImageVector.vectorResource(R.drawable.ic_explore), contentDescription = null)
    }
  )

  data object Installed : PluginScreens(
    route = "installed",
    title = "Installed",
    icon = {
      Icon(ImageVector.vectorResource(R.drawable.ic_download), contentDescription = null)
    }
  )
}