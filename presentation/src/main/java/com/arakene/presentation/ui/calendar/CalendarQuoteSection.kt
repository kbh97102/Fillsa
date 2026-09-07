package com.arakene.presentation.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arakene.domain.responses.MemberQuotesData
import com.arakene.domain.util.YN
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.IsDarkMode
import com.arakene.presentation.util.homeAnswerInputState
import com.arakene.presentation.util.noEffectClickable
import com.kizitonwose.calendar.core.CalendarDay
import coil3.compose.AsyncImage
import java.time.format.TextStyle
import java.util.Locale

internal data class CalendarDetailContent(
    val showEmptyMessage: Boolean,
    val showQuoteCard: Boolean,
    val quoteCardHeightDp: Int,
)

internal fun calendarDetailContent(
    presentation: CalendarSelectedDayPresentation,
): CalendarDetailContent = if (!presentation.completed) {
    CalendarDetailContent(
        showEmptyMessage = true,
        showQuoteCard = true,
        quoteCardHeightDp = 80,
    )
} else {
    CalendarDetailContent(
        showEmptyMessage = false,
        showQuoteCard = false,
        quoteCardHeightDp = 0,
    )
}

@Composable
internal fun CalendarQuoteSection(
    quoteData: MemberQuotesData?,
    selectedDay: CalendarDay,
    presentation: CalendarSelectedDayPresentation,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onLike: () -> Unit,
    onImage: () -> Unit,
    onOpenQuote: () -> Unit,
    modifier: Modifier = Modifier,
    darkMode: Boolean = IsDarkMode.current,
) {
    val detailContent = calendarDetailContent(presentation)
    Column(modifier = modifier.fillMaxWidth()) {
        if (detailContent.showEmptyMessage) {
            CalendarEmptyDay(darkMode)
            if (detailContent.showQuoteCard) {
                CalendarQuotePreviewCard(
                    quoteData = quoteData,
                    selectedDay = presentation.quoteDateOverride?.let {
                        CalendarDay(it, selectedDay.position)
                    } ?: selectedDay,
                    quoteCardHeightDp = detailContent.quoteCardHeightDp,
                    onClick = onOpenQuote,
                    darkMode = darkMode,
                    modifier = Modifier.padding(top = 4.dp).offset(y = (-28).dp),
                )
            }
        } else {
            if (quoteData != null) {
                CalendarCompletedDayCard(
                    quoteData, selectedDay, presentation, onCopy, onShare, onLike, onImage, darkMode,
                )
            }
            CalendarPromptAnswer(
                presentation = presentation,
                onOpenQuote = onOpenQuote,
                darkMode = darkMode,
                modifier = Modifier.padding(top = 10.dp),
            )
        }
    }
}

@Composable
private fun CalendarEmptyDay(darkMode: Boolean) {
    Row(
        modifier = Modifier.height(100.dp).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CalendarFigmaAsset("calendar_empty_handwriting.svg", Modifier.size(100.dp), darkMode)
        Column {
            Text(
                "필사하지 않은 날이에요.",
                style = FillsaTheme.typography.body3,
                color = if (darkMode) Color.White else colorResource(R.color.purple01),
            )
            Text(
                "아래 텍스트를 선택하여 기록해주세요!",
                style = FillsaTheme.typography.body4,
                color = if (darkMode) Color.White else colorResource(R.color.purple01),
            )
        }
    }
}

@Composable
private fun CalendarQuotePreviewCard(
    quoteData: MemberQuotesData?,
    selectedDay: CalendarDay,
    quoteCardHeightDp: Int,
    onClick: () -> Unit,
    darkMode: Boolean,
    modifier: Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().height(quoteCardHeightDp.dp)
            .background(if (darkMode) Color(0xFF424242) else Color.White, RoundedCornerShape(10.dp))
            .border(1.dp, colorResource(R.color.purple01), RoundedCornerShape(10.dp))
            .noEffectClickable(click = onClick)
            .semantics { contentDescription = "선택한 날짜의 필사 문장 열기" },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CalendarDateLabel(selectedDay, darkMode, Modifier.width(62.dp))
        Text(
            quoteData?.quote.orEmpty(),
            modifier = Modifier.weight(1f).padding(end = 10.dp),
            style = FillsaTheme.typography.body3,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            color = FillsaTheme.colorScheme.onBackground1,
        )
    }
}

@Composable
private fun CalendarCompletedDayCard(
    quoteData: MemberQuotesData,
    selectedDay: CalendarDay,
    presentation: CalendarSelectedDayPresentation,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onLike: () -> Unit,
    onImage: () -> Unit,
    darkMode: Boolean,
) {
    Column(
        modifier = Modifier.fillMaxWidth().height(133.dp)
            .background(if (darkMode) Color(0xFF424242) else Color.White, RoundedCornerShape(10.dp))
            .border(if (darkMode) 1.dp else 0.dp, if (darkMode) Color(0xFF616161) else Color.Transparent, RoundedCornerShape(10.dp)),
    ) {
        Row(Modifier.height(90.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            CalendarDateLabel(selectedDay, darkMode, Modifier.width(62.dp))
            Text(
                quoteData.quote,
                modifier = Modifier.weight(1f).padding(end = 10.dp),
                style = FillsaTheme.typography.body3,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = FillsaTheme.colorScheme.onBackground1,
            )
        }
        Box(Modifier.height(1.dp).fillMaxWidth().background(if (darkMode) Color(0x8C616161) else Color(0x3D9D8961)))
        CalendarActionRow(
            liked = quoteData.likeYn == YN.Y,
            registeredImageUri = presentation.registeredImageUri,
            onCopy = onCopy,
            onShare = onShare,
            onLike = onLike,
            onImage = onImage,
            darkMode = darkMode,
        )
    }
}

@Composable
private fun CalendarDateLabel(selectedDay: CalendarDay, darkMode: Boolean, modifier: Modifier) {
    val weekday = remember(selectedDay.date) {
        "(" + selectedDay.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREA) + ")"
    }
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            selectedDay.date.dayOfMonth.toString(),
            style = FillsaTheme.typography.heading4,
            color = if (darkMode) Color.White else colorResource(R.color.purple01),
        )
        Text(
            weekday,
            style = FillsaTheme.typography.body4,
            color = if (darkMode) Color.White else colorResource(R.color.purple01),
        )
    }
}

@Composable
private fun CalendarActionRow(
    liked: Boolean,
    registeredImageUri: String?,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onLike: () -> Unit,
    onImage: () -> Unit,
    darkMode: Boolean,
) {
    Row(
        modifier = Modifier.height(42.dp).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        CalendarAction("home_copy.svg", "복사", Modifier.width(70.dp), onCopy, darkMode)
        CalendarActionDivider(darkMode)
        CalendarAction("home_share.svg", "공유", Modifier.width(70.dp), onShare, darkMode)
        CalendarActionDivider(darkMode)
        CalendarLikeAction(liked, Modifier.width(70.dp), onLike, darkMode)
        CalendarActionDivider(darkMode)
        CalendarImageAction(registeredImageUri, Modifier.width(107.dp), onImage, darkMode)
    }
}

@Composable
private fun CalendarLikeAction(
    liked: Boolean,
    modifier: Modifier,
    onClick: () -> Unit,
    darkMode: Boolean,
) {
    val selectedColor = colorResource(R.color.purple01)
    Row(
        modifier = modifier.height(42.dp).noEffectClickable(click = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        CalendarFigmaAsset(
            fileName = if (liked) "calendar_record_heart.svg" else "home_like.svg",
            modifier = Modifier.size(16.dp),
            darkMode = darkMode,
            assetSet = if (liked) "calendar" else "home",
            tint = if (liked) selectedColor else null,
        )
        Text(
            "좋아요",
            style = FillsaTheme.typography.body4,
            color = if (liked) selectedColor else if (darkMode) Color(0xFFE0E0E0) else Color(0xFF6B6255),
            modifier = Modifier.padding(start = 4.dp),
        )
    }
}

@Composable
private fun CalendarImageAction(
    registeredImageUri: String?,
    modifier: Modifier,
    onClick: () -> Unit,
    darkMode: Boolean,
) {
    val registered = !registeredImageUri.isNullOrBlank()
    val selectedColor = colorResource(R.color.purple01)
    Row(
        modifier = modifier.height(42.dp).noEffectClickable(click = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (registered) {
            AsyncImage(
                model = registeredImageUri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(28.dp).clip(RoundedCornerShape(5.dp)),
            )
        } else {
            CalendarFigmaAsset("home_camera.svg", Modifier.size(16.dp), darkMode, assetSet = "home")
        }
        Text(
            if (registered) "이미지 보기" else "이미지 등록",
            style = if (registered) FillsaTheme.typography.buttonXSmallBold else FillsaTheme.typography.body4,
            color = if (registered) selectedColor else if (darkMode) Color(0xFFE0E0E0) else Color(0xFF6B6255),
            modifier = Modifier.padding(start = 4.dp),
        )
    }
}

@Composable
private fun CalendarAction(
    asset: String,
    label: String,
    modifier: Modifier,
    onClick: () -> Unit,
    darkMode: Boolean,
) {
    Row(
        modifier = modifier.height(42.dp).noEffectClickable(click = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        CalendarFigmaAsset(asset, Modifier.size(16.dp), darkMode, assetSet = "home")
        Text(
            label,
            style = FillsaTheme.typography.body4,
            color = if (darkMode) Color(0xFFE0E0E0) else Color(0xFF6B6255),
            modifier = Modifier.padding(start = 4.dp),
        )
    }
}

@Composable
private fun CalendarActionDivider(darkMode: Boolean) {
    Box(Modifier.width(1.dp).height(28.dp).background(if (darkMode) Color(0x8C616161) else Color(0x409D8961)))
}

@Composable
private fun CalendarPromptAnswer(
    presentation: CalendarSelectedDayPresentation,
    onOpenQuote: () -> Unit,
    darkMode: Boolean,
    modifier: Modifier,
) {
    var answer by rememberSaveable(presentation.answer) { mutableStateOf(presentation.answer) }
    val state = homeAnswerInputState(answer)
    val hasRecordedAnswer = presentation.hasRecordedAnswer
    val primary = if (darkMode) Color.White else Color(0xFF211F1B)
    val muted = Color(0xFF9E9E9E)
    Column(modifier = modifier) {
        Text("오늘의 질문", style = FillsaTheme.typography.subtitle2, color = colorResource(R.color.purple01))
        // Calendar has no question/answer data contract, so it retains the established Home placeholder.
        Text(
            "누군가의 호의를 한참 뒤에야 받아들인 적 있나요?",
            modifier = Modifier.padding(top = 4.dp),
            style = FillsaTheme.typography.body3,
            color = primary,
        )
        Box(
            modifier = Modifier.padding(top = 11.dp).fillMaxWidth().height(174.dp)
                .background(if (darkMode) Color(0xFF424242) else Color.White.copy(alpha = .5f), RoundedCornerShape(17.dp))
                .border(1.dp, if (darkMode) Color(0xFF616161) else Color(0xFFDED4BD), RoundedCornerShape(17.dp)),
        ) {
            BasicTextField(
                value = state.text,
                onValueChange = { answer = homeAnswerInputState(it).text },
                textStyle = FillsaTheme.typography.body4.copy(color = primary),
                modifier = Modifier.fillMaxWidth().height(174.dp).padding(11.dp)
                    .semantics { contentDescription = "오늘의 답변 입력" },
                decorationBox = { field ->
                    if (state.text.isEmpty()) {
                        Text(
                            "오늘의 질문을 보고 떠오른 생각을 자유롭게 기록해보세요.",
                            style = FillsaTheme.typography.body4,
                            color = muted,
                        )
                    }
                    field()
                },
            )
        }
        Text(
            (presentation.displayedCountOverride ?: (200 - state.remainingCount)).toString() + " / 200",
            modifier = Modifier.fillMaxWidth().padding(top = 3.dp),
            style = FillsaTheme.typography.body4,
            textAlign = TextAlign.End,
            color = muted,
        )
        Row(
            modifier = Modifier.padding(top = 10.dp).fillMaxWidth().height(50.dp)
                .background(
                    if (hasRecordedAnswer) Color(0xFFD3D5FF) else colorResource(R.color.purple01),
                    RoundedCornerShape(8.dp),
                )
                .noEffectClickable(click = onOpenQuote)
                .semantics {
                    contentDescription = if (hasRecordedAnswer) "내 답변 수정하기" else "내 답변 기록하기"
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            CalendarFigmaAsset(
                "home_write_answer.svg",
                Modifier.size(18.dp),
                assetSet = "home",
                tint = if (hasRecordedAnswer) colorResource(R.color.purple01) else null,
            )
            Text(
                if (hasRecordedAnswer) "내 답변 수정하기" else "내 답변 기록하기",
                style = FillsaTheme.typography.buttonMediumBold,
                color = if (hasRecordedAnswer) colorResource(R.color.purple01) else Color.White,
                modifier = Modifier.padding(start = 4.dp),
            )
        }
    }
}
