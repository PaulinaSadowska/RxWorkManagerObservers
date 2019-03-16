package com.paulinasadowska.rxworkmanagerobservers.observers

import androidx.lifecycle.LiveData
import androidx.work.WorkInfo
import com.paulinasadowska.rxworkmanagerobservers.exceptions.WorkCancelledException
import com.paulinasadowska.rxworkmanagerobservers.exceptions.WorkFailedException
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
        observer.onError(WorkCancelledException(workInfo.id))
    }

    override fun onFailed(workInfo: WorkInfo) {
        observer.onError(WorkFailedException(workInfo.id))
    }
}