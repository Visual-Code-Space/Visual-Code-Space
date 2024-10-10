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

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.teixeira.vcspace.resources.R.string
import com.teixeira.vcspace.activities.base.BaseComposeActivity
import com.teixeira.vcspace.ui.screens.settings.SettingsScreen

class SettingsActivity : BaseComposeActivity() {
  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  override fun MainScreen() {
    val scrollBehavior =
      TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    Scaffold(
      modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
      topBar = {
        LargeTopAppBar(
          title = {
            Text(
              text = stringResource(id = string.settings),
            )
          },
          navigationIcon = {
            IconButton(
              onClick = { backPressedDispatcher?.onBackPressed() },
              modifier = Modifier.padding(start = 8.dp)
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = "back",
              )
            }
          },
          scrollBehavior = scrollBehavior,
          modifier = Modifier.fillMaxWidth(),
        )
      }
    ) { innerPadding ->
      SettingsScreen(
        modifier = Modifier.padding(innerPadding)
      )
    }
  }
}
