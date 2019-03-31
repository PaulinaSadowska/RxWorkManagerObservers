@file:JvmName("WorkManagerRxHelper")

package com.paulinasadowska.rxworkmanagerobservers.extensions

import androidx.work.WorkManager
import java.util.*

fun WorkManager.getWorkDataByIdSingle(requestId: UUID) = this
        .getWorkInfoByIdLiveData(requestId)
        .toWorkDataSingle()

fun WorkManager.getWorkInfoByIdObservable(requestId: UUID) = this
        .getWorkInfoByIdLiveData(requestId)
        .toWorkInfoObservable()

fun WorkManager.getWorkDatasByTagObservable(
        tag: String,
        ignoreError: Boolean = true
) = this
        .getWorkInfosByTagLiveData(tag)
        .toWorkDatasObservable(ignoreError)

fun WorkManager.getWorkDatasForUniqueWorkObservable(
        name: String,
        ignoreError: Boolean = true
) = this
        .getWorkInfosForUniqueWorkLiveData(name)
        .toWorkDatasObservable(ignoreError)