package com.olderworld.app.uploader

import androidx.test.core.app.launchActivity
import org.junit.Test

class MainActivityTest {
    @Test
    fun useAppContext() {
        launchActivity<MainActivity>().recreate()
    }
}
