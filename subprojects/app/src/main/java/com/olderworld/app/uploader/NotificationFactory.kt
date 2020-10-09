package com.olderworld.app.uploader

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.scopes.ServiceScoped
import javax.inject.Inject

@ServiceScoped
internal class NotificationFactory @Inject constructor(
    private val service: Service,
    private val notificationManager: NotificationManagerCompat
) {
    fun uploadStatus(total: Int, progress: Int = 0): Notification {
        val activityIntent = PendingIntent.getActivity(
            service,
            0,
            Intent(service, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notificationCompatBuilder = NotificationCompat.Builder(
            service.applicationContext,
            BuildConfig.TRACKING_CHANNEL_ID
        ).apply {
            setAutoCancel(false)
            setOngoing(true)

            setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            setSmallIcon(R.mipmap.ic_launcher_round)

            setContentTitle(service.getString(R.string.uploading_notification_content_title))
            setContentText(service.getString(R.string.uploading_notification_content_text, total))
            setContentIntent(activityIntent)

            setProgress(100, progress, false)
        }
        val channelName = service.getString(R.string.tracking_notification_channel_name)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                BuildConfig.TRACKING_CHANNEL_ID,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        return notificationCompatBuilder.build()
    }
}
