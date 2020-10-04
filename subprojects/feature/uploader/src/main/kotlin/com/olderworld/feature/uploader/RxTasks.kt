package com.olderworld.feature.uploader

import io.reactivex.rxjava3.core.Flowable

interface RxTasks {
    val updates: Flowable<Set<Task>>
}
