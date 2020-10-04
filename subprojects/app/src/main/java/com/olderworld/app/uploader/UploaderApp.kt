package com.olderworld.app.uploader

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
internal class UploaderApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Config.onCreate()
    }

    private object SilentTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) = Unit
    }

    /**
     * Expose config for instrumentation tests.
     */
    object Config {
        fun onCreate() {
            if (BuildConfig.DEBUG) {
                Timber.tag(UploaderApp::class.java.simpleName)
                Timber.plant(Timber.DebugTree())
            } else {
                Timber.plant(SilentTree)
            }
        }
    }
}
