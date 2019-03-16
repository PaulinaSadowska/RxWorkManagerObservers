package com.paulinasadowska.rxworkmanagerobservers

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.*
import com.paulinasadowska.rxworkmanagerobservers.exceptions.WorkFailedException
import com.paulinasadowska.rxworkmanagerobservers.utils.DEFAULT_DELAY
import com.paulinasadowska.rxworkmanagerobservers.utils.createEchoRequest
import com.paulinasadowska.rxworkmanagerobservers.utils.initializeTestWorkManager
import com.paulinasadowska.rxworkmanagerobservers.workers.EchoWorker.Companion.KEY_ECHO_MESSAGE
import io.reactivex.android.schedulers.AndroidSchedulers
import junit.framework.Assert.assertEquals
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep

@RunWith(AndroidJUnit4::class)
class WorkInfoObservableTest {

    companion object {
        private const val EXAMPLE_ECHO_MESSAGE = "some message"
    }

    private val workManager by lazy { WorkManager.getInstance() }

    @Before
    fun setUp() {
        initializeTestWorkManager()
    }

    @Test
    fun someInputData_echoWorker_statesValuesThenCompletes() {
        //given
        val request = createEchoRequest(KEY_ECHO_MESSAGE to EXAMPLE_ECHO_MESSAGE)

        //when
        workManager.enqueue(request)
        val workInfoObservable = workManager
                .getWorkInfoByIdLiveData(request.id)
                .toWorkInfoObservable()
                .subscribeOn(AndroidSchedulers.mainThread())
                .test()

        sleep(DEFAULT_DELAY)

        //then
        workInfoObservable.apply {
            assertEquals(values()[0].state, WorkInfo.State.SUCCEEDED)
            assertEquals(values()[0].outputData, workDataOf(KEY_ECHO_MESSAGE to EXAMPLE_ECHO_MESSAGE))
            assertComplete()
        }
    }

    @Test
    fun noInputData_echoWorker_stateValuesThenError() {
        //given
        val request = createEchoRequest()

        //when
        workManager.enqueue(request)
        val workInfoObservable = workManager
                .getWorkInfoByIdLiveData(request.id)
                .toWorkInfoObservable()
                .subscribeOn(AndroidSchedulers.mainThread())
                .test()

        sleep(DEFAULT_DELAY)

        //then
        workInfoObservable.apply {
            assertEquals(values()[0].state, WorkInfo.State.FAILED)
            assertError(WorkFailedException::class.java)
        }
    }
}