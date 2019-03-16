package com.paulinasadowska.rxworkmanagerobservers

import androidx.lifecycle.LiveData
import androidx.work.Data
import androidx.work.WorkInfo
import com.paulinasadowska.rxworkmanagerobservers.base.MainThreadObservable
import com.paulinasadowska.rxworkmanagerobservers.observers.WorkInfoListObserver
import io.reactivex.Observer

class WorkInfoListObservable(
        private val liveData: LiveData<List<WorkInfo>>
) : MainThreadObservable<Data>() {

    override fun onSubscribeOnMainThread(observer: Observer<in Data>) {
        observer.subscribeAndStartObserving()
    }

    private fun Observer<in Data>.subscribeAndStartObserving() {
        WorkInfoListObserver(this, liveData).let {
            onSubscribe(it)
            liveData.observeForever(it)
        }
    }
}