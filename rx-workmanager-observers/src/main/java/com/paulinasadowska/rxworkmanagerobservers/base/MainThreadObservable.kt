package com.paulinasadowska.rxworkmanagerobservers.base

import com.paulinasadowska.rxworkmanagerobservers.exceptions.LiveDataSubscribedOnWrongThreadException
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposables

abstract class MainThreadObservable<T> : Observable<T>() {

    override fun subscribeActual(observer: Observer<in T>) {
        if (isOnMainThread()) {
            onSubscribeOnMainThread(observer)
        } else {
            observer.subscribeAndCallWrongThreadError()
        }
    }

    abstract fun onSubscribeOnMainThread(observer: Observer<in T>)

    private fun Observer<*>.subscribeAndCallWrongThreadError() {
        onSubscribe(Disposables.empty())
        onError(LiveDataSubscribedOnWrongThreadException())
    }
}