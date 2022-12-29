package de.uriegel.commanderengine.android

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class Application: Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_SERVICE_ID,
                "CHANNEL_SERVICE", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "Channel for foreground service"
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_SERVICE_ID = "CHANNEL_SERVICE"
    }
}