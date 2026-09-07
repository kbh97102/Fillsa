package com.arakene.presentation.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.svg.SvgDecoder
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.ui.theme.gangwoneduall
import com.arakene.presentation.util.IsDarkMode
import com.arakene.presentation.util.LocaleType
import com.arakene.presentation.util.StreakProvider
import com.arakene.presentation.util.HomeAnswerMaxGraphemes
import com.arakene.presentation.util.HomeAnswerUiState
import com.arakene.presentation.util.getWikipediaUriString
import com.arakene.presentation.util.noEffectClickable
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlin.math.abs

private val HomePrimary = Color(0xFF5C65FF)

internal data class HomeColorPalette(
    val background: Color,
    val card: Color,
    val cardBorder: Color,
    val mainDivider: Color,
    val actionDivider: Color,
    val primaryText: Color,
    val actionLabel: Color,
    val mutedText: Color,
    val answerField: Color,
    val answerBorder: Color,
)

internal fun homeColorPalette(darkMode: Boolean): HomeColorPalette =
    if (darkMode) {
        HomeColorPalette(
            background = Color(0xFF212121),
            card = Color(0xFF424242),
            cardBorder = Color(0xFF616161),
            mainDivider = Color(0x8C616161),
            actionDivider = Color(0xFF616161),
            primaryText = Color.White,
            actionLabel = Color(0xFFE0E0E0),
            mutedText = Color(0xFF9E9E9E),
            answerField = Color(0xFF424242),
            answerBorder = Color(0xFF616161),
        )
    } else {
        HomeColorPalette(
            background = Color(0xFFFFEFCC),
            card = Color(0xFFFFF7E6),
            cardBorder = Color.Transparent,
            mainDivider = Color(0x3D9D8961),
            actionDivider = Color(0x409D8961),
            primaryText = Color(0xFF212121),
            actionLabel = Color(0xFF565149),
            mutedText = Color(0xFF9E9E9E),
            answerField = Color(0x80FFFFFF),
            answerBorder = Color(0xFFDED4BD),
        )
    }

internal enum class HomeLikeIcon {
    Selected,
    Unselected,
}

internal fun homeLikeIcon(isLiked: Boolean): HomeLikeIcon =
    if (isLiked) HomeLikeIcon.Selected else HomeLikeIcon.Unselected

internal data class HomeWeekDayState(
    val date: LocalDate,
    val isSelected: Boolean,
    val isCompleted: Boolean,
)

internal fun homeWeekDayStates(
    selectedDate: LocalDate,
    completedDates: Set<LocalDate>,
): List<HomeWeekDayState> =
    (-6L..0L).map { offset ->
        val day = selectedDate.plusDays(offset)
        HomeWeekDayState(
            date = day,
            isSelected = day == selectedDate,
            isCompleted = day in completedDates,
        )
    }

internal enum class HomeQuoteSwipe {
    Previous,
    Next,
    None,
}

internal fun homeQuoteSwipe(
    horizontalDrag: Float,
    canGoNext: Boolean,
): HomeQuoteSwipe =
    when {
        horizontalDrag > 150f -> HomeQuoteSwipe.Previous
        horizontalDrag < -150f && canGoNext -> HomeQuoteSwipe.Next
        else -> HomeQuoteSwipe.None
    }

internal fun homeAuthorUri(author: String): String = getWikipediaUriString(author)

@Composable
internal fun FigmaHomeContent(
    date: LocalDate,
    quote: String,
    author: String,
    selectedLocale: LocaleType,
    isLike: Boolean,
    completedDates: Set<LocalDate>,
    isCalendarOpen: Boolean,
    displayedMonth: YearMonth,
    isStreakTooltipOpen: Boolean,
    answerUiState: HomeAnswerUiState,
    canGoNext: Boolean,
    onLocaleChanged: (LocaleType) -> Unit,
    onHome: () -> Unit,
    onProfile: () -> Unit,
    onCalendar: () -> Unit,
    onDismissCalendar: () -> Unit,
    onMonthChanged: (YearMonth) -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onStreakStatus: () -> Unit,
    onDismissStreakTooltip: () -> Unit,
    onStreakCalendar: () -> Unit,
    onQuote: () -> Unit,
    onAnswerChanged: (String) -> Unit,
    onRecordAnswer: () -> Unit,
    onEditAnswer: () -> Unit,
    onAuthor: () -> Unit,
    onPreviousQuote: () -> Unit,
    onNextQuote: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onLike: () -> Unit,
    onImage: () -> Unit,
    modifier: Modifier = Modifier,
    darkMode: Boolean = IsDarkMode.current,
) {
    val palette = homeColorPalette(darkMode)
    val streak = StreakProvider.current?.currentStreak ?: 0
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
    ) {
        Column {
            HomeHeaderSection(
                streak = streak,
                onHome = onHome,
                onProfile = onProfile,
                onStreakStatus = onStreakStatus,
                palette = palette,
                darkMode = darkMode,
            )
            HomeDateWeekSection(
                date = date,
                completedDates = completedDates,
                isCalendarOpen = isCalendarOpen,
                onCalendar = onCalendar,
                palette = palette,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("아래 글을 필사해주세요.", style = FillsaTheme.typography.body3, color = palette.primaryText)
                FigmaLocaleToggle(selectedLocale, onLocaleChanged)
            }

            HomeQuoteCard(
                quote = quote,
                author = author,
                canGoNext = canGoNext,
                onQuote = onQuote,
                onAuthor = onAuthor,
                onPreviousQuote = onPreviousQuote,
                onNextQuote = onNextQuote,
                palette = palette,
                darkMode = darkMode,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            )

            HomeQuoteActionRow(
                isLike = isLike,
                onCopy = onCopy,
                onShare = onShare,
                onLike = onLike,
                onImage = onImage,
                palette = palette,
                darkMode = darkMode,
            )

            HomePromptAnswerSection(
                answerUiState = answerUiState,
                onAnswerChanged = onAnswerChanged,
                onRecordAnswer = onRecordAnswer,
                onEditAnswer = onEditAnswer,
                palette = palette,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, top = 15.dp, end = 20.dp),
            )
        }

        if (isCalendarOpen || isStreakTooltipOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(isCalendarOpen, isStreakTooltipOpen) {
                        detectTapGestures {
                            if (isCalendarOpen) onDismissCalendar()
                            if (isStreakTooltipOpen) onDismissStreakTooltip()
                        }
                    },
            )
        }

        if (isCalendarOpen) {
            HomeInlineCalendar(
                displayedMonth = displayedMonth,
                selectedDate = date,
                onMonthChanged = onMonthChanged,
                onDateSelected = onDateSelected,
                modifier = Modifier.padding(start = 20.dp, top = 96.dp),
            )
        }

        if (isStreakTooltipOpen && streak == 0) {
            HomeStreakTooltip(
                onCalendar = onStreakCalendar,
                modifier = Modifier.padding(start = 92.dp, top = 44.dp),
            )
        }
    }
}

@Composable
private fun HomeHeaderSection(
    streak: Int,
    onHome: () -> Unit,
    onProfile: () -> Unit,
    onStreakStatus: () -> Unit,
    palette: HomeColorPalette,
    darkMode: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FigmaAsset("home_logo.svg", Modifier.width(60.dp).height(27.dp).noEffectClickable(click = onHome), darkMode = darkMode)
        Spacer(Modifier.weight(1f))
        FigmaAsset(
            "home_streak.svg",
            Modifier.size(20.dp).noEffectClickable(enable = streak == 0, click = onStreakStatus),
        )
        Text(
            text = "${streak}일",
            style = FillsaTheme.typography.subtitle1,
            color = palette.primaryText,
            modifier = Modifier.padding(start = 2.dp),
        )
        FigmaAsset(
            "home_profile.svg",
            Modifier.padding(start = 12.dp).size(24.dp).noEffectClickable(click = onProfile),
            darkMode = darkMode,
        )
    }
}

@Composable
private fun HomeDateWeekSection(
    date: LocalDate,
    completedDates: Set<LocalDate>,
    isCalendarOpen: Boolean,
    onCalendar: () -> Unit,
    palette: HomeColorPalette,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        HomeMonthCalendar(date = date, isOpen = isCalendarOpen, onCalendar = onCalendar)
        HomeWeekStrip(date = date, completedDates = completedDates, palette = palette)
    }
}

@Composable
private fun HomeMonthCalendar(date: LocalDate, isOpen: Boolean, onCalendar: () -> Unit) {
    Row(
        modifier = Modifier
            .height(30.dp)
            .width(73.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(if (isOpen) Color(0xFFEEF0FF) else Color.White)
            .noEffectClickable(click = onCalendar),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        FigmaAsset("home_calendar_selected.svg", Modifier.size(12.dp))
        Text(
            date.format(DateTimeFormatter.ofPattern("yyyy.MM")),
            style = FillsaTheme.typography.buttonXSmallBold,
            color = Color(0xFF212121),
            modifier = Modifier.padding(start = 2.dp),
        )
    }
}

@Composable
private fun HomeWeekStrip(date: LocalDate, completedDates: Set<LocalDate>, palette: HomeColorPalette) {
    val days = homeWeekDayStates(selectedDate = date, completedDates = completedDates)
    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        days.forEach { day ->
            Box(
                modifier = Modifier
                    .size(30.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(99.dp))
                        .background(
                            when {
                                day.isSelected -> Color.White
                                day.isCompleted -> HomePrimary
                                else -> Color.Transparent
                            }
                        )
                        .border(
                            1.dp,
                            if (day.isSelected || day.isCompleted) HomePrimary else palette.mutedText,
                            RoundedCornerShape(99.dp),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        day.date.dayOfMonth.toString(),
                        style = FillsaTheme.typography.body4,
                        color = if (day.isSelected) Color(0xFF212121) else if (day.isCompleted) Color.White else palette.mutedText,
                    )
                }
                if (day.isCompleted && !day.isSelected) {
                    FigmaAsset(
                        "home_complete_badge.svg",
                        Modifier.size(18.dp).align(Alignment.TopCenter).offset(y = (-10).dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun FigmaLocaleToggle(
    selectedLocale: LocaleType,
    onLocaleChanged: (LocaleType) -> Unit,
) {
    val koreanSelected = selectedLocale == LocaleType.KOR
    Row(
        modifier = Modifier
            .width(60.dp)
            .height(28.dp)
            .clip(RoundedCornerShape(99.dp))
            .background(Color(0xFFFFCB5C))
            .noEffectClickable {
                onLocaleChanged(if (koreanSelected) LocaleType.ENG else LocaleType.KOR)
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "한",
            style = FillsaTheme.typography.buttonXSmallBold,
            color = Color(0xFF212121),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 3.dp)
                .clip(RoundedCornerShape(99.dp))
                .background(if (koreanSelected) Color.White else Color.Transparent),
        )
        Text(
            "A",
            style = FillsaTheme.typography.buttonXSmallBold,
            color = Color(0xFF212121),
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun HomeQuoteCard(
    quote: String,
    author: String,
    canGoNext: Boolean,
    onQuote: () -> Unit,
    onAuthor: () -> Unit,
    onPreviousQuote: () -> Unit,
    onNextQuote: () -> Unit,
    palette: HomeColorPalette,
    darkMode: Boolean,
    modifier: Modifier = Modifier,
) {
    var horizontalDrag by remember { mutableStateOf(0f) }
    Box(
        modifier = (if (darkMode) modifier else modifier.shadow(
            16.dp,
            RoundedCornerShape(14.dp),
            ambientColor = Color(0xB2CBC0A8),
            spotColor = Color.Transparent,
        ))
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(palette.card)
            .border(1.dp, palette.cardBorder, RoundedCornerShape(14.dp))
            .noEffectClickable(click = onQuote)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        when (homeQuoteSwipe(horizontalDrag, canGoNext)) {
                            HomeQuoteSwipe.Previous -> onPreviousQuote()
                            HomeQuoteSwipe.Next -> onNextQuote()
                            HomeQuoteSwipe.None -> Unit
                        }
                        horizontalDrag = 0f
                    },
                    onDrag = { _, dragAmount -> horizontalDrag += dragAmount.x },
                )
            },
    ) {
        FigmaAsset(
            "home_quote_wave.svg",
            Modifier.fillMaxWidth().height(161.dp).align(Alignment.Center),
            contentScale = ContentScale.FillBounds,
            darkMode = darkMode,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 10.dp, top = 25.dp, end = 10.dp, bottom = 13.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = quote,
                style = FillsaTheme.typography.quote.copy(fontFamily = gangwoneduall),
                color = palette.primaryText,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.weight(1f))
            Row(
                modifier = Modifier.noEffectClickable(click = onAuthor),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    author,
                    style = FillsaTheme.typography.body4,
                    color = palette.primaryText,
                    textDecoration = TextDecoration.Underline,
                )
                FigmaAsset("home_search.svg", Modifier.padding(start = 2.dp).size(16.dp), darkMode = darkMode)
            }
        }
    }
}

@Composable
private fun HomeQuoteActionRow(
    isLike: Boolean,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onLike: () -> Unit,
    onImage: () -> Unit,
    palette: HomeColorPalette,
    darkMode: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.height(42.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            HomeQuoteAction("home_copy.svg", "복사", Modifier.weight(1f), onCopy, palette, darkMode)
            HomeQuoteActionDivider(palette)
            HomeQuoteAction("home_share.svg", "공유", Modifier.weight(1f), onShare, palette, darkMode)
            HomeQuoteActionDivider(palette)
            HomeQuoteLikeAction(isLiked = isLike, modifier = Modifier.weight(1f), onClick = onLike, palette = palette, darkMode = darkMode)
            HomeQuoteActionDivider(palette)
            HomeQuoteAction("home_camera.svg", "이미지 등록", Modifier.weight(1.15f), onImage, palette, darkMode)
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(palette.mainDivider))
    }
}

@Composable
private fun HomeQuoteActionDivider(palette: HomeColorPalette) =
    Box(Modifier.width(1.dp).height(28.dp).background(palette.actionDivider))

@Composable
private fun HomeQuoteAction(
    asset: String,
    label: String,
    modifier: Modifier,
    onClick: () -> Unit,
    palette: HomeColorPalette,
    darkMode: Boolean,
) {
    Row(
        modifier = modifier
            .height(42.dp)
            .noEffectClickable(click = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        FigmaAsset(asset, Modifier.size(16.dp), darkMode = darkMode)
        Text(label, style = FillsaTheme.typography.body4, color = palette.actionLabel, modifier = Modifier.padding(start = 4.dp))
    }
}

@Composable
private fun HomeQuoteLikeAction(
    isLiked: Boolean,
    modifier: Modifier,
    onClick: () -> Unit,
    palette: HomeColorPalette,
    darkMode: Boolean,
) {
    Row(
        modifier = modifier
            .height(42.dp)
            .noEffectClickable(click = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        when (homeLikeIcon(isLiked)) {
            HomeLikeIcon.Selected -> Image(
                painter = painterResource(R.drawable.icn_fill_heart),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )

            HomeLikeIcon.Unselected -> FigmaAsset("home_like.svg", Modifier.size(16.dp), darkMode = darkMode)
        }
        Text("좋아요", style = FillsaTheme.typography.body4, color = palette.actionLabel, modifier = Modifier.padding(start = 4.dp))
    }
}

@Composable
private fun HomePromptAnswerSection(
    answerUiState: HomeAnswerUiState,
    onAnswerChanged: (String) -> Unit,
    onRecordAnswer: () -> Unit,
    onEditAnswer: () -> Unit,
    palette: HomeColorPalette,
    modifier: Modifier = Modifier,
) {
    val answerState = answerUiState.input

    Column(modifier = modifier) {
        Text("오늘의 질문", style = FillsaTheme.typography.subtitle2, color = HomePrimary)
        // The Home contract has no question feed, so this preserves the established Figma placeholder.
        Text(
            "누군가의 호의를 한참 뒤에야 받아들인 적 있나요?",
            style = FillsaTheme.typography.body3,
            color = palette.primaryText,
            modifier = Modifier.padding(top = 4.dp),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .height(174.dp)
                .clip(RoundedCornerShape(17.dp))
                .background(palette.answerField)
                .border(1.dp, palette.answerBorder, RoundedCornerShape(17.dp)),
        ) {
            BasicTextField(
                value = answerState.text,
                onValueChange = onAnswerChanged,
                readOnly = !answerUiState.isEditing,
                textStyle = FillsaTheme.typography.body4.copy(color = palette.primaryText),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(11.dp)
                    .semantics { contentDescription = "오늘의 답변 입력" },
                decorationBox = { innerTextField ->
                    if (answerState.text.isEmpty()) {
                        Text(
                            "오늘의 질문을 보고 떠오른 생각을 자유롭게 기록해보세요.",
                            style = FillsaTheme.typography.body4,
                            color = palette.mutedText,
                        )
                    }
                    innerTextField()
                },
            )
        }
        Text(
            "${HomeAnswerMaxGraphemes - answerState.remainingCount} / $HomeAnswerMaxGraphemes",
            style = FillsaTheme.typography.body4,
            color = palette.mutedText,
            modifier = Modifier.fillMaxWidth().padding(top = 3.dp),
            textAlign = TextAlign.End,
        )
        HomeAnswerRecordButton(
            isRecorded = answerUiState.isRecorded,
            onClick = if (answerUiState.isRecorded) onEditAnswer else onRecordAnswer,
        )
    }
}

@Composable
private fun HomeAnswerRecordButton(isRecorded: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(HomePrimary)
            .noEffectClickable(click = onClick)
            .semantics { contentDescription = "내 답변 기록하기" },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        FigmaAsset("home_write_answer.svg", Modifier.size(18.dp))
        Text(
            if (isRecorded) "내 답변 수정하기" else "내 답변 기록하기",
            style = FillsaTheme.typography.buttonMediumBold,
            color = Color.White,
            modifier = Modifier.padding(start = 4.dp),
        )
    }
}

@Composable
private fun HomeStreakTooltip(onCalendar: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .width(188.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF212121))
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Text("연속 필사를 완료해 주세요!", style = FillsaTheme.typography.body4, color = Color.White)
        Text(
            "나의 필사현황 보기",
            style = FillsaTheme.typography.body4,
            color = Color(0xFFFFCB5C),
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.padding(top = 4.dp).noEffectClickable(click = onCalendar),
        )
    }
}

@Composable
private fun FigmaAsset(
    fileName: String,
    modifier: Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    darkMode: Boolean = false,
) {
    val context = LocalContext.current
    AsyncImage(
        model = remember(fileName, darkMode) {
            ImageRequest.Builder(context)
                .data("file:///android_asset/figma/${if (darkMode) "home-night" else "home"}/$fileName")
                .decoderFactory(SvgDecoder.Factory())
                .build()
        },
        contentDescription = null,
        modifier = modifier,
        contentScale = contentScale,
    )
}
