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

fun WorkManager.getWorkDatasByTagObservable(tag: String) = this
        .getWorkInfosByTagLiveData(tag)
        .toWorkDatasObservable()

fun WorkManager.getWorkDatasForUniqueWorkObservable(name: String) = this
        .getWorkInfosForUniqueWorkLiveData(name)
        .toWorkDatasObservable()