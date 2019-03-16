package com.paulinasadowska.rxworkmanagerobservers.observers

import androidx.lifecycle.LiveData
import androidx.work.WorkInfo
import com.paulinasadowska.rxworkmanagerobservers.exceptions.WorkException
import com.paulinasadowska.rxworkmanagerobservers.exceptions.WorkFailedException
import com.paulinasadowska.rxworkmanagerobservers.observers.base.WorkInfoLiveDataObserver
import io.reactivex.Observer

internal class WorkInfoObserver(
        private val observer: Observer<in WorkInfo>,
        liveData: LiveData<WorkInfo>
) : WorkInfoLiveDataObserver(liveData) {

    override fun onSucceeded(workInfo: WorkInfo) {
        observer.apply {
            onNext(workInfo)
            onComplete()
        }
    }

    override fun onOtherState(value: WorkInfo) {
        observer.onNext(value)
    }

    override fun onCanceled(workInfo: WorkInfo) {
        onNextThenError(workInfo, WorkFailedException(workInfo.id))
    }

    override fun onFailed(workInfo: WorkInfo) {
        onNextThenError(workInfo, WorkFailedException(workInfo.id))
    }

    private fun onNextThenError(workInfo: WorkInfo, exception: WorkException) {
        observer.apply {
            onNext(workInfo)
            onError(exception)
        }
    }
}