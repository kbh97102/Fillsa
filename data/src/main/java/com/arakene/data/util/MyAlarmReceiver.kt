package com.arakene.data.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class MyAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // 알람이 트리거되면 WorkManager에 OneTimeWorkRequest를 스케줄링
        val dailyQuoteRequest = OneTimeWorkRequestBuilder<DailyQuoteWorker>()
           .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            "DailyQuoteUpdate",
            ExistingWorkPolicy.REPLACE,
            dailyQuoteRequest
        )
    }
}