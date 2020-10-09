package com.olderworld.app.uploader

import android.content.Context
import android.content.Intent
import android.net.Uri
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
        private const val FILE_URIS = "FILE_URIS"

        fun intent(context: Context, fileUris: List<Uri>) =
            Intent(context, FileUploaderService::class.java).let {
                it.action = BuildConfig.ACTION_UPLOAD
                it.putExtra(FILE_URIS, fileUris.toTypedArray())
            }
    }

    @Inject
    lateinit var uploader: Uploader

    @Inject
    lateinit var notificationManager: NotificationManagerCompat

    @Inject
    lateinit var notificationFactory: NotificationFactory

    override fun onCreate() {
        super.onCreate()

        uploader.onStateChange(this) { uploadState ->
            when (uploadState.isFinished) {
                true -> stopForeground(true)
                false -> updateNotification(uploadState)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let { action ->
            when (action) {
                BuildConfig.ACTION_UPLOAD -> {
                    val tasks = intent.fileUris().map { it.asTask() }
                    uploader.enqueue(tasks)

                    val notification = notificationFactory.uploadStatus(tasks.size)
                    startForeground(NOTIFICATION_ID, notification)
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun updateNotification(state: Uploader.State) {
        notificationFactory.uploadStatus(total = state.total, progress = state.progress).let {
            notificationManager.notify(NotificationFactory.UPLOAD_NOTIFICATION_ID, it)
        }
    }

    private fun Intent.fileUris(): List<Uri> = getParcelableArrayExtra(FILE_URIS)
        ?.toList()
        ?.map { it as Uri }
        .orEmpty()
}
