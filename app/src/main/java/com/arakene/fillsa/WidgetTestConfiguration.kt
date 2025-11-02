package com.arakene.fillsa

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.glance.appwidget.GlanceAppWidgetManager
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
                onConfirm = { selectedTheme, selectedSize ->
                    saveConfig(appWidgetId, selectedTheme, selectedSize)
                    updateWidget(appWidgetId)
                    setResultAndFinish()
                }
            )
        }
    }

    // At the top level of your kotlin file:


    private val scope  = CoroutineScope(Dispatchers.IO)

    private fun saveConfig(appWidgetId: Int, theme: String, size: String) {
        // SharedPreferences 등으로 설정 저장
        scope.launch {
            this@WidgetTestConfiguration.dataStore.edit {
                it[WidgetPrefsKey.TEST_STRING] = "${(System.currentTimeMillis() / 1000f)}"
            }
        }
    }

    private fun updateWidget(appWidgetId: Int) {
        AppWidgetManager.getInstance(this)
//        appWidgetManager.update
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

@Composable
fun MyWidgetConfigScreen(
    onConfirm: (String, String) -> Unit
) {
    var selectedTheme by remember { mutableStateOf("Dark") }
    var selectedSize by remember { mutableStateOf("Medium") }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Text("위젯 설정", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(16.dp))

        Text("테마 선택")
        Row {
            listOf("Dark", "Light").forEach { theme ->
                Button(
                    onClick = { selectedTheme = theme },
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text(theme)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Button(
            modifier = Modifier.align(Alignment.End),
            onClick = { onConfirm(selectedTheme, selectedSize) }
        ) {
            Text("위젯 추가")
        }
    }
}
