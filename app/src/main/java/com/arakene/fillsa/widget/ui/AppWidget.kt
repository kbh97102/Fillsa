package com.arakene.fillsa.widget.ui

import android.content.Context
import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.arakene.domain.usecase.db.GetLocalQuoteForWidgetUseCase
import com.arakene.fillsa.widget.WidgetPrefsKey
import com.arakene.fillsa.widget.dataStore
import com.arakene.presentation.R
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

    // TODO: 데이터 관리 방법에 더 간단한 방법이 있는걸로 파악됨 https://proandroiddev.com/widgets-with-glance-beyond-string-states-2dcc4db2f76c 참고

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        // TODO: 테이터/와이파이 미연결 후 다시 안결된 경우 데이터 재호출 필요
        val appContext = context.applicationContext ?: throw IllegalStateException()
        val statisticsEntryPoint =
            EntryPointAccessors.fromApplication(
                appContext,
                StatisticsProviderEntryPoint::class.java,
            )
        val testUseCase = statisticsEntryPoint.getDailyUseCase()

        val dailyQuoteInfo = withContext(Dispatchers.IO) {
            testUseCase()
        }
//        val pretendard = FontFamily(
//            Font(R.font.pretendard_400, FontWeight.Normal, FontStyle.Normal),
//            Font(R.font.pretendard_700, FontWeight.Bold, FontStyle.Normal),
//        )

        val dataStore = context.dataStore

        provideContent {
            val dailyQuote by dailyQuoteInfo.collectAsState(null)
            GlanceTheme {

                var test by remember {
                    mutableStateOf("")
                }

                LaunchedEffect(dataStore) {
                    dataStore.data.collectLatest { data ->
                        test = data[WidgetPrefsKey.TEST_STRING] ?: ""
                    }
                }

                LaunchedEffect(test) {
                    if (test.isNotEmpty()) {
                        Log.e(">>>>", "WIDGET UPDATED? ${test}")
                    }
                }

                Column(
                    modifier = GlanceModifier.fillMaxSize().background(R.color.primary)
                        .padding(4.dp),
                ) {
                    // header
                    Row(verticalAlignment = Alignment.Vertical.CenterVertically) {
                        Image(
                            ImageProvider(R.drawable.icn_logo),
                            contentDescription = null
                        )

                        Text(
                            "오늘의 문장", style = TextStyle(
                                fontWeight = FontWeight.Normal,
                                fontSize = 18.sp,
                                color = ColorProvider(
                                    day = Color(context.getColor(R.color.gray_700)),
                                    night = Color(context.getColor(R.color.gray_700))
                                ),
                            )
                        )
                    }

                    Column(
                        modifier = GlanceModifier
                            .defaultWeight()
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 연속 로그인
                        // 명언
                        Text(
                            modifier = GlanceModifier.fillMaxWidth(),
                            text = dailyQuote?.korQuote ?: "",
                            style = TextStyle(
                                fontWeight = FontWeight.Normal,
                                fontSize = 18.sp,
                                color = ColorProvider(
                                    day = Color(context.getColor(R.color.purple01)),
                                    night = Color(context.getColor(R.color.purple01)),
                                ),
                                textAlign = TextAlign.Center,
                            ),
                        )
                        // 저자
                        Text(
                            dailyQuote?.korAuthor ?: "",
                            style = TextStyle(
                                fontWeight = FontWeight.Normal,
                                fontSize = 18.sp,
                            ),
                        )
                    }
                }
            }
        }
    }

}