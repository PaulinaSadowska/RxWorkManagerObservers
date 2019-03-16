package com.paulinasadowska.rxworkmanagerobservers

import androidx.lifecycle.LiveData
import androidx.work.Data
import androidx.work.WorkInfo
import com.paulinasadowska.rxworkmanagerobservers.exceptions.LiveDataSubscribedOnWrongThreadException
import com.paulinasadowska.rxworkmanagerobservers.observers.WorkDataObserver
import com.paulinasadowska.rxworkmanagerobservers.utils.isOnMainThread
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposables

class WorkDataSingle(
        private val liveData: LiveData<WorkInfo>
) : Single<Data>() {

    override fun subscribeActual(observer: SingleObserver<in Data>) {
        if (isOnMainThread()) {
            observer.subscribeAndStartObserving()
        } else {
            observer.subscribeAndWrongThreadError()
        }
    }

    private fun SingleObserver<in Data>.subscribeAndStartObserving() {
        WorkDataObserver(this, liveData).let {
            onSubscribe(it)
            liveData.observeForever(it)
        }
    }

    private fun SingleObserver<*>.subscribeAndWrongThreadError() {
        onSubscribe(Disposables.empty())
        onError(LiveDataSubscribedOnWrongThreadException())
    }
}

