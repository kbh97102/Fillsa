package com.arakene.fillsa

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import javax.inject.Inject

@HiltWorker
class StreakInfoWorker(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {

        // TODO: 매일 자정이 넘어간 후 어제 날짜의 필사 여부를 체크, 만약 필사한 내역이 없다면 연속필사 일수 초기화




        return Result.success()
    }
}