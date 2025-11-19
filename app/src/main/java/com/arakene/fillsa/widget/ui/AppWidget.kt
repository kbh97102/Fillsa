package com.arakene.fillsa.widget.ui

import android.content.Context
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.arakene.domain.responses.MemberStreakResponse
import com.arakene.domain.usecase.common.GetMemberStreaksUseCase
import com.arakene.domain.usecase.db.GetLocalQuoteForWidgetUseCase
import com.arakene.fillsa.widget.WidgetPrefsKey
import com.arakene.fillsa.widget.dataStore
import com.arakene.presentation.R
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

// GlanceAppWidget UI
class MyWidget : GlanceAppWidget() {

    override val stateDefinition = MyDataStoreStateDefinition

    // a way to get hilt inject what you need in non-suported class
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WidgetHiltEntryPoint {
        fun getDailyUseCase(): GetLocalQuoteForWidgetUseCase
        fun getStreak(): GetMemberStreaksUseCase
    }

    // TODO: 데이터 관리 방법에 더 간단한 방법이 있는걸로 파악됨 https://proandroiddev.com/widgets-with-glance-beyond-string-states-2dcc4db2f76c 참고

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        // TODO: 테이터/와이파이 미연결 후 다시 안결된 경우 데이터 재호출 필요
        val appContext = context.applicationContext ?: throw IllegalStateException()
        val widgetEntryPoint =
            EntryPointAccessors.fromApplication(
                appContext,
                WidgetHiltEntryPoint::class.java,
            )
        val getDailyUseCase = widgetEntryPoint.getDailyUseCase()
        val streakUseCase = widgetEntryPoint.getStreak()

        val dailyQuoteInfo = withContext(Dispatchers.IO) {
            getDailyUseCase()
        }

        val streakState = mutableStateOf<MemberStreakResponse?>(null)

        withContext(Dispatchers.IO) {
            streakUseCase().let {
                streakState.value = it
            }
        }

        provideContent {
            val dailyQuote by dailyQuoteInfo.collectAsState(null)
            val streak by remember { streakState }
            GlanceTheme {

                val context = LocalContext.current

                val prefs = currentState<Preferences>()

                val widgetFontSize = prefs[WidgetPrefsKey.FONT_SIZE_KEY] ?: "중간"
                val widgetLanguage = prefs[WidgetPrefsKey.LANGUAGE_KEY] ?: "한국어"

                val fontSize = when (widgetFontSize) {
                    "작게" -> 16.sp
                    "중간" -> 18.sp
                    else -> 20.sp
                }

                Column(
                    modifier = GlanceModifier.fillMaxSize().background(R.color.primary)
                        .padding(horizontal = 10.dp, vertical = 11.dp),
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

                        Spacer(GlanceModifier.defaultWeight())

                        if (streak != null && (streak?.currentStreak ?: 0) > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    provider = ImageProvider(
                                        (R.drawable.icn_today_complete)
                                    ),
                                    contentDescription = null,
                                    modifier = GlanceModifier.size(24.dp)
                                )

                                Text(
                                    "${streak?.currentStreak}일", style = TextStyle(
                                        fontWeight = FontWeight.Normal,
                                        fontSize = fontSize,
                                        color = ColorProvider(
                                            day = Color(context.getColor(R.color.gray_700)),
                                            night = Color(context.getColor(R.color.gray_700)),
                                        ),
                                        textAlign = TextAlign.Center,
                                    )
                                )
                            }
                        }
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
                            text = if (widgetLanguage == "한국어") dailyQuote?.korQuote
                                ?: "" else dailyQuote?.engQuote ?: "",
                            style = TextStyle(
                                fontWeight = FontWeight.Normal,
                                fontSize = fontSize,
                                color = ColorProvider(
                                    day = Color(context.getColor(R.color.purple01)),
                                    night = Color(context.getColor(R.color.purple01)),
                                ),
                                textAlign = TextAlign.Center,
                            ),
                        )
                        // 저자
                        Text(
                            if (widgetLanguage == "한국어") dailyQuote?.korAuthor
                                ?: "" else dailyQuote?.engAuthor ?: "",
                            style = TextStyle(
                                fontWeight = FontWeight.Normal,
                                fontSize = fontSize,
                            ),
                        )
                    }
                }
            }
        }
    }

    object MyDataStoreStateDefinition : GlanceStateDefinition<Preferences> {
        private const val DATA_STORE_NAME = "widget_test"

        override suspend fun getDataStore(
            context: Context,
            fileKey: String
        ): DataStore<Preferences> {
            return context.dataStore
        }

        override fun getLocation(context: Context, fileKey: String): File {
            return context.dataStoreFile(DATA_STORE_NAME)
        }
    }

}