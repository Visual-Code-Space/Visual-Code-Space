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
import com.teixeira.vcspace.extensions.toFile
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
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val children: List<FileTreeNode> = emptyList()
)

data class FileTreeNodeLoadingProgress(
    val totalFiles: Int = 0,
    val loadedFiles: Int = 0
) {
    val percentComplete: Int
        get() = if (totalFiles > 0) {
            ((loadedFiles.toFloat() / totalFiles) * 100).toInt().coerceIn(0, 100)
        } else 0
}

suspend fun createFileTreeFromPath(
    path: String,
    progressCallback: (FileTreeNodeLoadingProgress) -> Unit = {}
): FileTreeNode {
    val progress = FileTreeNodeLoadingProgress()
    val progressMutex = Mutex()

    suspend fun updateProgress(update: (FileTreeNodeLoadingProgress) -> FileTreeNodeLoadingProgress) {
        progressMutex.withLock {
            val updatedProgress = update(progress)
            progressCallback(updatedProgress)
        }
    }

    suspend fun buildTree(filePath: String, isRootCall: Boolean = false): FileTreeNode {
        val file = java.io.File(filePath)
        val name = file.name
        val isDirectory = file.isDirectory

        return withContext(Dispatchers.IO) {
            if (isDirectory) {
                val childFiles = file.listFiles()
                    // ?.filter { !it.isHidden }
                    ?.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))
                    ?: emptyList()

                if (isRootCall) {
                    val totalCount = countFilesRecursively(file)
                    updateProgress { it.copy(totalFiles = totalCount) }
                }

                val children = childFiles.map { childFile ->
                    val node = buildTree(childFile.absolutePath)
                    updateProgress { it.copy(loadedFiles = it.loadedFiles + 1) }
                    node
                }

                FileTreeNode(name, filePath, true, children)
            } else {
                if (isRootCall) {
                    updateProgress { it.copy(totalFiles = 1, loadedFiles = 1) }
                }

                FileTreeNode(name, filePath, false)
            }
        }
    }

    return buildTree(path, true)
}

/**
 * Helper function to count all files and directories recursively
 */
private fun countFilesRecursively(directory: java.io.File): Int {
    if (!directory.isDirectory) return 1

    var count = 1 // Count this directory
    directory.listFiles()
        ?.filter { !it.isHidden }
        ?.forEach { file ->
            count += countFilesRecursively(file)
        }

    return count
}

@SuppressLint("MaterialDesignInsteadOrbitDesign")
fun getIconForFile(file: FileTreeNode): ImageVector {
    return when (file.path.toFile().extension) {
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
