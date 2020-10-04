package com.olderworld.feature.uploader

import timber.log.Timber
import java.util.logging.Level
import java.util.logging.Logger

internal class TimberTestTree : Timber.Tree() {
    private val logger = Logger.getGlobal()

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (t == null) {
            logger.log(Level.INFO, "msg=$message")
        } else {
            logger.log(Level.SEVERE, "msg=$message", t)
        }
    }
}
