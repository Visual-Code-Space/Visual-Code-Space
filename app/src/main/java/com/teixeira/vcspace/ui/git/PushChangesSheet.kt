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

package com.teixeira.vcspace.ui.git

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.eclipse.jgit.revwalk.RevCommit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PushChangesSheet(
  onDismissRequest: () -> Unit,
  commits: List<RevCommit>,
  modifier: Modifier = Modifier,
  onPushClick: () -> Unit = {},
) {
  val coroutineScope = rememberCoroutineScope()
  val sheetState = rememberModalBottomSheetState()

  val hide = remember {
    suspend {
      coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
        if (!sheetState.isVisible) {
          onDismissRequest()
        }
      }
    }
  }

  ModalBottomSheet(
    onDismissRequest = onDismissRequest,
    modifier = modifier
  ) {
    Column(
      modifier = Modifier
        .padding(16.dp)
        .fillMaxSize(),
      horizontalAlignment = Alignment.Start
    ) {
      Text(
        text = "Push Commits",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 28.sp,
        letterSpacing = (-0.015).sp
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "This will upload your local commits to the remote repository.",
        fontSize = 16.sp,
        lineHeight = 20.sp
      )

      Spacer(modifier = Modifier.height(16.dp))

      repeat(commits.size) {
        Text(
          text = commits[it].shortMessage,
          modifier = Modifier.padding(8.dp)
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      Button(
        onClick = {
          coroutineScope.launch {
            hide()
            onPushClick()
          }
        },
        modifier = Modifier.fillMaxWidth(),
        enabled = commits.isNotEmpty()
      ) {
        Text(
          text = "Push",
          fontWeight = FontWeight.SemiBold,
          letterSpacing = (0.015).em
        )
      }
    }
  }
}