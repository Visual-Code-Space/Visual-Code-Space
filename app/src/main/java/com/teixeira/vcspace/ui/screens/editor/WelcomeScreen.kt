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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.teixeira.vcspace.ui.extensions.harmonizeWithPrimary

@Composable
fun WelcomeScreen(
    onOpenFile: () -> Unit,
    onNewFile: () -> Unit,
    onOpenFolder: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(
            Brush.verticalGradient(
                listOf(
                    MaterialTheme.colorScheme.background,
                    MaterialTheme.colorScheme.background.harmonizeWithPrimary(0.05f)
                )
            )
        ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(16.dp),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.harmonizeWithPrimary(0.1f)
            )
        ) {
            WelcomeScreenContent(
                onOpenFile = onOpenFile,
                onNewFile = onNewFile,
                onOpenFolder = onOpenFolder
            )
        }
    }
}

@Composable
private fun WelcomeScreenContent(
    onOpenFile: () -> Unit,
    onNewFile: () -> Unit,
    onOpenFolder: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to Visual Code Space",
            color = MaterialTheme.colorScheme.onSurface.harmonizeWithPrimary(fraction = 0.4f),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Open a file or folder to start coding",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WelcomeScreenButton(
                text = "Open File",
                onClick = onOpenFile
            )

            Spacer(modifier = Modifier.width(16.dp))

            WelcomeScreenButton(
                text = "New File",
                onClick = onNewFile
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onOpenFolder,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Open Folder")
        }
    }
}

@Composable
private fun RowScope.WelcomeScreenButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.weight(1f)
    ) {
        Text(text = text)
    }
}
