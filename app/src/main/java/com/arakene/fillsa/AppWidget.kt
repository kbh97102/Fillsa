package com.arakene.fillsa

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.text.Text
import com.arakene.domain.usecase.home.GetDailyQuoteNoTokenUseCase
import com.arakene.domain.util.ApiResult
import com.arakene.domain.util.CommonError
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// GlanceAppWidget UI
class MyWidget : GlanceAppWidget() {

    // a way to get hilt inject what you need in non-suported class
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface StatisticsProviderEntryPoint {
        fun getDailyUseCase(): GetDailyQuoteNoTokenUseCase
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

        // TODO: 에러 핸들링이 필요한가? 리트라이정도는 해야하나
        val errorHandler = CoroutineExceptionHandler { context, throwable ->

        }

        val test = withContext(Dispatchers.IO){
            testUseCase("2025-09-16")
        }

        val testState = mutableStateOf("")

        when(test){
            is ApiResult.Fail -> {
               if (test.error is CommonError.ApiFail){
                   /*
                   TODO:
                       - 재시도 패턴(보통 자정에 데이터 업데이트 실패를 고려하여)
                            - 오전 6시
                            - 오후 12시
                    */
               }
            }
            else -> {}
        }

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