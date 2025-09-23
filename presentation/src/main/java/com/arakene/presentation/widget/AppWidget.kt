package com.arakene.presentation.widget

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.text.Text
import com.arakene.domain.usecase.home.GetDailyQuoteNoTokenUseCase
import com.arakene.domain.util.ApiResult
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.FillsaColorScheme
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher

// GlanceAppWidget UI
class MyWidget : GlanceAppWidget() {

    // a way to get hilt inject what you need in non-suported class
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface StatisticsProviderEntryPoint {
        fun getDailyUseCase(): GetDailyQuoteNoTokenUseCase
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        val appContext = context.applicationContext ?: throw IllegalStateException()
        val statisticsEntryPoint =
            EntryPointAccessors.fromApplication(
                appContext,
                StatisticsProviderEntryPoint::class.java,
            )
        val testUseCase = statisticsEntryPoint.getDailyUseCase()

        val test = withContext(Dispatchers.IO){
            testUseCase("2025-09-16")
        }

        val testState = mutableStateOf("")

        if (test is ApiResult.Success){
            Log.e("WIDGET", "widget ${test}")
            testState.value = test.data.korQuote ?: ""
        } else {
            Log.e("WIDGET", "Api Call Fail")
        }

        provideContent {
            val testRemember by remember {
                testState
            }
            GlanceTheme(){
//                currentState()
                Text("test ${testRemember}")
            }
        }
    }
}