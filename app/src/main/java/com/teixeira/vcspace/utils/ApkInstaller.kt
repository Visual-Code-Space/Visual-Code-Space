package com.teixeira.vcspace.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import com.teixeira.vcspace.file.File

/**
 * Adopted from:
 * [ApkInstaller.java](https://github.com/tyron12233/CodeAssist/blob/main/app/src/main/java/com/tyron/code/util/ApkInstaller.java)
 */
object ApkInstaller {
    fun installApplication(context: Context, file: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(file.uri(context), "application/vnd.android.package-archive")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }
}
