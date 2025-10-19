package com.arakene.fillsa

import android.content.Context
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.glance.text.Text
import com.arakene.domain.usecase.db.GetLocalQuoteForWidgetUseCase
import com.arakene.presentation.R
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
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

        val dailyQuoteInfo = withContext(Dispatchers.IO) {
            testUseCase()
        }
//        val pretendard = FontFamily(
//            Font(R.font.pretendard_400, FontWeight.Normal, FontStyle.Normal),
//            Font(R.font.pretendard_700, FontWeight.Bold, FontStyle.Normal),
//        )
        provideContent {
            val dailyQuote by dailyQuoteInfo.collectAsState(null)
            GlanceTheme {
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
                            "오늘의 문장", style = androidx.glance.text.TextStyle(
                                fontWeight = androidx.glance.text.FontWeight.Bold,
                                fontSize = 6.sp,
                                color = ColorProvider(
                                    day = Color(context.getColor(R.color.gray_700)),
                                    night = Color.Cyan
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
                            dailyQuote?.korQuote ?: "",
                            style = androidx.glance.text.TextStyle(
                                fontWeight = androidx.glance.text.FontWeight.Bold,
                                fontSize = 12.sp,
                                color = ColorProvider(
                                    day = Color(context.getColor(R.color.purple01)),
                                    night = Color.Cyan
                                ),
                            ),
                        )
                        // 저자
                        Text(
                            dailyQuote?.korAuthor ?: "",
                            style = androidx.glance.text.TextStyle(
                                fontWeight = androidx.glance.text.FontWeight.Bold,
                                fontSize = 10.sp,
                            ),
                        )
                    }
                }
            }
        }
    }

}