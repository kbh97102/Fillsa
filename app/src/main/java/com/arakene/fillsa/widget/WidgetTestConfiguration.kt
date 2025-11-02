package com.arakene.fillsa.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.datastore.preferences.core.edit
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.arakene.fillsa.widget.ui.MyWidget
import com.arakene.fillsa.widget.ui.MyWidgetConfigScreen
import com.arakene.presentation.ui.theme.FillsaTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WidgetTestConfiguration : ComponentActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 위젯 ID 가져오기
        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        setContent {
            MyWidgetConfigScreen(
                onClick = {
                    updateWidget(appWidgetId)
                    setResultAndFinish()
                },
                dataStore = dataStore
            )
        }
    }

    private fun updateWidget(appWidgetId: Int) {
        AppWidgetManager.getInstance(this)
        CoroutineScope(Dispatchers.IO).launch {
            val glanceManager = GlanceAppWidgetManager(this@WidgetTestConfiguration)
            val glanceId = glanceManager.getGlanceIdBy(appWidgetId)
            MyWidget().update(this@WidgetTestConfiguration, glanceId)
        }
    }

    private fun setResultAndFinish() {
        val resultValue = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(RESULT_OK, resultValue)
        finish()
    }
}
