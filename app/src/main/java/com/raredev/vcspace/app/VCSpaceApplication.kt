package com.raredev.vcspace.app

import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.PopupMenu
import com.blankj.utilcode.util.ThrowableUtils
import com.google.android.material.color.DynamicColors
import com.raredev.vcspace.activities.CrashActivity
import com.raredev.vcspace.providers.GrammarProvider
import com.raredev.vcspace.utils.PreferencesUtils
import com.raredev.vcspace.utils.Utils
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver
import org.eclipse.tm4e.core.registry.IThemeSource

class VCSpaceApplication: BaseApplication() {

  private var uncaughtException: Thread.UncaughtExceptionHandler? = null

  override fun onCreate() {
    uncaughtException = Thread.getDefaultUncaughtExceptionHandler()
    Thread.setDefaultUncaughtExceptionHandler(::uncaughtException)
    super.onCreate()

    if (PreferencesUtils.dynamicColors) {
      DynamicColors.applyToActivitiesIfAvailable(this)
    }
    AppCompatDelegate.setDefaultNightMode(PreferencesUtils.appTheme)
    GrammarProvider.initialize(this)
    loadDefaultThemes()
  }

  private fun loadDefaultThemes() {
    FileProviderRegistry.getInstance().dispose()
    FileProviderRegistry.getInstance().addFileProvider(AssetsFileResolver(assets))

    val themes = arrayOf("darcula", "quietlight")
    val themeRegistry = ThemeRegistry.getInstance()
    themes.forEach { name ->
      val path = "editor/sora-editor/schemes/$name.json"
      themeRegistry.loadTheme(
        ThemeModel(
          IThemeSource.fromInputStream(
            FileProviderRegistry.getInstance().tryGetInputStream(path), path, null
          ), name
        ).setDark(name == "darcula")
      )
    }
   // themeRegistry.setTheme(if (Utils.isDarkMode()) "darcula" else "quietlight")
  }

  fun uncaughtException(thread: Thread, th: Throwable) {
    try {
      val intent = Intent(this, CrashActivity::class.java)

      // Add the error message
      intent.putExtra(CrashActivity.KEY_EXTRA_ERROR, ThrowableUtils.getFullStackTrace(th))
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      // Start the crash activity
      startActivity(intent)

      uncaughtException?.uncaughtException(thread, th)
      System.exit(1)
    } catch (e: Throwable) {
      e.printStackTrace()
    }
  }
}
