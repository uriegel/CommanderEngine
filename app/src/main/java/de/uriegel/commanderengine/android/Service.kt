package de.uriegel.commanderengine.android

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import de.uriegel.commanderengine.R
import de.uriegel.commanderengine.android.Application.Companion.CHANNEL_SERVICE_ID
import de.uriegel.commanderengine.startKtorServer
import de.uriegel.commanderengine.stopKtorServer
import de.uriegel.commanderengine.ui.MainActivity
import io.ktor.server.engine.ApplicationEngine

class Service: Service() {
    override fun onCreate() {
        super.onCreate()

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0,
            notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        notification = NotificationCompat.Builder(this, CHANNEL_SERVICE_ID)
            .setContentTitle(getString(R.string.app_title))
            .setContentText(getString(R.string.service_notification_text))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .build()

        wakeLock = (getSystemService(POWER_SERVICE) as PowerManager)
            .run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "EndlessService::lock").apply {
                    acquire(20 * 3600_000.toLong())
                }
            }

        //server.start(this)
        server = startKtorServer(this)
        running.value = true
        pending.value = false
    }

    override fun onDestroy() {
        super.onDestroy()

        wakeLock.let {
            if (it.isHeld) {
                it.release()
            }
        }

        server?.also { stopKtorServer(it) }

        running.value = false
        pending.value = false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, notification)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//            showNotification()
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? = null

    private lateinit var notification: Notification
    private lateinit var wakeLock: PowerManager.WakeLock
    private var server: ApplicationEngine? = null

    companion object {
        val pending = mutableStateOf(false)
        val running = mutableStateOf(false)
    }
}