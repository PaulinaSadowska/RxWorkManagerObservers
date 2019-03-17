package com.paulinasadowska.rxworkmanagerobservers.observers

import androidx.lifecycle.LiveData
import androidx.work.Data
import androidx.work.WorkInfo
import com.paulinasadowska.rxworkmanagerobservers.observers.base.WorkInfosLiveDataObserver
import io.reactivex.Observer
import java.util.*

internal class WorkDatasObserver(
        private val observer: Observer<in Data>,
        liveData: LiveData<List<WorkInfo>>
) : WorkInfosLiveDataObserver(liveData) {

    private var succeededIds: MutableSet<UUID> = HashSet()
    private var errorIds: MutableSet<UUID> = HashSet()

    override fun onSucceeded(workInfo: WorkInfo, requestsCount: Int) {
        if (!succeededIds.contains(workInfo.id)) {
            observer.onNext(workInfo.outputData)
            succeededIds.add(workInfo.id)
        }
        completeIfShould(requestsCount)
    }

    override fun onCanceled(workInfo: WorkInfo, requestsCount: Int) {
        onError(workInfo.id, requestsCount)
    }

    override fun onFailed(workInfo: WorkInfo, requestsCount: Int) {
        onError(workInfo.id, requestsCount)
    }

    private fun onError(workId: UUID, requestsCount: Int) {
        errorIds.add(workId)
        completeIfShould(requestsCount)
    }

    private fun completeIfShould(requestsCount: Int) {
        if (shouldComplete(requestsCount)) {
            observer.onComplete()
            removeObserver()
        }
    }

    private fun shouldComplete(requestsCount: Int) = succeededIds.size + errorIds.size == requestsCount
}