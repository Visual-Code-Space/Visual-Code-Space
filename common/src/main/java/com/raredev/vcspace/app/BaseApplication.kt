package com.raredev.vcspace.app

import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.preference.PreferenceManager
import com.raredev.vcspace.utils.Environment

open class BaseApplication: Application() {

  companion object {
    const val REPO_URL = "https://github.com/Visual-Code-Space/Visual-Code-Space"

    private lateinit var instance: BaseApplication

    fun getInstance(): BaseApplication {
      return instance
    }
  }

  private lateinit var prefs: SharedPreferences

  override fun onCreate() {
    instance = this
    super.onCreate()

    prefs = PreferenceManager.getDefaultSharedPreferences(this)
    Environment.init()
  }

  fun getPrefs(): SharedPreferences {
    return prefs
  }

  fun openProjectRepo() {
    openUrl(REPO_URL)
  }

  fun openUrl(url: String) {
    try {
      val intent = Intent()
      intent.setAction(Intent.ACTION_VIEW)
      intent.setData(Uri.parse(url))
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      startActivity(intent)
    } catch (th: Throwable) {
      th.printStackTrace()
    }
  }
}
