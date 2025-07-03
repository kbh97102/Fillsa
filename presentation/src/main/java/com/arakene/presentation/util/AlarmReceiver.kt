package com.arakene.presentation.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.arakene.domain.util.ApiResult
import com.arakene.presentation.MainActivity
import com.arakene.presentation.R
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AlarmReceiver : BroadcastReceiver() {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onReceive(p0: Context?, p1: Intent?) {

        p0?.let {
            val entryPoint = EntryPointAccessors.fromApplication(
                it,
                AlarmReceiverEntryPoint::class.java
            )
            val useCase = entryPoint.getQuoteUseCase()
            val manager = entryPoint.getAlarmManager()

            scope.launch {
                val time = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now())
                val data = useCase(time)
                if (data is ApiResult.Success) {
                    showNotification(
                        it,
                        "오늘의 필사 문장",
                        "${data.data.korQuote} - ${data.data.korAuthor}"
                    )
                }
            }

            manager.scheduleExactAlarm(it)
        }
    }

    private fun showNotification(context: Context, title: String, message: String) {
        kotlin.runCatching {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channel = NotificationChannel(
                "alarm_channel_id",
                "알람 채널",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)

            val clickIntent = Intent(context, MainActivity::class.java).apply {
                flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                clickIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(context, "alarm_channel_id")
                .setSmallIcon(R.drawable.icn_logo) // 아이콘 지정
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
                Log.d(">>>>", "Success")
            }

    }
}