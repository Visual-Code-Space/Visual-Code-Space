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

package com.teixeira.vcspace.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

fun <T : Any> NavHostController.navigateSingleTop(route: T) {
  navigate(route) {
    launchSingleTop = true
    restoreState = true
  }
}

@Composable
fun ErrorMessage(modifier: Modifier = Modifier, message: String) {
  Text(
    text = message,
    color = MaterialTheme.colorScheme.error,
    style = MaterialTheme.typography.bodySmall,
    modifier = modifier.padding(top = 4.dp)
  )
}

@Composable
fun LoadingIndicator() {
  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    CircularProgressIndicator()
  }
}

@Composable
fun InputField(
  modifier: Modifier = Modifier,
  label: String,
  value: String,
  placeholder: @Composable (() -> Unit)? = null,
  onValueChange: (String) -> Unit,
  singleLine: Boolean = true,
  maxLines: Int = 1,
  isError: Boolean = false
) {
  OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    label = { Text(label) },
    placeholder = placeholder,
    isError = isError,
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    singleLine = singleLine,
    maxLines = maxLines,
    keyboardOptions = KeyboardOptions(
      imeAction = if (singleLine) ImeAction.Next else ImeAction.Default
    )
  )
}