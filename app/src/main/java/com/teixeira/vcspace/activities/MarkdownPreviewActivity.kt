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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.teixeira.vcspace.activities.base.BaseComposeActivity
import dev.jeziellago.compose.markdowntext.MarkdownText
import java.io.File

class MarkdownPreviewActivity : BaseComposeActivity() {
    companion object {
        const val EXTRA_FILE_PATH = "file_path"
    }

    @Composable
    override fun MainScreen() {
        val filePath = intent.getStringExtra(EXTRA_FILE_PATH)

        if (filePath != null) {
            val fileContent = remember { File(filePath).readText() }

            Scaffold { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    MarkdownText(
                        markdown = fileContent,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        isTextSelectable = true,
                        linkColor = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
