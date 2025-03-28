package com.teixeira.vcspace.app

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Process
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.PermissionUtils.SimpleCallback
import com.teixeira.vcspace.activities.CrashActivity
import com.teixeira.vcspace.core.EventManager
import com.teixeira.vcspace.extensions.doIfNull
import com.teixeira.vcspace.providers.GrammarProvider
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver
import org.eclipse.tm4e.core.registry.IThemeSource
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.system.exitProcess

class VCSpaceApplication : BaseApplication() {

    companion object {
        const val CRASH_REPORT_NOTIFICATION_CHANNEL_ID = "crash_report_notification_channel_id"
        const val CRASH_REPORT_NOTIFICATION_ID = 1

        private const val TAG = "VCSpaceApplication"

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
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler(CrashHandler())
        appInstance = this
        EventManager.instance.clearListeners()

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

    private fun createNotificationChannel() {
        val name = "Crash Report Channel"
        val descriptionText = "Channel for crash report notifications"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel =
            NotificationChannel(CRASH_REPORT_NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        createNotificationChannel()
    }

    private inner class CrashHandler : Thread.UncaughtExceptionHandler {
        override fun uncaughtException(t: Thread, e: Throwable) {
            saveCrashReport(t.name, e)

            if (Looper.myLooper() != null) {
                val handler = Handler(Looper.myLooper()!!)
                while (true) {
                    runCatching {
                        handler.post {
                            //showCrashNotification(getCrashReportFilePath(t.name))
                            Toast.makeText(
                                applicationContext,
                                "App crash report saved.",
                                Toast.LENGTH_LONG
                            ).show()

                            Process.killProcess(Process.myPid())
                            exitProcess(0)
                        }
                        Looper.loop()
                    }.onFailure { saveCrashReport(t.name, it) }
                }
            }
        }

        private fun collectDeviceInfo(): Map<String, String> {
            val info = mutableMapOf<String, String>()

            runCatching {
                val packageInfo =
                    packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
                if (packageInfo != null) {
                    val versionName = packageInfo.versionName.toString()
                    val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        packageInfo.longVersionCode.toString()
                    } else {
                        @Suppress("DEPRECATION")
                        packageInfo.versionCode.toString()
                    }

                    info["versionName"] = versionName
                    info["versionCode"] = versionCode
                }
            }.onFailure { Log.e(TAG, "Error collecting device info", it) }

            var fields = Build::class.java.declaredFields
            for (field in fields) {
                runCatching {
                    field.isAccessible = true
                    val value = field.get(null)
                    if (value is Array<*> && value.isArrayOf<String>()) {
                        info[field.name] = value.contentToString()
                    } else {
                        info[field.name] = value?.toString() ?: "null"
                    }
                }.onFailure { Log.e(TAG, "Error collecting device info", it) }
            }

            fields = Build.VERSION::class.java.declaredFields
            for (field in fields) {
                runCatching {
                    runCatching {
                        field.isAccessible = true
                        val value = field.get(null)
                        if (value is Array<*> && value.isArrayOf<String>()) {
                            info[field.name] = value.contentToString()
                        } else {
                            info[field.name] = value?.toString() ?: "null"
                        }
                    }.onFailure { Log.e(TAG, "Error collecting device info", it) }
                }
            }

            return info
        }

        private fun saveCrashReport(threadName: String, exeption: Throwable) {
            val report = StringBuilder()
            val timestamp = System.currentTimeMillis()

            report.append("Crash at $timestamp (timestamp) in thread named $threadName\n")
            report.append("Local date and time: ")
                .append(SimpleDateFormat.getDateTimeInstance().format(Date(timestamp)))
                .append("\n")

            val deviceInfo = collectDeviceInfo()
            for ((key, value) in deviceInfo) {
                report.append("$key=$value\n")
            }

            val writer: Writer = StringWriter()
            val printWriter = PrintWriter(writer)
            exeption.printStackTrace(printWriter)
            var cause = exeption.cause
            while (cause != null) {
                cause.printStackTrace(printWriter)
                cause = cause.cause
            }
            printWriter.close()
            report.append("$writer\n")

            runCatching {
                Log.e(TAG, report.toString())

                val fileName = "crash-$timestamp.txt"
                File(PathUtils.getExternalAppFilesPath(), fileName).apply {
                    writeText(report.toString())
                    showCrashNotification(absolutePath)
                    startActivity(
                        Intent(applicationContext, CrashActivity::class.java).apply {
                            putExtra(CrashActivity.KEY_EXTRA_ERROR, readText())
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TOP
                        }
                    )
                }
                Log.i(TAG, "Crash report saved to ${getCrashReportFilePath(fileName)}")
            }.onFailure { Log.e(TAG, "Error saving crash report", it) }
        }

        private fun getCrashReportFilePath(fileName: String): String {
            val file = File(PathUtils.getExternalAppFilesPath(), fileName)
            return file.absolutePath
        }

        private fun showCrashNotification(crashReportFilePath: String) {
            val file = File(crashReportFilePath)
            val uri = FileProvider.getUriForFile(applicationContext, "$packageName.provider", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "text/plain")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val pendingIntent = PendingIntent.getActivity(
                applicationContext,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val builder = NotificationCompat.Builder(
                applicationContext,
                CRASH_REPORT_NOTIFICATION_CHANNEL_ID
            ).setSmallIcon(android.R.drawable.stat_notify_error)
                .setContentTitle("App Crash")
                .setContentText("App crashed, view crash report.")
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("App crashed, click to view crash report file.\nFile saved at: $crashReportFilePath")
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ActivityCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    PermissionUtils.permission(Manifest.permission.POST_NOTIFICATIONS)
                        .callback(object : SimpleCallback {
                            @SuppressLint("MissingPermission")
                            override fun onGranted() {
                                NotificationManagerCompat
                                    .from(applicationContext)
                                    .notify(CRASH_REPORT_NOTIFICATION_ID, builder.build())
                            }

                            override fun onDenied() {
                                Toast.makeText(
                                    applicationContext,
                                    "Notification permission denied.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }).request()
                    return
                }
            }
            NotificationManagerCompat
                .from(applicationContext)
                .notify(CRASH_REPORT_NOTIFICATION_ID, builder.build())
        }
    }
}
