package com.arakene.fillsa

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.arakene.data.R
import com.arakene.domain.usecase.home.GetDailyQuoteNoTokenUseCase
import com.arakene.domain.util.ApiResult
import com.arakene.presentation.MainActivity
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

    companion object{
        private const val CHANNEL_ID = "DailyNotificationChannelID"
    }

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

        runCatching {
            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channel = NotificationChannel(
                CHANNEL_ID,
                "DailyNotification",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)

            val clickIntent = Intent(applicationContext, MainActivity::class.java).apply {
                flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }

            val pendingIntent = PendingIntent.getActivity(
                applicationContext,
                0,
                clickIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(com.arakene.presentation.R.drawable.icn_logo) // 아이콘 지정
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

            notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
        }
            .onFailure {
                it.printStackTrace()
            }
            .onSuccess {
                Log.d(">>>>", "DailyNotification Success")
            }


//        val notificationManager =
//            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        val channel = NotificationChannel(
//            "daily_channel",
//            "DailyNotification",
//            NotificationManager.IMPORTANCE_DEFAULT
//        )
//        notificationManager.createNotificationChannel(channel)
//
//        val notification = NotificationCompat.Builder(applicationContext, "daily_channel")
//            .setContentTitle(title)
//            .setContentText(message)
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .setAutoCancel(true)
//            .build()
//
//        notificationManager.notify(1001, notification)
    }
}