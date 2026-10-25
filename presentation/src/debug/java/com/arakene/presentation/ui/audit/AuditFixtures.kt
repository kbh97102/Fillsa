package com.arakene.presentation.ui.audit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.arakene.domain.responses.*
import com.arakene.presentation.R
import com.arakene.presentation.ui.BottomNavigationBar
import com.arakene.presentation.ui.calendar.*
import com.arakene.presentation.ui.home.*
import com.arakene.presentation.ui.mypage.*
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.*
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDate
import java.time.YearMonth
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalAsyncImagePreviewHandler
import coil3.compose.asPainter
import coil3.request.SuccessResult
import coil3.svg.Svg
import coil3.svg.width
import coil3.svg.height
import okio.source
import okio.buffer

internal const val AuditQuote = "상황을 가장 잘 활용하는 사람이 가장 좋은 상황을 맞는다."
internal const val AuditHomeQuote = "사랑이라는 선물은 억지로 줄 수 없고 받아들여지기를 기다릴 뿐이다."
internal const val AuditQuestion = "누군가의 호의를 한참 뒤에야 받아들인 적 있나요?"
internal const val AuditAnswer = "친구가 힘들 때 언제든 연락하라고 했는데, 한참 뒤에야 그 말이 진심이었다는 걸 믿고 먼저 연락한 적이 있어요."

/** Same production theme/navigation; ads and OS bars require separate runtime evidence. */
@Composable
@OptIn(coil3.annotation.ExperimentalCoilApi::class)
internal fun AuditRoot(dark: Boolean, route: String? = null, content: @Composable () -> Unit) {
    // Decode bundled SVGs synchronously so Preview captures the actual assets, not an async empty frame.
    val assets = remember {
        AsyncImagePreviewHandler { loader, request ->
            val data = request.data.toString()
            if (data.startsWith("file:///android_asset/") && data.endsWith(".svg")) {
                val svg = request.context.assets.open(data.removePrefix("file:///android_asset/")).source().buffer().use { Svg.Parser.DEFAULT.parse(it) }
                val bounds = svg.viewBox
                val w = (svg.width.takeIf { it > 0 } ?: bounds?.width ?: 32f).toInt().coerceAtLeast(1)
                val h = (svg.height.takeIf { it > 0 } ?: bounds?.height ?: 32f).toInt().coerceAtLeast(1)
                svg.width("100%")
                svg.height("100%")
                val image = svg.asImage(w * 3, h * 3)
                AsyncImagePainter.State.Success(image.asPainter(request.context), SuccessResult(image, request))
            } else AsyncImagePreviewHandler.Default.handle(loader, request)
        }
    }
    FillsaTheme(darkTheme = dark) {
        CompositionLocalProvider(IsDarkMode provides dark, StreakProvider provides MemberStreakResponse(100, false),
            LocalAsyncImagePreviewHandler provides assets) {
            Column(Modifier.fillMaxSize().background(FillsaTheme.colorScheme.background)) {
                if (route == null) content() else {
                    val nav = rememberNavController()
                    NavHost(nav, startDestination = route, modifier = Modifier.weight(1f)) {
                        composable(route) { content() }
                    }
                    BottomNavigationBar(isLogged = true, displayAd = false, displayBottomBar = true,
                        navController = nav, navigationEnabled = false)
                }
            }
        }
    }
}

@Composable
internal fun AuditHome(calendar: Boolean = false, recorded: Boolean = false, editing: Boolean = false,
    liked: Boolean = false, image: Boolean = false, tooltip: Boolean = false, quote: String = AuditHomeQuote) {
    val date = LocalDate.of(2026, 8, 16)
    FigmaHomeContent(date = date, quote = quote, author = "존우든", isQuoteLoading = false,
        selectedLocale = LocaleType.KOR, isLike = liked,
        backgroundImageUrl = if (image) "android.resource://${LocalContext.current.packageName}/${R.drawable.home_registered_image_fixture}" else "",
        weekDays = (10..16).map { HomeWeekDayState(date.withDayOfMonth(it), it == 16, it < 12) },
        question = AuditQuestion, isCalendarOpen = calendar, displayedMonth = YearMonth.of(2026, 8),
        calendarReferenceDate = date, isStreakTooltipOpen = tooltip,
        answerUiState = HomeAnswerUiState(draft = if (editing) AuditAnswer else "",
            recordedAnswer = if (recorded) AuditAnswer else null, isEditing = !recorded, dateKey = date),
        canGoNext = false, onLocaleChanged = {}, onHome = {}, onProfile = {}, onCalendar = {},
        onDismissCalendar = {}, onMonthChanged = {}, onDateSelected = {}, onWeekDaySelected = {},
        onStreakStatus = {}, onDismissStreakTooltip = {}, onStreakCalendar = {}, onQuote = {},
        onAnswerChanged = {}, onRecordAnswer = {}, onEditAnswer = {}, onAuthor = {},
        onPreviousQuote = {}, onNextQuote = {}, onCopy = {}, onShare = {}, onLike = {}, onImage = {})
}

@Composable
internal fun AuditCalendar(state: CalendarRuntimeQaState) {
    val fixture = CalendarRuntimeQaFixture(state)
    val data = fixture.data
    val quote = data.memberQuotes.firstOrNull { it.quoteDate == fixture.selectedDay.date.toString() }
    val presentation = fixture.selectedDayPresentation("android.resource://${LocalContext.current.packageName}/${R.drawable.home_registered_image_fixture}")
    CalendarContent(data, fixture.selectedDay, quote, presentation,
        HomeAnswerUiState(recordedAnswer = presentation.answer.takeIf { it.isNotBlank() },
            isEditing = !presentation.hasRecordedAnswer, dateKey = fixture.selectedDay.date), {}, {}, fixture)
}

@Composable
internal fun AuditTyping(dark: Boolean, written: Boolean = false) {
    TypingQuoteContent(DailyQuoteDto(AuditQuote, "존 우든"),
        TextFieldValue(if (written) AuditQuote else ""), TextFieldValue(""), LocaleType.KOR, false, dark,
        {}, {}, {}, {}, {}, {}, {})
}

@Composable
internal fun AuditNotice(empty: Boolean, title: String, date: String, content: String) {
    val flow = remember { MutableStateFlow(PagingData.from(if (empty) emptyList() else
        listOf(NoticeResponse(1, title, content, date)))) }
    NoticeContent(flow.collectAsLazyPagingItems(), {})
}

