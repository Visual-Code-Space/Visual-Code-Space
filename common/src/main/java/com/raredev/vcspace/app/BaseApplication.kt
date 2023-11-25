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

package com.raredev.vcspace.app

import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.preference.PreferenceManager

open class BaseApplication : Application() {

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
      intent.action = Intent.ACTION_VIEW
      intent.data = Uri.parse(url)
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      startActivity(intent)
    } catch (th: Throwable) {
      th.printStackTrace()
    }
  }
}
