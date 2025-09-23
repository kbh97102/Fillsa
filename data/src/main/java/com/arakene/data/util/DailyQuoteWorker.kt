package com.arakene.data.util

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.arakene.domain.usecase.db.SetLocalQuoteForWidgetUseCase
import com.arakene.domain.usecase.home.GetDailyQuoteUseCase
import com.arakene.domain.util.ApiResult
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@HiltWorker
class DailyQuoteWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val getDailyQuoteUseCase: GetDailyQuoteUseCase, // UseCase 주입
    private val setLocalQuoteForWidgetUseCase: SetLocalQuoteForWidgetUseCase

) : CoroutineWorker(appContext, workerParams) {

    val parser = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override suspend fun doWork(): Result {
        return try {
            val response = getDailyQuoteUseCase(parser.format(LocalDate.now())) // UseCase 실행

            when (response) {
                is ApiResult.Success -> {
                   setLocalQuoteForWidgetUseCase(response.data)
                }
                else -> {
                    // TODO: 에러처리
                }
            }

            Result.success()
        } catch (e: Exception) {
            // API 응답값이 200이 아닌 경우에 대한 재시도 정책
            Result.retry()
        }
    }
}