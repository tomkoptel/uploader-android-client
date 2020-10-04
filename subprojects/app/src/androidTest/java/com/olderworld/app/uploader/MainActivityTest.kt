package com.olderworld.app.uploader

import androidx.test.core.app.launchActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class MainActivityTest {
    @get:Rule
    val hiltAndroidRule = HiltAndroidRule(this)

    @Test
    fun useAppContext() {
        launchActivity<MainActivity>().recreate()
    }
}
