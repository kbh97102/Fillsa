package com.arakene.fillsa

import android.content.Context
import android.util.Log
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.text.Text
import com.arakene.domain.usecase.db.GetLocalQuoteForWidgetUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

// GlanceAppWidget UI
class MyWidget : GlanceAppWidget() {

    // a way to get hilt inject what you need in non-suported class
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface StatisticsProviderEntryPoint {
        fun getDailyUseCase(): GetLocalQuoteForWidgetUseCase
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        // TODO: 테이터/와이파이 미연결 후 다시 안결된 경우 데이터 재호출 필요

        val appContext = context.applicationContext ?: throw IllegalStateException()
        val statisticsEntryPoint =
            EntryPointAccessors.fromApplication(
                appContext,
                StatisticsProviderEntryPoint::class.java,
            )
        val testUseCase = statisticsEntryPoint.getDailyUseCase()

        val test = withContext(Dispatchers.IO) {
            testUseCase()
        }

        provideContent {
            val testRemember by test.collectAsState(null)

            GlanceTheme {
                Text("test ${testRemember}")
            }
        }
    }
}