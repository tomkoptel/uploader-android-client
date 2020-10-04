package com.olderworld.app.uploader

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Intent
import android.content.collectUris
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import com.olderworld.feature.uploader.Uploader
import com.olderworld.feature.uploader.asTask
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
internal class FileUploaderService : LifecycleService() {
    companion object {
        private const val NOTIFICATION_ID = 0xf11e
    }

    @Inject
    lateinit var uploaderLifecycleObserver: UploaderLifecycleObserver
    lateinit var notificationManagerCompat: NotificationManagerCompat

    override fun onCreate() {
        super.onCreate()
        notificationManagerCompat = NotificationManagerCompat.from(applicationContext)

        lifecycle.addObserver(uploaderLifecycleObserver)
        uploaderLifecycleObserver.state.observe(this) { upload ->
            when (upload.isFinished) {
                true -> stopForeground(true)
                false -> updateNotification(upload)
            }
        }
    }

    private fun updateNotification(state: Uploader.State) {
        buildNotification(state.progress, state.total).let {
            notificationManagerCompat.notify(NOTIFICATION_ID, it)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let { action ->
            when (action) {
                BuildConfig.ACTION_UPLOAD -> {
                    val tasks = intent.collectUris().map { it.asTask() }
                    uploaderLifecycleObserver.uploader.enqueue(tasks)

                    val notification = buildNotification(0, tasks.size)
                    startForeground(NOTIFICATION_ID, notification)
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun buildNotification(progress: Int, total: Int): Notification {
        val activityIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            FLAG_UPDATE_CURRENT
        )
        // Retrieves NotificationCompat.Builder used to create initial Notification
        val notificationCompatBuilder = NotificationCompat.Builder(
            applicationContext,
            BuildConfig.TRACKING_CHANNEL_ID
        ).apply {
            setAutoCancel(false)
            setOngoing(true)

            setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            setSmallIcon(R.mipmap.ic_launcher_round)

            setContentTitle(getString(R.string.uploading_notification_content_title))
            setContentText(getString(R.string.uploading_notification_content_text, total))
            setContentIntent(activityIntent)

            setProgress(100, progress, false)
        }
        val channelName = getString(R.string.tracking_notification_channel_name)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                BuildConfig.TRACKING_CHANNEL_ID,
                channelName,
                IMPORTANCE_LOW
            )
            notificationManagerCompat.createNotificationChannel(channel)
        }

        return notificationCompatBuilder.build()
    }
}
