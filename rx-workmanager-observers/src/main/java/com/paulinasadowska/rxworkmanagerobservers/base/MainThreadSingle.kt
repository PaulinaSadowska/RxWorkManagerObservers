package com.paulinasadowska.rxworkmanagerobservers.base

import com.paulinasadowska.rxworkmanagerobservers.exceptions.LiveDataSubscribedOnWrongThreadException
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposables

abstract class MainThreadSingle<T> : Single<T>() {

    override fun subscribeActual(observer: SingleObserver<in T>) {
        if (isOnMainThread()) {
            onSubscribeOnMainThread(observer)
        } else {
            observer.subscribeAndCallWrongThreadError()
        }
    }

    abstract fun onSubscribeOnMainThread(observer: SingleObserver<in T>)

    private fun SingleObserver<*>.subscribeAndCallWrongThreadError() {
        onSubscribe(Disposables.empty())
        onError(LiveDataSubscribedOnWrongThreadException())
    }
}