package com.paulinasadowska.rxworkmanagerobservers

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import com.paulinasadowska.rxworkmanagerobservers.exceptions.WorkFailedException
import com.paulinasadowska.rxworkmanagerobservers.extensions.getWorkDatasByTagObservable
import com.paulinasadowska.rxworkmanagerobservers.utils.DEFAULT_DELAY
import com.paulinasadowska.rxworkmanagerobservers.utils.DEFAULT_DELAY_LONG
import com.paulinasadowska.rxworkmanagerobservers.utils.createEchoRequest
import com.paulinasadowska.rxworkmanagerobservers.utils.initializeTestWorkManager
import com.paulinasadowska.rxworkmanagerobservers.workers.EchoWorker.Companion.KEY_ECHO_MESSAGE
import io.reactivex.android.schedulers.AndroidSchedulers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.emptyIterable
import org.hamcrest.Matchers.iterableWithSize
import org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep

@RunWith(AndroidJUnit4::class)
class WorkDatasObservableTest {

    companion object {
        private const val EXAMPLE_ECHO_MESSAGE_1 = "some message 1"
        private const val EXAMPLE_ECHO_MESSAGE_2 = "some message 2"
        private const val REQUEST_TAG = "requestTag"
    }

    private val workManager by lazy { WorkManager.getInstance() }

    @Before
    fun setUp() {
        initializeTestWorkManager()
    }

    @Test
    fun someInputData_echoWorker_completesWithTwoValues() {
        //given
        val request1 = createEchoRequestWithTag(KEY_ECHO_MESSAGE to EXAMPLE_ECHO_MESSAGE_1)
        val request2 = createEchoRequestWithTag(KEY_ECHO_MESSAGE to EXAMPLE_ECHO_MESSAGE_2)

        //when
        workManager.enqueue(request1)
        workManager.enqueue(request2)
        val workListObserver = workManager
                .getWorkDatasByTagObservable(REQUEST_TAG)
                .subscribeOn(AndroidSchedulers.mainThread())
                .test()

        sleep(DEFAULT_DELAY)

        //then
        workListObserver.values().apply {
            assertThat(this, iterableWithSize(2))
            assertThat(this,
                    containsInAnyOrder(
                            workDataOf(KEY_ECHO_MESSAGE to EXAMPLE_ECHO_MESSAGE_1),
                            workDataOf(KEY_ECHO_MESSAGE to EXAMPLE_ECHO_MESSAGE_2)
                    ))
        }
        workListObserver.assertComplete()
    }

    @Test
    fun onlyOneRequestWithInputData_echoWorker_completesWithOneValue() {
        //given
        val request1 = createEchoRequestWithTag()
        val request2 = createEchoRequestWithTag(KEY_ECHO_MESSAGE to EXAMPLE_ECHO_MESSAGE_2)

        //when
        workManager.enqueue(request1)
        workManager.enqueue(request2)
        val workListObserver = workManager
                .getWorkDatasByTagObservable(REQUEST_TAG)
                .subscribeOn(AndroidSchedulers.mainThread())
                .test()

        sleep(DEFAULT_DELAY)

        //then
        workListObserver.values().apply {
            assertThat(this, iterableWithSize(1))
            assertThat(this, Matchers.contains(
                    workDataOf(KEY_ECHO_MESSAGE to EXAMPLE_ECHO_MESSAGE_2)
            ))
        }
        workListObserver.assertComplete()
    }

    @Test
    fun allRequestsFail_echoWorker_completesWithNoValues() {
        //given
        val request1 = createEchoRequestWithTag()
        val request2 = createEchoRequestWithTag()

        //when
        workManager.enqueue(request1)
        workManager.enqueue(request2)
        val workListObserver = workManager
                .getWorkDatasByTagObservable(REQUEST_TAG)
                .subscribeOn(AndroidSchedulers.mainThread())
                .test()

        sleep(DEFAULT_DELAY_LONG)

        //then
        assertThat(workListObserver.values(), emptyIterable())
        workListObserver.assertComplete()
    }

    @Test
    fun oneRequestFailAndIgnoreErrorFalse_echoWorker_error() {
        //given
        val request1 = createEchoRequestWithTag(KEY_ECHO_MESSAGE to EXAMPLE_ECHO_MESSAGE_1)
        val request2 = createEchoRequestWithTag()

        //when
        workManager.enqueue(request1)
        workManager.enqueue(request2)

        val workListObserver = workManager
                .getWorkDatasByTagObservable(REQUEST_TAG, false)
                .subscribeOn(AndroidSchedulers.mainThread())
                .test()

        sleep(DEFAULT_DELAY)

        //then
        workListObserver.values().apply {
            assertThat(this, iterableWithSize(1))
            assertThat(this, Matchers.contains(
                    workDataOf(KEY_ECHO_MESSAGE to EXAMPLE_ECHO_MESSAGE_1)
            ))
        }
        workListObserver.assertError(WorkFailedException::class.java)
    }

    private fun createEchoRequestWithTag(pair: Pair<String, String>? = null): WorkRequest {
        return createEchoRequest(pair, REQUEST_TAG)
    }
}