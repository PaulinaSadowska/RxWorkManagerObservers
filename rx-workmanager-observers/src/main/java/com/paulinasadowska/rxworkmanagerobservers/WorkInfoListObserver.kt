package com.paulinasadowska.rxworkmanagerobservers

import androidx.lifecycle.LiveData
import androidx.work.Data
import androidx.work.WorkInfo
import io.reactivex.Observer
import io.reactivex.android.MainThreadDisposable
import java.util.*

internal class WorkInfoListObserver(
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