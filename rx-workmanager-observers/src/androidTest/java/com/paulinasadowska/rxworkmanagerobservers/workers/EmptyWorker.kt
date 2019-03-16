package com.paulinasadowska.rxworkmanagerobservers.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

internal class EmptyWorker(
        context: Context,
        workerParameters: WorkerParameters
) : Worker(context, workerParameters) {

    override fun doWork(): Result {
        return Result.success()
    }
}