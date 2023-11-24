package com.raredev.vcspace.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.raredev.vcspace.BuildConfig
import java.io.File

/**
 * Adopted from: [ApkInstaller.java](https://github.com/tyron12233/CodeAssist/blob/main/app/src/main/java/com/tyron/code/util/ApkInstaller.java)
 */
object ApkInstaller {
    fun installApplication(context: Context, file: File?) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uriFromFile(context, file), "application/vnd.android.package-archive")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

    fun uriFromFile(context: Context?, file: File?): Uri {
        return FileProvider.getUriForFile(
            context!!,
            BuildConfig.APPLICATION_ID + ".provider",
            file!!
        )
    }
}