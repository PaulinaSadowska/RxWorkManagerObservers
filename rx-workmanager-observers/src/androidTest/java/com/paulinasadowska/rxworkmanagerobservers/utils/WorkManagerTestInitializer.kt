package com.paulinasadowska.rxworkmanagerobservers.utils

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.work.Configuration
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper

fun initializeTestWorkManager(){
    val context = ApplicationProvider.getApplicationContext<Context>()
    val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setExecutor(SynchronousExecutor())
            .build()

    WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
}