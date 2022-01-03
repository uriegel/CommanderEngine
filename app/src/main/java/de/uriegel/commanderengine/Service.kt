package de.uriegel.commanderengine

import android.app.Application
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import de.uriegel.commanderengine.Application.Companion.CHANNEL_SERVICE_ID

class Service: Service() {
    override fun onCreate() {
        super.onCreate()

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        notification = NotificationCompat.Builder(this, CHANNEL_SERVICE_ID)
            .setContentTitle(getString(R.string.app_title))
            .setContentText(getString(R.string.service_notification_text))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()

        wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager)
            .run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "EndlessService::lock").apply {
                    acquire(20 * 3600_000.toLong())
                }
            }

        running.value = true

        server.start()
    }

    override fun onDestroy() {
        super.onDestroy()

        wakeLock.let {
            if (it.isHeld) {
                it.release()
            }
        }

        server.stop()

        running.value = false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, notification)
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? = null

    private lateinit var notification: Notification
    private lateinit var wakeLock: PowerManager.WakeLock
    private val server = Server()

    companion object {
        val running = MutableLiveData(false)
    }
}