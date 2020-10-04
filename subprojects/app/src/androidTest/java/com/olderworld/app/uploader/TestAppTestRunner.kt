@file:Suppress("DEPRECATION")

package com.olderworld.app.uploader

import android.app.Application
import android.content.Context
import android.os.AsyncTask
import androidx.test.runner.AndroidJUnitRunner
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.Schedulers

class TestAppTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application {
        return super.newApplication(cl, TestApp_Application::class.java.name, context)
    }

    override fun onStart() {
        overrideRxSchedulers()
        super.onStart()
    }

    private fun overrideRxSchedulers() {
        val serialExecutor = Schedulers.from(AsyncTask.SERIAL_EXECUTOR)
        val threadPoolExecutor = Schedulers.from(AsyncTask.THREAD_POOL_EXECUTOR)
        RxJavaPlugins.setInitComputationSchedulerHandler { threadPoolExecutor }
        RxJavaPlugins.setInitIoSchedulerHandler { serialExecutor }
    }
}
