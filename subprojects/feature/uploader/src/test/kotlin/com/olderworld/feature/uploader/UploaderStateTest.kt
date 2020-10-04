package com.olderworld.feature.uploader

import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

internal class UploaderStateTest {
    @Test
    fun `if no tasks then progress is 0`() {
        Uploader.State(
            pending = 0,
            sending = 0,
            terminated = 0,
            completed = 0
        ).progress shouldBeEqualTo 0
    }

    @Test
    fun `if not terminated or completed then no progress`() {
        Uploader.State(
            pending = 0,
            sending = 5,
            terminated = 0,
            completed = 0
        ).progress shouldBeEqualTo 0
    }

    @Test
    fun `if completed at least 5 out of 10 then progress 50`() {
        Uploader.State(
            pending = 0,
            sending = 5,
            terminated = 0,
            completed = 5
        ).progress shouldBeEqualTo 50
    }

    @Test
    fun `if terminated at least 5 out of 10 then progress 50`() {
        Uploader.State(
            pending = 0,
            sending = 5,
            terminated = 5,
            completed = 0
        ).progress shouldBeEqualTo 50
    }
}
