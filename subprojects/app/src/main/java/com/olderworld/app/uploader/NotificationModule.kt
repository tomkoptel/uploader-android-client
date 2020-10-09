package com.olderworld.app.uploader

import android.app.Application
import androidx.core.app.NotificationManagerCompat
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent

@dagger.Module
@InstallIn(ServiceComponent::class)
internal class NotificationModule {
    @Provides
    fun notificationManagerCompat(application: Application) =
        NotificationManagerCompat.from(application)
}
