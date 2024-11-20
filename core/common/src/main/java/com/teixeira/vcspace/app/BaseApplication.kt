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

package com.teixeira.vcspace.app

import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.preference.PreferenceManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

open class BaseApplication : Application() {

  companion object {
    const val REPO_URL = "https://github.com/Visual-Code-Space/Visual-Code-Space"

    private var _instance: BaseApplication? = null
    val instance: BaseApplication
      get() = checkNotNull(_instance) { "Application instance not found" }
  }

  val defaultPrefs: SharedPreferences by lazy {
    PreferenceManager.getDefaultSharedPreferences(this)
  }

  val encryptedPrefs: SharedPreferences by lazy {
    val masterKey = MasterKey.Builder(this)
      .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
      .build()

    EncryptedSharedPreferences.create(
      this,
      "encrypted_prefs",
      masterKey,
      EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
      EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
  }

  override fun onCreate() {
    _instance = this
    super.onCreate()
  }

  fun openProjectRepo() {
    openUrl(REPO_URL)
  }

  fun openUrl(url: String) {
    try {
      startActivity(
        Intent().apply {
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          action = Intent.ACTION_VIEW
          data = Uri.parse(url)
        }
      )
    } catch (th: Throwable) {
      th.printStackTrace()
    }
  }
}
