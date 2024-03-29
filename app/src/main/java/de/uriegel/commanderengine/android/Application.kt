package de.uriegel.commanderengine.android

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import de.uriegel.commanderengine.R

class Application: Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_SERVICE_ID,
                getString(R.string.CHANNEL_SERVICE), NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = getString(R.string.CHANNEL_SERVICE_DESCRIPTION)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_SERVICE_ID = "CHANNEL_SERVICE"
    }
}