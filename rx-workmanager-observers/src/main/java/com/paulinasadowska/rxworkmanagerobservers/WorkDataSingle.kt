package com.paulinasadowska.rxworkmanagerobservers

import androidx.lifecycle.LiveData
import androidx.work.Data
import androidx.work.WorkInfo
import com.paulinasadowska.rxworkmanagerobservers.base.MainThreadSingle
import com.paulinasadowska.rxworkmanagerobservers.observers.WorkDataObserver
import io.reactivex.SingleObserver

class WorkDataSingle(
        private val liveData: LiveData<WorkInfo>
) : MainThreadSingle<Data>() {

    override fun onSubscribeOnMainThread(observer: SingleObserver<in Data>) {
        observer.subscribeAndStartObserving()
    }

    private fun SingleObserver<in Data>.subscribeAndStartObserving() {
        WorkDataObserver(this, liveData).let {
            onSubscribe(it)
            liveData.observeForever(it)
        }
    }
}

