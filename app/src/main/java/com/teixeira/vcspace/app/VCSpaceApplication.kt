package com.teixeira.vcspace.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.blankj.utilcode.util.ThrowableUtils
import com.downloader.PRDownloader
import com.teixeira.vcspace.activities.CrashActivity
import com.teixeira.vcspace.activities.EditorActivity
import com.teixeira.vcspace.extensions.doIfNull
import com.teixeira.vcspace.providers.GrammarProvider
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver
import org.eclipse.tm4e.core.registry.IThemeSource
import kotlin.system.exitProcess

class VCSpaceApplication : BaseApplication() {

    private var uncaughtException: Thread.UncaughtExceptionHandler? = null
    private val activities = mutableListOf<Activity>()

    companion object {
        private var appInstance: VCSpaceApplication? = null

        init {
            System.loadLibrary("vcspace")
        }

        @JvmStatic
        @Synchronized
        fun getInstance(): VCSpaceApplication {
            doIfNull(appInstance) {
                appInstance = VCSpaceApplication()
            }

            return appInstance!!
        }
    }

    override fun onCreate() {
        uncaughtException = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this::uncaughtException)
        super.onCreate()
        appInstance = this

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                activities.add(activity)
            }

            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {
                activities.removeAt(activities.indexOf(activity))
            }
        })

        PRDownloader.initialize(applicationContext)
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
                ).apply { isDark = name != "quietlight" }
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
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
            )

            uncaughtException?.uncaughtException(thread, th)
            exitProcess(1)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    // for plugin use
    fun getEditorActivity(): EditorActivity? {
        return activities.find { it is EditorActivity } as EditorActivity?
    }
}

internal fun noLocalProvidedFor(name: String): Nothing {
    error("CompositionLocal $name not present")
}
