package com.arakene.data.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.arakene.data.R
import com.arakene.domain.usecase.home.GetDailyQuoteNoTokenUseCase
import com.arakene.domain.util.ApiResult
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@HiltWorker
class DailyNotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    val getNotificationMessageUseCase: GetDailyQuoteNoTokenUseCase
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {

            val time = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now())

            val message = getNotificationMessageUseCase(time)

            if (message is ApiResult.Success) {
                showNotification(
                    "오늘의 필사 문장",
                    "${message.data.korQuote} - ${message.data.korAuthor}"
                )
                return Result.success()
            }

            Result.failure()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun showNotification(title: String, message: String) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            "daily_channel",
            "DailyNotification",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(applicationContext, "daily_channel")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1001, notification)
    }
}
