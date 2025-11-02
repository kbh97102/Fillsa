package com.arakene.fillsa.widget

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.arakene.domain.usecase.db.SetLocalQuoteForWidgetUseCase
import com.arakene.domain.usecase.home.GetDailyQuoteNoTokenUseCase
import com.arakene.domain.util.ApiResult
import com.arakene.domain.util.CommonError
import com.arakene.fillsa.widget.ui.MyWidget
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlin.math.min

@HiltWorker
class DailyQuoteWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val getDailyQuoteUseCase: GetDailyQuoteNoTokenUseCase, // UseCase 주입
    private val setLocalQuoteForWidgetUseCase: SetLocalQuoteForWidgetUseCase

) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val KEY_RETRY_COUNT = "KEY_RETRY_COUNT"
        const val RETRY_COUNT = 2
    }

    private val parser = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override suspend fun doWork(): Result {
        val currentRetryCount = inputData.getInt(KEY_RETRY_COUNT, 0)

        if (currentRetryCount >= RETRY_COUNT) {
            Log.e("WIDGET", "Max retry attempts reached")
            return Result.failure()
        }

        return try {
            val response = getDailyQuoteUseCase(parser.format(LocalDate.now())) // UseCase 실행


            when (response) {
                is ApiResult.Success -> {
                    setLocalQuoteForWidgetUseCase(response.data)
                    Log.e("WIDGET>", "Worker Success ${response.data}")

                    MyWidget().updateAll(applicationContext)

                    Result.success()
                }

                is ApiResult.Fail -> {
                    Log.e("WIDGET", "Worker FAIL ${response.error}")
                    // TODO: 에러처리
                    if (response.error is CommonError.ApiFail) {
                        scheduleNextRetry(currentRetryCount + 1)
                        Result.success()
                    } else {
                        Result.failure()
                    }
                }
            }
        }
        catch (e: Exception) {
            Result.retry()
        }
    }

    private fun scheduleNextRetry(nextAttemptCount: Int) {
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance().apply { timeInMillis = now }

        val next6am = (calendar.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 6)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            if (timeInMillis <= now) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }.timeInMillis

        val next12pm = (calendar.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            if (timeInMillis <= now) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }.timeInMillis

        val delay = min(next6am - now, next12pm - now)

        val inputData = workDataOf(KEY_RETRY_COUNT to nextAttemptCount)

        val workRequest = OneTimeWorkRequest.Builder(DailyQuoteWorker::class.java)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .addTag("WIDGET")
            .build()

        WorkManager.Companion.getInstance(applicationContext).enqueueUniqueWork(
            "FILLSA_WIDGET_RETRY",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
}