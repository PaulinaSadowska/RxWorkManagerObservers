package com.paulinasadowska.rxworkmanagerobservers.observers

import androidx.lifecycle.LiveData
import androidx.work.WorkInfo

internal abstract class WorkInfoLiveDataObserver(
        liveData: LiveData<WorkInfo>
) : LiveDataObserver<WorkInfo>(liveData) {

    override fun onChangedAndNotDisposed(value: WorkInfo) {
        when (value.state) {
            WorkInfo.State.SUCCEEDED -> {
                onSucceeded(value)
                removeObserver()
            }
            WorkInfo.State.FAILED -> {
                onFailed(value)
                removeObserver()
            }
            WorkInfo.State.CANCELLED -> {
                onCanceled(value)
                removeObserver()
            }
            else -> {
                onOtherState(value)
            }
        }
    }

    abstract fun onSucceeded(workInfo: WorkInfo)

    abstract fun onCanceled(workInfo: WorkInfo)

    abstract fun onFailed(workInfo: WorkInfo)

    protected open fun onOtherState(value: WorkInfo) {
        //do nothing
    }
}