package com.paulinasadowska.rxworkmanagerobservers

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.testing.WorkManagerTestInitHelper
import androidx.work.workDataOf
import com.paulinasadowska.rxworkmanagerobservers.exceptions.WorkFailedException
import com.paulinasadowska.rxworkmanagerobservers.extensions.getWorkDataByIdSingle
import com.paulinasadowska.rxworkmanagerobservers.extensions.toWorkDataSingle
import com.paulinasadowska.rxworkmanagerobservers.utils.DEFAULT_DELAY
import com.paulinasadowska.rxworkmanagerobservers.utils.createEchoRequest
import com.paulinasadowska.rxworkmanagerobservers.utils.initializeTestWorkManager
import com.paulinasadowska.rxworkmanagerobservers.workers.EchoWorker
import com.paulinasadowska.rxworkmanagerobservers.workers.EchoWorker.Companion.KEY_ECHO_MESSAGE
import com.paulinasadowska.rxworkmanagerobservers.workers.EmptyWorker
import io.reactivex.android.schedulers.AndroidSchedulers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class WorkDataSingleTest {

    companion object {
        private const val EXAMPLE_ECHO_MESSAGE = "some message"
    }

    private val workManager by lazy { WorkManager.getInstance() }

    @Before
    fun setUp() {
        initializeTestWorkManager()
    }

    @Test
    fun someInputData_echoWorker_successWithValueTheSameAsInput() {
        //given
        val request = createEchoRequest(KEY_ECHO_MESSAGE to EXAMPLE_ECHO_MESSAGE)

        //when
        workManager.enqueue(request)
        val workSingle = workManager
                .getWorkDataByIdSingle(request.id)
                .subscribeOn(AndroidSchedulers.mainThread())
                .test()

        sleep(DEFAULT_DELAY)

        //then
        workSingle.assertValue(workDataOf(KEY_ECHO_MESSAGE to EXAMPLE_ECHO_MESSAGE))
    }

    @Test
    fun noInputData_echoWorker_errorWithWorkFailedException() {
        //given
        val request = createEchoRequest()

        //when
        workManager.enqueue(request)
        val workSingle = workManager
                .getWorkDataByIdSingle(request.id)
                .subscribeOn(AndroidSchedulers.mainThread())
                .test()

        sleep(DEFAULT_DELAY)

        //then
        workSingle.assertError(WorkFailedException::class.java)
    }

    @Test
    fun someInputData_echoWorkerWithDelay_successWithValueTheSameAsInputAfterDelayMet() {
        //given
        val request =
                createEchoRequestBuilderWithData(KEY_ECHO_MESSAGE to EXAMPLE_ECHO_MESSAGE)
                        .setInitialDelay(10, TimeUnit.DAYS)
                        .build()

        val testDriver = WorkManagerTestInitHelper.getTestDriver()

        //when
        workManager.enqueue(request)
        val workSingle = workManager
                .getWorkDataByIdSingle(request.id)
                .subscribeOn(AndroidSchedulers.mainThread())
                .test()

        sleep(DEFAULT_DELAY)

        workSingle.assertNoValues()
        testDriver.setInitialDelayMet(request.id)

        sleep(DEFAULT_DELAY)

        //then
        workSingle.assertValue(workDataOf(KEY_ECHO_MESSAGE to EXAMPLE_ECHO_MESSAGE))
    }

    @Test
    fun emptyWorker_successWithEmptyValue() {
        //given
        val request = OneTimeWorkRequest.from(EmptyWorker::class.java)

        //when
        workManager.enqueue(request)
        val workSingle = workManager
                .getWorkDataByIdSingle(request.id)
                .subscribeOn(AndroidSchedulers.mainThread())
                .test()

        sleep(DEFAULT_DELAY)

        //then
        workSingle.assertValue(workDataOf())
    }

    private fun createEchoRequestBuilderWithData(pair: Pair<String, String>): OneTimeWorkRequest.Builder {
        return OneTimeWorkRequestBuilder<EchoWorker>()
                .setInputData(workDataOf(pair))
    }
}