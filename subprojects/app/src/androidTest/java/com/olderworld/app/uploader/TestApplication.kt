package com.olderworld.app.uploader

import androidx.multidex.MultiDexApplication

open class TestApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        UploaderApp.Config.onCreate()
    }
}
