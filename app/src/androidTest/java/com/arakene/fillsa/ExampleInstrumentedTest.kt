package com.arakene.fillsa

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.arakene.data.util.DailyQuoteWorker
import com.arakene.domain.responses.DailyQuoteDto
import com.arakene.domain.usecase.db.SetLocalQuoteForWidgetUseCase
import com.arakene.domain.usecase.home.GetDailyQuoteUseCase
import com.arakene.domain.util.ApiResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Before

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
        appContext = mockk(relaxed = true)
        getDailyQuoteUseCase = mockk()
        setLocalQuoteForWidgetUseCase = mockk()
        val workerParameters = mockk<WorkerParameters>(relaxed = true)

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
}