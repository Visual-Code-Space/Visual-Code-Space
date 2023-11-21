package com.raredev.vcspace.providers

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.raredev.vcspace.app.BaseApplication.Companion.getInstance
import com.raredev.vcspace.models.FileIcon
import com.raredev.vcspace.res.R
import com.raredev.vcspace.utils.FileUtil
import java.io.File

/**
 * Class to provide File icons
 *
 * @author Felipe Teixeira
 */
object FileIconProvider {

  private var fileIcons: List<FileIcon> = mutableListOf()

  init {
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

  private fun findFileIconByExtension(extension: String): FileIcon? =
      fileIcons.find { it.fileExtensions.contains(extension) }
}
