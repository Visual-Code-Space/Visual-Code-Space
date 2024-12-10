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

package com.teixeira.vcspace.ui.screens.editor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.asTextOrNull
import com.teixeira.vcspace.app.strings
import dev.jeziellago.compose.markdowntext.MarkdownText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeExplanationSheet(
  response: GenerateContentResponse,
  onDismissRequest: () -> Unit,
  modifier: Modifier = Modifier
) {
  val text = response.candidates[0].content.parts[0].asTextOrNull().toString()
  val usageMetadata = response.usageMetadata

  var showUsageMetadata by remember { mutableStateOf(false) }

  ModalBottomSheet(onDismissRequest = onDismissRequest, modifier = modifier) {
    Column(
      modifier = Modifier
        .padding(16.dp)
        .verticalScroll(rememberScrollState())
    ) {
      Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 16.dp)
      ) {
        Text(
          text = stringResource(strings.code_explanation),
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.weight(1f)
        )

        AnimatedVisibility(visible = !showUsageMetadata) {
          IconButton(
            onClick = { showUsageMetadata = !showUsageMetadata },
            modifier = Modifier.size(12.dp)
          ) {
            Icon(
              Icons.Default.QuestionMark,
              contentDescription = null
            )
          }
        }

        AnimatedVisibility(visible = showUsageMetadata) {
          Text(
            text = "total: ${usageMetadata?.totalTokenCount}, prompt: ${usageMetadata?.promptTokenCount}, candidates: ${usageMetadata?.candidatesTokenCount}",
            fontSize = 9.sp,
            fontWeight = FontWeight.W200,
            modifier = Modifier.clickable(
              onClick = { showUsageMetadata = !showUsageMetadata },
              interactionSource = remember { MutableInteractionSource() },
              indication = null
            )
          )
        }
      }

      MarkdownText(
        markdown = text,
        isTextSelectable = true
      )
    }
  }
}
