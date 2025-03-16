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

package com.teixeira.vcspace.compose.ui.filetree

import android.annotation.SuppressLint
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.ui.graphics.vector.ImageVector
import com.teixeira.vcspace.file.File
import com.teixeira.vcspace.file.extension
import com.teixeira.vcspace.ui.icon.LanguageCpp
import com.teixeira.vcspace.ui.icons.Icons
import com.teixeira.vcspace.ui.icons.LanguageC
import com.teixeira.vcspace.ui.icons.LanguageCsharp
import com.teixeira.vcspace.ui.icons.LanguageCss3
import com.teixeira.vcspace.ui.icons.LanguageGo
import com.teixeira.vcspace.ui.icons.LanguageHtml5
import com.teixeira.vcspace.ui.icons.LanguageJava
import com.teixeira.vcspace.ui.icons.LanguageJavascript
import com.teixeira.vcspace.ui.icons.LanguageKotlin
import com.teixeira.vcspace.ui.icons.LanguageLua
import com.teixeira.vcspace.ui.icons.LanguageMarkdown
import com.teixeira.vcspace.ui.icons.LanguagePhp
import com.teixeira.vcspace.ui.icons.LanguagePython
import com.teixeira.vcspace.ui.icons.LanguageRust
import com.teixeira.vcspace.ui.icons.LanguageSwift
import com.teixeira.vcspace.ui.icons.LanguageTypescript
import com.teixeira.vcspace.ui.icons.LanguageXml
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * Data class representing a file or directory in the file tree
 */
data class FileTreeNode(
    val file: File,
    val children: List<FileTreeNode> = emptyList()
)

suspend fun createFileTreeFromPath(file: File): FileTreeNode {
    val mutex = Mutex()

    return withContext(Dispatchers.IO) {
        FileTreeNode(
            file = file,
            children = if (file.isDirectory) {
                file.listFiles()?.map { file ->
                    mutex.withLock {
                        if (file.isDirectory) {
                            createFileTreeFromPath(file)
                        } else {
                            FileTreeNode(file)
                        }
                    }
                }?.sortedWith(compareBy({ !it.file.isDirectory }, { it.file.name.lowercase() }))
                    ?: emptyList()
            } else {
                emptyList()
            }
        )
    }
}

@SuppressLint("MaterialDesignInsteadOrbitDesign")
fun getIconForFile(node: FileTreeNode): ImageVector {
    return when (node.file.extension) {
        "c" -> Icons.LanguageC
        "cpp" -> Icons.LanguageCpp
        "cs" -> Icons.LanguageCsharp
        "css" -> Icons.LanguageCss3
        "html" -> Icons.LanguageHtml5
        "go" -> Icons.LanguageGo
        "lua" -> Icons.LanguageLua
        "rs" -> Icons.LanguageRust
        "md" -> Icons.LanguageMarkdown
        "php" -> Icons.LanguagePhp
        "py" -> Icons.LanguagePython
        "swift" -> Icons.LanguageSwift
        "java" -> Icons.LanguageJava
        "js", "jsx" -> Icons.LanguageJavascript
        "kt", "kts" -> Icons.LanguageKotlin
        "ts", "tsx" -> Icons.LanguageTypescript
        "xml" -> Icons.LanguageXml
        "jpg", "jpeg", "png", "gif", "bmp", "svg", "ico" -> androidx.compose.material.icons.Icons.Default.Image
        "mp4", "avi", "mkv", "mov", "wmv", "flv", "webm", "ogv" -> androidx.compose.material.icons.Icons.Default.VideoFile
        else -> androidx.compose.material.icons.Icons.AutoMirrored.Filled.InsertDriveFile
    }
}
