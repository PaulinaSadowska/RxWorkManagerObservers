package com.paulinasadowska.rxworkmanagerobservers

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import com.paulinasadowska.rxworkmanagerobservers.utils.initializeTestWorkManager
import com.paulinasadowska.rxworkmanagerobservers.workers.EchoWorker
import com.paulinasadowska.rxworkmanagerobservers.workers.EchoWorker.Companion.KEY_ECHO_MESSAGE
import io.reactivex.android.schedulers.AndroidSchedulers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep

@RunWith(AndroidJUnit4::class)
class WorkerLiveDataTagSingleTest {

    companion object {
        private const val EXAMPLE_ECHO_MESSAGE_1 = "some message 1"
        private const val EXAMPLE_ECHO_MESSAGE_2 = "some message 2"
        private const val REQUEST_TAG = "requestTag"
        private const val DELAY = 20L
    }

    private val workManager by lazy { WorkManager.getInstance() }

    @Before
    fun setUp() {
        initializeTestWorkManager()
    }

    @Test
    fun someInputData_echoWorker_successWithValueTheSameAsInput() {
        //given
        val request1 = createEchoRequestWithData(KEY_ECHO_MESSAGE to EXAMPLE_ECHO_MESSAGE_1)
        val request2 = createEchoRequestWithData(KEY_ECHO_MESSAGE to EXAMPLE_ECHO_MESSAGE_2)

        //when
        workManager.enqueue(request1)
        workManager.enqueue(request2)
        val workSingle = workManager
                .getWorkInfosByTagLiveData(REQUEST_TAG)
                .toObservable()
                .subscribeOn(AndroidSchedulers.mainThread())
                .test()

        sleep(DELAY)

        //then
        workSingle.values().apply {
            assert(size == 2)
            assert(contains(workDataOf(KEY_ECHO_MESSAGE to EXAMPLE_ECHO_MESSAGE_1)))
            assert(contains(workDataOf(KEY_ECHO_MESSAGE to EXAMPLE_ECHO_MESSAGE_2)))
        }
    }

    private fun createEchoRequestWithData(pair: Pair<String, String>): WorkRequest {
        return OneTimeWorkRequestBuilder<EchoWorker>()
                .setInputData(workDataOf(pair))
                .addTag(REQUEST_TAG)
                .build()
    }
}