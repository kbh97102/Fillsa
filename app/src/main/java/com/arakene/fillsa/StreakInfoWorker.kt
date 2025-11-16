package com.arakene.fillsa

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.arakene.domain.usecase.common.CheckYesterdayStreakUseCase
import com.arakene.domain.usecase.db.GetYesterdayStreakInfoUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate
import javax.inject.Inject

@HiltWorker
class StreakInfoWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val checkYesterdayStreakUseCase: CheckYesterdayStreakUseCase
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        // 정각을 넘긴이후 실행하기에 하루 전날을 기준으로 계산
        checkYesterdayStreakUseCase.invoke()

        return Result.success()
    }
}