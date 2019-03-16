package com.paulinasadowska.rxworkmanagerobservers.observers

import androidx.lifecycle.LiveData
import androidx.work.Data
import androidx.work.WorkInfo
import com.paulinasadowska.rxworkmanagerobservers.exceptions.WorkCancelledException
import com.paulinasadowska.rxworkmanagerobservers.exceptions.WorkFailedException
import io.reactivex.SingleObserver

internal class WorkDataObserver(
        private val observer: SingleObserver<in Data>,
        liveData: LiveData<WorkInfo>
) : WorkInfoLiveDataObserver(liveData) {

    override fun onSucceeded(workInfo: WorkInfo) {
        observer.onSuccess(workInfo.outputData)
    }

    override fun onCanceled(workInfo: WorkInfo) {
        observer.onError(WorkCancelledException(workInfo.id))
    }

    override fun onFailed(workInfo: WorkInfo) {
        observer.onError(WorkFailedException(workInfo.id))
    }
}