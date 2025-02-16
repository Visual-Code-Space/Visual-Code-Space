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

package com.teixeira.vcspace.terminal.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import com.teixeira.vcspace.activities.TerminalActivity
import com.teixeira.vcspace.app.drawables
import com.teixeira.vcspace.extensions.makePluralIf
import com.teixeira.vcspace.terminal.Session
import com.termux.terminal.TerminalSession
import com.termux.terminal.TerminalSessionClient

// https://github.com/Xed-Editor/Xed-Editor/blob/main/core/main/src/main/java/com/rk/xededitor/service/SessionService.kt
class TerminalService : Service() {
    private val sessions = hashMapOf<String, TerminalSession>()
    val sessionList = mutableStateListOf<String>()
    var currentSession = mutableStateOf("main")

    @Suppress("PrivatePropertyName")
    private val ACTION_EXIT by lazy { "com.teixeira.vcspace.action.ACTION_EXIT" }
    private val notificationId = 46536745

    inner class TerminalBinder : Binder() {
        val service
            get() = this@TerminalService

        fun createSession(
            id: String,
            client: TerminalSessionClient,
            activity: TerminalActivity
        ): TerminalSession {
            return Session.createSession(activity, client, id).also {
                sessions[id] = it
                sessionList.add(id)
                updateNotification()
            }
        }

        fun getSession(id: String): TerminalSession? {
            return sessions[id]
        }

        fun terminateSession(id: String) {
            sessions[id]?.finishIfRunning()
            sessions.remove(id)
            sessionList.remove(id)
            if (sessions.isEmpty()) {
                stopSelf()
            } else {
                updateNotification()
            }
        }
    }

    private val binder = TerminalBinder()
    private val notificationManager by lazy { getSystemService(NotificationManager::class.java) }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification = createNotification()
        startForeground(notificationId, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_EXIT -> {
                sessions.forEach { session -> session.value.finishIfRunning() }
                stopSelf()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        sessions.forEach { session -> session.value.finishIfRunning() }
        super.onDestroy()
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, TerminalActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val exitIntent = Intent(this, TerminalService::class.java).apply { action = ACTION_EXIT }
        val exitPendingIntent = PendingIntent.getService(
            this,
            notificationId,
            exitIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Visual Code Space")
            .setContentText(getNotificationContentText())
            .setSmallIcon(drawables.terminal)
            .setContentIntent(pendingIntent)
            .addAction(
                NotificationCompat.Action.Builder(
                    null,
                    "Exit",
                    exitPendingIntent
                ).build()
            )
            .setOngoing(true)
            .build()
    }

    private val CHANNEL_ID = "session_service_channel"

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Session Service",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Notification for Terminal Service"
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun updateNotification() {
        val notification = createNotification()
        notificationManager.notify(notificationId, notification)
    }

    private fun getNotificationContentText(): String {
        val count = sessions.size
        return "$count${" session" makePluralIf (count > 1)} running"
    }
}
