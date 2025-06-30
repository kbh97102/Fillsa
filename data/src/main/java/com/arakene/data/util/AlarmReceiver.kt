package com.arakene.data.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.arakene.data.R

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {

        val quote = p1?.getStringExtra("quote")
        val author = p1?.getStringExtra("author")

        if (!quote.isNullOrBlank() && !author.isNullOrBlank()) {
            showNotification(context = p0 ?: return, title = "오늘의 필사", message = "$quote - $author")
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

            val builder = NotificationCompat.Builder(context, "alarm_channel_id")
                .setSmallIcon(R.drawable.icn_logo_alert) // 아이콘 지정
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

            notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
        }
            .onFailure {
                it.printStackTrace()
            }

    }
}