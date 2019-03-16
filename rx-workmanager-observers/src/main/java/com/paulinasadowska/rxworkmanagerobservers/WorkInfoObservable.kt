package com.paulinasadowska.rxworkmanagerobservers

import androidx.lifecycle.LiveData
import androidx.work.WorkInfo
import com.paulinasadowska.rxworkmanagerobservers.exceptions.LiveDataSubscribedOnWrongThreadException
import com.paulinasadowska.rxworkmanagerobservers.observers.WorkInfoObserver
import com.paulinasadowska.rxworkmanagerobservers.utils.isOnMainThread
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposables

class WorkInfoObservable(
        private val liveData: LiveData<WorkInfo>
) : Observable<WorkInfo>() {

    override fun subscribeActual(observer: Observer<in WorkInfo>) {
        if (isOnMainThread()) {
            observer.subscribeAndStartObserving()
        } else {
            observer.subscribeAndWrongThreadError()
        }
    }

    private fun Observer<in WorkInfo>.subscribeAndStartObserving() {
        WorkInfoObserver(this, liveData).let {
            onSubscribe(it)
            liveData.observeForever(it)
        }
    }

    private fun Observer<*>.subscribeAndWrongThreadError() {
        onSubscribe(Disposables.empty())
        onError(LiveDataSubscribedOnWrongThreadException())
    }
}
