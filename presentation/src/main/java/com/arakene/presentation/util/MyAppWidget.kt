package com.arakene.presentation.util

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import com.arakene.domain.repository.WidgetRepository
import com.arakene.domain.responses.DailyQuotaNoToken
import dagger.hilt.EntryPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MyAppWidget : GlanceAppWidget() {

    companion object {
        private val SMALL_SQUARE = DpSize(100.dp, 100.dp)
        private val HORIZONTAL_RECTANGLE = DpSize(250.dp, 100.dp)
        private val BIG_SQUARE = DpSize(250.dp, 250.dp)
    }

    override val sizeMode = SizeMode.Responsive(
        setOf(
            SMALL_SQUARE,
            HORIZONTAL_RECTANGLE,
            BIG_SQUARE
        )
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        // In this method, load data needed to render the AppWidget.
        // Use `withContext` to switch to another thread for long running
        // operations.

        val repository = EntryPoints.get(
            context,
            WidgetModelRepositoryEntryPoint::class.java
        ).widgetModelRepository()

        val testValue = withContext(Dispatchers.IO) {
            repository.getData("2025-06-24")
        }

        provideContent {
            // create your AppWidget here
            TestView(testValue)
        }
    }

    @Composable
    private fun TestView(testValue: DailyQuotaNoToken?) {
        val size = LocalSize.current

        when {
            testValue != null -> {
                Column(modifier = GlanceModifier.fillMaxSize().background(Color.White)) {
                    if (size.height >= BIG_SQUARE.height) {
                        Text(text = "Where to?", modifier = GlanceModifier.padding(12.dp))
                    }
                    Row(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(testValue.korAuthor ?: "")
                        Text(testValue.korQuote ?: "")

                        if (size.width >= HORIZONTAL_RECTANGLE.width) {
                            Button("School", onClick = {})
                        }
                    }
                    if (size.height >= BIG_SQUARE.height) {
                        Text(text = "provided by X")
                    }
                }
            }

            else -> {

            }
        }


    }
}