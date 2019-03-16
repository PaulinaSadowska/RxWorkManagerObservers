package com.paulinasadowska.rxworkmanagerobservers

import androidx.lifecycle.LiveData
import androidx.work.Data
import androidx.work.WorkInfo
import com.paulinasadowska.rxworkmanagerobservers.exceptions.LiveDataSubscribedOnWrongThreadException
import com.paulinasadowska.rxworkmanagerobservers.utils.isOnMainThread
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposables

class WorkInfoListObservable(
        private val liveData: LiveData<List<WorkInfo>>
) : Observable<Data>() {
    override fun subscribeActual(observer: Observer<in Data>) {
        if (isOnMainThread()) {
            observer.subscribeAndStartObserving()
        } else {
            observer.subscribeAndWrongThreadError()
        }
    }

    private fun Observer<in Data>.subscribeAndStartObserving() {
        WorkInfoListObserver(this, liveData).let {
            onSubscribe(it)
            liveData.observeForever(it)
        }
    }

    private fun Observer<*>.subscribeAndWrongThreadError() {
        onSubscribe(Disposables.empty())
        onError(LiveDataSubscribedOnWrongThreadException())
    }
}