package com.arakene.fillsa

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.WorkManagerTestInitHelper
import com.arakene.fillsa.DailyQuoteWorker
import com.arakene.domain.responses.DailyQuoteDto
import com.arakene.domain.responses.ErrorResponse
import com.arakene.domain.usecase.db.SetLocalQuoteForWidgetUseCase
import com.arakene.domain.usecase.home.GetDailyQuoteUseCase
import com.arakene.domain.util.ApiResult
import com.arakene.domain.util.CommonError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    private lateinit var appContext: Context
    private lateinit var getDailyQuoteUseCase: GetDailyQuoteUseCase
    private lateinit var setLocalQuoteForWidgetUseCase: SetLocalQuoteForWidgetUseCase
    private lateinit var worker: DailyQuoteWorker


    @Before
    fun setup() {
        appContext = getApplicationContext<Context>()
        getDailyQuoteUseCase = mockk()
        setLocalQuoteForWidgetUseCase = mockk()
        val workerParameters = mockk<WorkerParameters>(relaxed = true)

        val config = Configuration.Builder()
            .setWorkerFactory(
                TestWorkerFactory(
                    getDailyQuoteUseCase,
                    setLocalQuoteForWidgetUseCase
                )
            )
            .build()

        WorkManagerTestInitHelper.initializeTestWorkManager(appContext, config)

        // Instantiate the worker with mocked dependencies.
        worker = DailyQuoteWorker(
            appContext,
            workerParameters,
            getDailyQuoteUseCase,
            setLocalQuoteForWidgetUseCase
        )
    }

    @Test
    fun doWork_should_return_SUCCESS_when_API_call_is_successful() = runTest {
        // Arrange
        val quote = DailyQuoteDto("Test quote", "Author")
        coEvery { getDailyQuoteUseCase(any()) } returns ApiResult.Success(quote)
        coEvery { setLocalQuoteForWidgetUseCase(any()) } returns Unit

        // Act
        val result = worker.doWork()

        // Assert
        assert(result == ListenableWorker.Result.success())
        // 해당 함수가 정확히 한번만 호출되었는가에 대한 테스
        coVerify(exactly = 1) { getDailyQuoteUseCase(any()) }
        coVerify(exactly = 1) { setLocalQuoteForWidgetUseCase(quote) }
    }

    @Test
    fun workManager_delay_test() = runTest {
        // Arrange
        val quote = DailyQuoteDto("Test quote", "Author")
        coEvery { getDailyQuoteUseCase(any()) } returns ApiResult.Success(quote)
        coEvery { setLocalQuoteForWidgetUseCase(any()) } returns Unit

        val request = OneTimeWorkRequestBuilder<DailyQuoteWorker>()
            .setInitialDelay(10, TimeUnit.SECONDS)
            .build()

        val workManager = WorkManager.getInstance(getApplicationContext())
        // Get the TestDriver
        val testDriver = WorkManagerTestInitHelper.getTestDriver(getApplicationContext())
        // Enqueue
        workManager.enqueue(request).result.get()
        // Tells the WorkManager test framework that initial delays are now met.
        testDriver?.setInitialDelayMet(request.id)
        val workInfo = workManager.getWorkInfoById(request.id).get()
        assert(workInfo?.state == WorkInfo.State.SUCCEEDED)

        coVerify(exactly = 1) { getDailyQuoteUseCase(any()) }
        coVerify(exactly = 1) { setLocalQuoteForWidgetUseCase(quote) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun scheduled_retry() = runTest {

        coEvery { getDailyQuoteUseCase(any()) } returns ApiResult.Fail(
            CommonError.ApiFail(
                ErrorResponse.defaultError()
            )
        )
        coEvery { setLocalQuoteForWidgetUseCase(any()) } returns Unit

        val workManager = WorkManager.getInstance(appContext)
        val testDriver = WorkManagerTestInitHelper.getTestDriver(appContext)

        // TODO: 난 알람매니저를 통해서 정확한 트리거 타임을 사용하고싶은데 PeriodicWorkRequest는 불가능한건지, 테스트에서만 이렇게 해도 되는걸지
        val request = OneTimeWorkRequestBuilder<DailyQuoteWorker>().build()

        workManager.enqueue(request).result.get()

        testDriver?.setAllConstraintsMet(request.id)

        var workInfo: WorkInfo? = null
        repeat(20) {
            workInfo = workManager.getWorkInfoById(request.id).get()
            if (workInfo?.state?.isFinished == true) return@repeat
            delay(100)
        }

        var retryInfo: List<WorkInfo>? = null
        repeat(20) {
            retryInfo = workManager.getWorkInfosForUniqueWork("FILLSA_WIDGET_RETRY").get()
            if (retryInfo?.isNotEmpty() == true) return@repeat
            delay(100)
        }

        assertTrue(retryInfo?.isNotEmpty() == true)
        Log.d("test2", "Next retry scheduled: ${retryInfo?.firstOrNull()}")
    }

}


class TestWorkerFactory(
    private val getDailyQuoteUseCase: GetDailyQuoteUseCase,
    private val setLocalQuoteForWidgetUseCase: SetLocalQuoteForWidgetUseCase
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return if (workerClassName == DailyQuoteWorker::class.qualifiedName) {
            DailyQuoteWorker(
                appContext,
                workerParameters,
                getDailyQuoteUseCase,
                setLocalQuoteForWidgetUseCase
            )
        } else {
            null
        }
    }
}