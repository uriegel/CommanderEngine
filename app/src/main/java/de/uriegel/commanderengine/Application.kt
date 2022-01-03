package de.uriegel.commanderengine

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager

class Application: Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(CHANNEL_SERVICE_ID,
            "CHANNEL_SERVICE", NotificationManager.IMPORTANCE_DEFAULT)
        channel.description = "Channel for foreground service"
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)
    }

    companion object {
        lateinit var instance: Application
            private set
        const val CHANNEL_SERVICE_ID = "CHANNEL_SERVICE"
    }
}