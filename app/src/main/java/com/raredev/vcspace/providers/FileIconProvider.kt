package com.raredev.vcspace.providers

import android.graphics.drawable.Drawable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.raredev.vcspace.app.BaseApplication.Companion.getInstance
import com.raredev.vcspace.models.FileIcon
import com.raredev.vcspace.res.R
import com.raredev.vcspace.utils.FileUtil
import com.raredev.vcspace.utils.Utils
import java.io.File

/**
 * Class to provide File icons
 *
 * @author Felipe Teixeira
 */
object FileIconProvider {

  private var fileIcons: List<FileIcon> = mutableListOf()

  fun initialize() {
    if (fileIcons.isNotEmpty()) {
      return
    }

    val fileIconsJson = FileUtil.readFromAsset(getInstance(), "files/file_icons.json")

    fileIcons = Gson().fromJson(fileIconsJson, object : TypeToken<List<FileIcon>>() {})
  }

  fun findFileIconResource(file: File): Int {
    val fileIcon = findFileIconByExtension(file.extension)
    if (fileIcon == null) {
      return R.drawable.ic_file
    }
    val resId =
        getInstance()
            .resources
            .getIdentifier(fileIcon.drawableName, "drawable", getInstance().packageName)
    return if (resId == 0) R.drawable.ic_file else resId
  }

  fun findFileIconDrawable(file: File): Drawable? {
    val extension = file.extension
    val fileTypes =
        mapOf(
            "cs" to "csharp",
            "md" to "markdown",
            "py" to "python",
            "kt" to "kotlin",
            "glsl" to "shader",
            "php" to "php2",
            "kts" to "gradlekts",
            "sh" to "shell", // or "shell2"
            "gitmodules" to "gitignore",
            "keystore" to "key"
            // ...
            )

    val qualifiedName = fileTypes[extension] ?: extension
    return Utils.getDrawableFromSvg("icons/files/$qualifiedName.svg")
  }

  fun findFolderIconDrawable(file: File): Drawable? {
    val name = file.name.lowercase();
    val folderName = if (name.startsWith(".")) name.replaceFirst(".", "") else name
    return Utils.getDrawableFromSvg("icons/folders/$folderName.svg")
  }

  private fun findFileIconByExtension(extension: String): FileIcon? =
      fileIcons.find { it.fileExtensions.contains(extension) }
}
