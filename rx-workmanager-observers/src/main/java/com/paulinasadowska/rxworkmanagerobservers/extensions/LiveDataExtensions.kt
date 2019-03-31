@file:JvmName("LiveDataRxConverter")
package com.paulinasadowska.rxworkmanagerobservers.extensions

import androidx.lifecycle.LiveData
import androidx.work.Data
import androidx.work.WorkInfo
import com.paulinasadowska.rxworkmanagerobservers.WorkDataSingle
import com.paulinasadowska.rxworkmanagerobservers.WorkDatasObservable
import com.paulinasadowska.rxworkmanagerobservers.WorkInfoObservable
import io.reactivex.Observable
import io.reactivex.Single

fun LiveData<WorkInfo>.toWorkDataSingle(): Single<Data> {
    return WorkDataSingle(this)
}

fun LiveData<List<WorkInfo>>.toWorkDatasObservable(
        ignoreError: Boolean = true
): Observable<Data> {
    return WorkDatasObservable(this, ignoreError)
}

fun LiveData<WorkInfo>.toWorkInfoObservable(): Observable<WorkInfo> {
    return WorkInfoObservable(this)
}