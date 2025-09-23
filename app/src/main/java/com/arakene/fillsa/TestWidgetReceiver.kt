package com.arakene.fillsa

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.arakene.data.util.MyAlarmReceiver
import java.util.Calendar

class TestWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MyWidget()

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        context?.let {
            Log.e("WIDGET", "setAlarm")
            scheduleDailyUpdate(it)
        }
    }

    private fun scheduleDailyUpdate(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // 오전 6시 업데이트
        val morningIntent = Intent(context, MyAlarmReceiver::class.java).apply {
            putExtra("UPDATE_TIME", "MORNING")
        }
        val morningPendingIntent = PendingIntent.getBroadcast(context, 0, morningIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val morningCalendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 6)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            // 이미 시간이 지났으면 다음 날로 설정
            if (System.currentTimeMillis() > timeInMillis) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            morningCalendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            morningPendingIntent
        )

        // 자정(밤 12시) 업데이트
        val midnightIntent = Intent(context, MyAlarmReceiver::class.java).apply {
            putExtra("UPDATE_TIME", "MIDNIGHT")
        }
        val midnightPendingIntent = PendingIntent.getBroadcast(context, 1, midnightIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val midnightCalendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            // 이미 시간이 지났으면 다음 날로 설정
            if (System.currentTimeMillis() > timeInMillis) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            midnightCalendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            midnightPendingIntent
        )
    }
}