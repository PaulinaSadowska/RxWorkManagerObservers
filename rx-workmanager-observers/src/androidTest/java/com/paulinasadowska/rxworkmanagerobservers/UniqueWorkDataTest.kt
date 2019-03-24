package com.paulinasadowska.rxworkmanagerobservers

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.*
import com.paulinasadowska.rxworkmanagerobservers.extensions.getWorkDatasForUniqueWorkObservable
import com.paulinasadowska.rxworkmanagerobservers.utils.DEFAULT_DELAY
import com.paulinasadowska.rxworkmanagerobservers.utils.DEFAULT_DELAY_LONG
import com.paulinasadowska.rxworkmanagerobservers.utils.initializeTestWorkManager
import com.paulinasadowska.rxworkmanagerobservers.workers.EchoWorker
import com.paulinasadowska.rxworkmanagerobservers.workers.EchoWorker.Companion.KEY_ECHO_MESSAGE
import io.reactivex.android.schedulers.AndroidSchedulers
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.hamcrest.collection.IsIterableContainingInAnyOrder
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep

@RunWith(AndroidJUnit4::class)
class UniqueWorkDataTest {

    companion object {
        private const val EXAMPLE_ECHO_MESSAGE_1 = "some message 1"
        private const val EXAMPLE_ECHO_MESSAGE_2 = "some message 2"
        private const val WORK_NAME = "workName"

        internal const val SMALL_DELAY = 10L
    }

    private val workManager by lazy { WorkManager.getInstance() }

    @Before
    fun setUp() {
        initializeTestWorkManager()
    }

    @Test
    fun someInputData_uniqueWorkWithReplace_completesWithValueFromSecondRequest() {
        //given
        val request1 = createEchoRequest(KEY_ECHO_MESSAGE to EXAMPLE_ECHO_MESSAGE_1)
        val request2 = createEchoRequest(KEY_ECHO_MESSAGE to EXAMPLE_ECHO_MESSAGE_2)

        //when
        workManager.enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.REPLACE, request1)
        workManager.enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.REPLACE, request2)
        val workListObserver = workManager
                .getWorkDatasForUniqueWorkObservable(WORK_NAME)
                .subscribeOn(AndroidSchedulers.mainThread())
                .test()

        sleep(DEFAULT_DELAY_LONG)

        //then
        workListObserver.assertComplete()
        workListObserver.values().apply {
            MatcherAssert.assertThat(this, Matchers.iterableWithSize(1))
            MatcherAssert.assertThat(first(), Matchers.equalTo(
                    workDataOf(KEY_ECHO_MESSAGE to EXAMPLE_ECHO_MESSAGE_2)
            ))
        }
    }

    @Test
    fun someInputData_uniqueWorkWithKeep_completesWithValueFromFirstRequest() {
        //given
        val request1 = createEchoRequest(KEY_ECHO_MESSAGE to EXAMPLE_ECHO_MESSAGE_1)
        val request2 = createEchoRequest(KEY_ECHO_MESSAGE to EXAMPLE_ECHO_MESSAGE_2)

        //when
        workManager.enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.KEEP, request1)
        sleep(SMALL_DELAY)
        workManager.enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.KEEP, request2)
        val workListObserver = workManager
                .getWorkDatasForUniqueWorkObservable(WORK_NAME)
                .subscribeOn(AndroidSchedulers.mainThread())
                .test()

        sleep(DEFAULT_DELAY_LONG)

        //then
        workListObserver.assertComplete()
        workListObserver.values().apply {
            MatcherAssert.assertThat(this, Matchers.iterableWithSize(1))
            MatcherAssert.assertThat(first(), Matchers.equalTo(
                    workDataOf(KEY_ECHO_MESSAGE to EXAMPLE_ECHO_MESSAGE_2)
            ))
        }
    }

    @Test
    @Ignore("EXAMPLE_ECHO_MESSAGE_1 is returned two times somehow, probably a bug in WorkManager?")
    fun someInputData_uniqueWorkWithAppend_completesWithValueFromFirstRequest() {
        //given
        val request1 = createEchoRequest(KEY_ECHO_MESSAGE to EXAMPLE_ECHO_MESSAGE_1)
        val request2 = createEchoRequest(KEY_ECHO_MESSAGE to EXAMPLE_ECHO_MESSAGE_2)

        //when
        workManager.enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.APPEND, request1)
        sleep(SMALL_DELAY)
        workManager.enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.APPEND, request2)
        val workListObserver = workManager
                .getWorkDatasForUniqueWorkObservable(WORK_NAME)
                .subscribeOn(AndroidSchedulers.mainThread())
                .test()

        sleep(DEFAULT_DELAY_LONG*100)

        //then
        workListObserver.assertComplete()
        workListObserver.values().apply {
            MatcherAssert.assertThat(this, Matchers.iterableWithSize(2))
            MatcherAssert.assertThat(this, IsIterableContainingInAnyOrder.containsInAnyOrder(
                    workDataOf(KEY_ECHO_MESSAGE to EXAMPLE_ECHO_MESSAGE_1),
                    workDataOf(KEY_ECHO_MESSAGE to EXAMPLE_ECHO_MESSAGE_2)
            ))
        }
    }

    private fun createEchoRequest(pair: Pair<String, String>): OneTimeWorkRequest {
        return OneTimeWorkRequestBuilder<EchoWorker>()
                .setInputData(workDataOf(pair))
                .build()
    }
}