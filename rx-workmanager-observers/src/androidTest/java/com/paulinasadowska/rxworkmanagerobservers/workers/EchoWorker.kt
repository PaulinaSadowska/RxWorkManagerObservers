package com.paulinasadowska.rxworkmanagerobservers.workers

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters

internal class EchoWorker(context: Context, workerParameters: WorkerParameters)
    : Worker(context, workerParameters) {

    internal companion object {
        internal const val KEY_ECHO_MESSAGE = "echoMessage"
    }

    override fun doWork(): Result {
        return when (inputData.getString(KEY_ECHO_MESSAGE)) {
            null -> ListenableWorker.Result.failure()
            else -> ListenableWorker.Result.success(inputData)
        }
    }
}