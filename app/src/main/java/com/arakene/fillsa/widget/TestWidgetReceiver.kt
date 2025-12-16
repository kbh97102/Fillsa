package com.arakene.fillsa.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import com.arakene.fillsa.widget.ui.MyWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TestWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MyWidget()

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        super.onAppWidgetOptionsChanged(
            context,
            appWidgetManager,
            appWidgetId,
            newOptions
        )

        val minWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
        val minHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)

        Log.e(">>>>SIZE", "minWidth=$minWidth minHeight=$minHeight")

        val sizeType =
            if (minWidth < 200) "SMALL" else "LARGE"

        val glanceId = GlanceAppWidgetManager(context).getGlanceIdBy(appWidgetId)

        scope.launch {
            context.dataStore.edit {
                it.set(stringPreferencesKey("SIZE_KEY"), sizeType)
            }

            glanceAppWidget.update(
                context,
                glanceId
            )
        }
    }

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        context?.let {
            Log.e("WIDGET", "setAlarm")
            WorkScheduler.scheduleDailyWorks(context)
        }
    }
}