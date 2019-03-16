package com.paulinasadowska.rxworkmanagerobservers

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.work.Data
import androidx.work.WorkInfo
import com.paulinasadowska.rxworkmanagerobservers.exceptions.LiveDataSubscribedOnWrongThreadException
import com.paulinasadowska.rxworkmanagerobservers.exceptions.WorkCancelledException
import com.paulinasadowska.rxworkmanagerobservers.exceptions.WorkFailedException
import com.paulinasadowska.rxworkmanagerobservers.utils.isOnMainThread
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.MainThreadDisposable
import io.reactivex.disposables.Disposables

fun LiveData<WorkInfo>.toSingle(): Single<Data> {
    return WorkerLiveDataSingle(this)
}

class WorkerLiveDataSingle(
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
        WorkInfoObserver(this, liveData).let {
            onSubscribe(it)
            liveData.observeForever(it)
        }
    }

    private fun SingleObserver<*>.subscribeAndWrongThreadError() {
        onSubscribe(Disposables.empty())
        onError(LiveDataSubscribedOnWrongThreadException())
    }

    private class WorkInfoObserver(
            private val observer: SingleObserver<in Data>,
            private val liveData: LiveData<WorkInfo>
    ) : MainThreadDisposable(), Observer<WorkInfo> {

        override fun onChanged(workInfo: WorkInfo) {
            if (isDisposed) {
                return
            }

            observer.apply {
                when (workInfo.state) {
                    WorkInfo.State.SUCCEEDED -> {
                        onSuccess(workInfo.outputData)
                        removeObserver()
                    }
                    WorkInfo.State.FAILED -> {
                        onError(WorkFailedException(workInfo.id))
                        removeObserver()
                    }
                    WorkInfo.State.CANCELLED -> {
                        onError(WorkCancelledException(workInfo.id))
                        removeObserver()
                    }
                    else -> {
                        // wait
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

