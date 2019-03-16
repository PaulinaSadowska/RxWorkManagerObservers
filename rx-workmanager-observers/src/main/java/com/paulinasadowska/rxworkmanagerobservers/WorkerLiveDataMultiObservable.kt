package com.paulinasadowska.rxworkmanagerobservers

import androidx.lifecycle.LiveData
import androidx.work.Data
import androidx.work.WorkInfo
import com.paulinasadowska.rxworkmanagerobservers.exceptions.LiveDataSubscribedOnWrongThreadException
import com.paulinasadowska.rxworkmanagerobservers.utils.isOnMainThread
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.MainThreadDisposable
import io.reactivex.disposables.Disposables
import java.util.*

fun LiveData<List<WorkInfo>>.toObservable(): Observable<Data> {
    return WorkerLiveDataMultiObservable(this)
}

class WorkerLiveDataMultiObservable(
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
        WorkInfoObserver(this, liveData).let {
            onSubscribe(it)
            liveData.observeForever(it)
        }
    }

    private fun Observer<*>.subscribeAndWrongThreadError() {
        onSubscribe(Disposables.empty())
        onError(LiveDataSubscribedOnWrongThreadException())
    }

    private class WorkInfoObserver(
            private val observer: Observer<in Data>,
            private val liveData: LiveData<List<WorkInfo>>
    ) : MainThreadDisposable(), androidx.lifecycle.Observer<List<WorkInfo>> {

        private var succeededIds: MutableSet<UUID> = HashSet()
        private var errorIds: MutableSet<UUID> = HashSet()

        override fun onChanged(workInfoList: List<WorkInfo>) {
            if (isDisposed) {
                return
            }

            observer.apply {
                workInfoList.forEach {
                    when (it.state) {
                        WorkInfo.State.FAILED,
                        WorkInfo.State.CANCELLED -> {
                            errorIds.add(it.id)
                            if ((succeededIds.size + errorIds.size) == workInfoList.size) {
                                onComplete()
                                removeObserver()
                            }
                        }
                        WorkInfo.State.SUCCEEDED -> {
                            if (!succeededIds.contains(it.id)) {
                                onNext(it.outputData)
                                succeededIds.add(it.id)
                            }

                            if ((succeededIds.size + errorIds.size) == workInfoList.size) {
                                onComplete()
                                removeObserver()
                            }
                        }
                        else -> {
                            // wait
                        }
                    }
                }
            }

        }

        override fun onDispose() {
            removeObserver()
        }

        private fun removeObserver() {
            liveData.removeObserver(this)
        }
    }
}