package com.teixeira.vcspace.app

import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import com.blankj.utilcode.util.ThrowableUtils
import com.google.android.material.color.DynamicColors
import com.teixeira.vcspace.activities.CrashActivity
import com.teixeira.vcspace.preferences.aparenceMaterialYou
import com.teixeira.vcspace.preferences.aparenceUIMode
import com.teixeira.vcspace.providers.GrammarProvider
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver
import kotlin.system.exitProcess
import org.eclipse.tm4e.core.registry.IThemeSource

class VCSpaceApplication : BaseApplication() {

  private var uncaughtException: Thread.UncaughtExceptionHandler? = null

  override fun onCreate() {
    uncaughtException = Thread.getDefaultUncaughtExceptionHandler()
    Thread.setDefaultUncaughtExceptionHandler(this::uncaughtException)
    super.onCreate()

    AppCompatDelegate.setDefaultNightMode(aparenceUIMode)
    if (aparenceMaterialYou) {
      DynamicColors.applyToActivitiesIfAvailable(this)
    }
    GrammarProvider.initialize(this)
    loadDefaultThemes()
  }

  private fun loadDefaultThemes() {
    FileProviderRegistry.getInstance().dispose()
    FileProviderRegistry.getInstance().addFileProvider(AssetsFileResolver(assets))

    val themes = arrayOf("darcula", "quietlight", "abyss", "solarized_drak")
    val themeRegistry = ThemeRegistry.getInstance()
    themes.forEach { name ->
      val path = "editor/schemes/$name.json"
      themeRegistry.loadTheme(
        ThemeModel(
            IThemeSource.fromInputStream(
              FileProviderRegistry.getInstance().tryGetInputStream(path),
              path,
              null,
            ),
            name,
          )
          .apply { setDark(name != "quietlight") }
      )
    }
  }

  private fun uncaughtException(thread: Thread, th: Throwable) {
    try {
      // Start the crash activity
      startActivity(
        Intent(this, CrashActivity::class.java).apply {
          // Add the error message
          putExtra(CrashActivity.KEY_EXTRA_ERROR, ThrowableUtils.getFullStackTrace(th))
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
      )

      uncaughtException?.uncaughtException(thread, th)
      exitProcess(1)
    } catch (e: Throwable) {
      e.printStackTrace()
    }
  }
}
