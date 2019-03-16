package com.paulinasadowska.rxworkmanagerobservers.utils

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkRequest
import androidx.work.workDataOf
import com.paulinasadowska.rxworkmanagerobservers.workers.EchoWorker

internal fun createEchoRequest(pair: Pair<String, String>? = null, tag: String? = null): WorkRequest {
    return OneTimeWorkRequestBuilder<EchoWorker>().apply {
        pair?.let {
            setInputData(workDataOf(it))
        }

        tag?.let {
            addTag(it)
        }
    }.build()
}