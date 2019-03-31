package com.paulinasadowska.rxworkmanagerobservers.observers

import androidx.lifecycle.LiveData
import androidx.work.Data
import androidx.work.WorkInfo
import com.paulinasadowska.rxworkmanagerobservers.exceptions.WorkCancelledException
import com.paulinasadowska.rxworkmanagerobservers.exceptions.WorkException
import com.paulinasadowska.rxworkmanagerobservers.exceptions.WorkFailedException
import com.paulinasadowska.rxworkmanagerobservers.observers.base.WorkInfosLiveDataObserver
import io.reactivex.Observer
import java.util.*

internal class WorkDatasObserver(
        private val observer: Observer<in Data>,
        liveData: LiveData<List<WorkInfo>>,
        private val ignoreError: Boolean
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
        onError(workInfo.id, requestsCount, WorkCancelledException(workInfo.id))
    }

    override fun onFailed(workInfo: WorkInfo, requestsCount: Int) {
        onError(workInfo.id, requestsCount, WorkFailedException(workInfo.id))
    }

    private fun onError(workId: UUID, requestsCount: Int, cause: WorkException) {
        if (ignoreError) {
            errorIds.add(workId)
            completeIfShould(requestsCount)
        } else {
            observer.onError(cause)
            removeObserver()
        }
    }

    private fun completeIfShould(requestsCount: Int) {
        if (shouldComplete(requestsCount)) {
            observer.onComplete()
            removeObserver()
        }
    }

    private fun shouldComplete(requestsCount: Int) =
            succeededIds.size + errorIds.size == requestsCount
}