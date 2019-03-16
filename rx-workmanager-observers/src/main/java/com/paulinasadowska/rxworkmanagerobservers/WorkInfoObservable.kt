package com.paulinasadowska.rxworkmanagerobservers

import androidx.lifecycle.LiveData
import androidx.work.WorkInfo
import com.paulinasadowska.rxworkmanagerobservers.base.MainThreadObservable
import com.paulinasadowska.rxworkmanagerobservers.observers.WorkInfoObserver
import io.reactivex.Observer

class WorkInfoObservable(
        private val liveData: LiveData<WorkInfo>
) : MainThreadObservable<WorkInfo>() {

    override fun onSubscribeOnMainThread(observer: Observer<in WorkInfo>) {
        observer.subscribeAndStartObserving()
    }

    private fun Observer<in WorkInfo>.subscribeAndStartObserving() {
        WorkInfoObserver(this, liveData).let {
            onSubscribe(it)
            liveData.observeForever(it)
        }
    }
}
