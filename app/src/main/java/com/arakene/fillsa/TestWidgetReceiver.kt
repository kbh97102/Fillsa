package com.arakene.fillsa

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class TestWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MyWidget()

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        context?.let {
            Log.e("WIDGET", "setAlarm")
            WorkScheduler.scheduleDailyWorks(context)
        }
    }
}