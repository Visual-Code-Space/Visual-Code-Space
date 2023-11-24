package com.raredev.vcspace.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.raredev.vcspace.app.BaseApplication.Companion.getInstance

object Utils {
  fun setDrawableTint(drawable: Drawable, color: Int) {
    drawable.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
  }

  fun setActivityTitle(activity: Activity, title: String?) {
    if (activity is AppCompatActivity) {
      activity.supportActionBar!!.title = title
    } else {
      activity.actionBar!!.title = title
    }
  }

  fun isPermissionGaranted(context: Context?): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      Environment.isExternalStorageManager()
    } else {
      (ContextCompat.checkSelfPermission(
        context!!,
        Manifest.permission.READ_EXTERNAL_STORAGE
      )
          == PackageManager.PERMISSION_GRANTED)
    }
  }

  val isDarkMode: Boolean
    get() {
      if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) return true else if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) return false
      val uiMode = (getInstance().resources.configuration.uiMode
          and Configuration.UI_MODE_NIGHT_MASK)
      return uiMode == Configuration.UI_MODE_NIGHT_YES
    }
}