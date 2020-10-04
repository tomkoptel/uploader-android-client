package com.olderworld.app.uploader

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import com.olderworld.feature.uploader.Uploader
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
internal class FileUploaderService : LifecycleService() {
    companion object {
        private const val NOTIFICATION_ID = 0xf11e
    }

    @Inject
    lateinit var uploader: Uploader

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let { action ->
            when (action) {
                BuildConfig.ACTION_UPLOAD -> {
                    val notification = buildNotification()
                    startForeground(NOTIFICATION_ID, notification)
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun buildNotification(): Notification? {
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
            // TODO set back to true when dev is done to prevent the user from swipping it out
            setOngoing(false)

            setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            setSmallIcon(R.mipmap.ic_launcher_round)

            setContentTitle(getString(R.string.uploading_notification_content_title))
            setContentText(getString(R.string.uploading_notification_content_text, 1))
            setContentIntent(activityIntent)

            setProgress(10, 1, false)
        }
        val channelName = getString(R.string.tracking_notification_channel_name)

        val notificationManagerCompat =
            NotificationManagerCompat.from(applicationContext)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                BuildConfig.TRACKING_CHANNEL_ID,
                channelName,
                IMPORTANCE_LOW
            )
            notificationManagerCompat.createNotificationChannel(channel)
        }

        val notification = notificationCompatBuilder.build()
        return notification
    }
}
