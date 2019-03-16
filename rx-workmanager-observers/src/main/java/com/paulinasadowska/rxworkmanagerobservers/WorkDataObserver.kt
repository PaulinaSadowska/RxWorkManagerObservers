package com.paulinasadowska.rxworkmanagerobservers

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.work.Data
import androidx.work.WorkInfo
import com.paulinasadowska.rxworkmanagerobservers.exceptions.WorkCancelledException
import com.paulinasadowska.rxworkmanagerobservers.exceptions.WorkFailedException
import io.reactivex.SingleObserver
import io.reactivex.android.MainThreadDisposable

internal class WorkDataObserver(
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