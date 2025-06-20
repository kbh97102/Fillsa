package com.arakene.data.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.util.Log
import androidx.core.os.bundleOf
import com.arakene.domain.usecase.home.GetDailyQuoteNoTokenUseCase
import com.arakene.domain.util.ApiResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class AlarmManagerHelper @Inject constructor(
    private val context: Context,
    val getNotificationMessageUseCase: GetDailyQuoteNoTokenUseCase
) {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private val intent = Intent(context, AlarmReceiver::class.java).apply {
        action = "com.arakene.TESTACTION"
    }


    private fun checkDuplicated(): Boolean {

        val checkPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        return checkPendingIntent != null
    }

    fun setAlarm() {

        Log.e(">>>>", "Set Alarm")
        if (checkDuplicated()) {
            Log.e(">>>>", "Duplicated")
            return
        }

        scope.launch {
            val time = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now())
            val message = getNotificationMessageUseCase(time) as? ApiResult.Success

            intent.putExtras(
                bundleOf(
                    "quote" to message?.data?.korQuote,
                    "author" to message?.data?.korAuthor
                )
            )

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 9)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                if (before(Calendar.getInstance())) {
                    add(Calendar.DAY_OF_MONTH, 1)
                }
            }

            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + 61 * 1000L,
                pendingIntent
            )

//            alarmManager.setRepeating(
//                AlarmManager.RTC_WAKEUP,
//                calendar.timeInMillis,
//                AlarmManager.INTERVAL_DAY,
//                pendingIntent
//            )

        }
    }

    fun cancelAlarm() {

        Log.e(">>>>", "Cancel Alarm")
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(pendingIntent)
    }

}