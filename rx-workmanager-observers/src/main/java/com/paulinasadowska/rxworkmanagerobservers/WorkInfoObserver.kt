package com.paulinasadowska.rxworkmanagerobservers

import androidx.lifecycle.LiveData
import androidx.work.WorkInfo
import com.paulinasadowska.rxworkmanagerobservers.exceptions.WorkCancelledException
import com.paulinasadowska.rxworkmanagerobservers.exceptions.WorkFailedException
import io.reactivex.Observer
import io.reactivex.android.MainThreadDisposable

internal class WorkInfoObserver(
        private val observer: Observer<in WorkInfo>,
        private val liveData: LiveData<WorkInfo>
) : MainThreadDisposable(), androidx.lifecycle.Observer<WorkInfo> {

    override fun onChanged(workInfo: WorkInfo) {
        if (isDisposed) {
            return
        }

        observer.apply {
            when (workInfo.state) {
                WorkInfo.State.FAILED -> {
                    onError(WorkFailedException(workInfo.id))
                    removeObserver()
                }
                WorkInfo.State.CANCELLED -> {
                    onError(WorkCancelledException(workInfo.id))
                    removeObserver()
                }
                WorkInfo.State.SUCCEEDED -> {
                    onNext(workInfo)
                    onComplete()
                    removeObserver()
                }
                else -> onNext(workInfo)
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