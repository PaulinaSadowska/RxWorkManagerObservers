package com.paulinasadowska.rxworkmanagerobservers.observers

import androidx.lifecycle.LiveData
import androidx.work.WorkInfo

internal abstract class WorkInfoLiveDataListObserver(
        private val liveData: LiveData<List<WorkInfo>>
) : LiveDataObserver<List<WorkInfo>>(liveData) {

    override fun onChangedAndNotDisposed(value: List<WorkInfo>) {
        val size = value.size
        value.forEach {
            when (it.state) {
                WorkInfo.State.SUCCEEDED -> {
                    onSucceeded(it, size)
                }
                WorkInfo.State.FAILED -> {
                    onFailed(it, size)
                }
                WorkInfo.State.CANCELLED -> {
                    onCanceled(it, size)
                }
                else -> {
                    //do nothing
                }
            }
        }
    }

    abstract fun onSucceeded(workInfo: WorkInfo, requestsCount: Int)

    abstract fun onCanceled(workInfo: WorkInfo, requestsCount: Int)

    abstract fun onFailed(workInfo: WorkInfo, requestsCount: Int)
}