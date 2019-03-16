@file:JvmName("LiveDataRxConverter")
package com.paulinasadowska.rxworkmanagerobservers

import androidx.lifecycle.LiveData
import androidx.work.Data
import androidx.work.WorkInfo
import io.reactivex.Observable
import io.reactivex.Single

fun LiveData<WorkInfo>.toWorkDataSingle(): Single<Data> {
    return WorkDataSingle(this)
}

fun LiveData<List<WorkInfo>>.toWorkInfoListObservable(): Observable<Data> {
    return WorkInfoListObservable(this)
}

fun LiveData<WorkInfo>.toWorkInfoObservable(): Observable<WorkInfo> {
    return WorkInfoObservable(this)
}