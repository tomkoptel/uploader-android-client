package com.olderworld.app.uploader

import android.app.Application
import timber.log.Timber

internal class UploaderApp : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.tag(UploaderApp::class.java.simpleName)
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(SilentTree)
        }
    }

    private object SilentTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) = Unit
    }
}
