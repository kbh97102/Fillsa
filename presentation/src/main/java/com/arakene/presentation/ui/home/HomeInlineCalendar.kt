package com.arakene.presentation.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arakene.presentation.R
import com.arakene.presentation.util.IsDarkMode
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.DateCondition
import com.arakene.presentation.util.noEffectClickable
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

internal data class HomeCalendarDay(
    val date: LocalDate,
    val isInDisplayedMonth: Boolean,
    val isSelected: Boolean,
    val isToday: Boolean,
)

internal fun isHomeCalendarDateSelectable(date: LocalDate, today: LocalDate = DateCondition.currentDay()): Boolean =
    date in DateCondition.startDay..today

internal fun isHomeCalendarNextMonthEnabled(displayedMonth: YearMonth, referenceDate: LocalDate): Boolean =
    displayedMonth < YearMonth.from(referenceDate)

/** Sunday-first grid containing all visible dates for a month. */
internal fun homeMonthGrid(
    month: YearMonth,
    selectedDate: LocalDate,
    today: LocalDate = DateCondition.currentDay(),
): List<HomeCalendarDay> {
    val first = month.atDay(1)
    val gridStart = first.minusDays((first.dayOfWeek.value % DayOfWeek.SUNDAY.value).toLong())
    val last = month.atEndOfMonth()
    val gridEnd = last.plusDays(((DayOfWeek.SATURDAY.value - last.dayOfWeek.value + 7) % 7).toLong())
    return generateSequence(gridStart) { date -> date.plusDays(1).takeIf { it <= gridEnd } }
        .map { date ->
            HomeCalendarDay(
                date = date,
                isInDisplayedMonth = YearMonth.from(date) == month,
                isSelected = date == selectedDate,
                isToday = date == today,
            )
        }
        .toList()
}

@Composable
internal fun HomeInlineCalendar(
    displayedMonth: YearMonth,
    selectedDate: LocalDate,
    referenceDate: LocalDate,
    onMonthChanged: (YearMonth) -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    darkMode: Boolean = IsDarkMode.current,
    modifier: Modifier = Modifier,
) {
    var isMonthPickerOpen by rememberSaveable { mutableStateOf(false) }
    val days = homeMonthGrid(displayedMonth, selectedDate, referenceDate)
    if (isMonthPickerOpen) {
        HomeYearMonthDialog(
            displayedMonth = displayedMonth,
            lastMonth = YearMonth.from(referenceDate),
            darkMode = darkMode,
            onConfirm = onMonthChanged,
            onDismiss = { isMonthPickerOpen = false },
        )
    }
    Column(
        modifier = modifier
            .width(248.dp)
            .height(335.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFFFFDFC))
            .border(1.dp, Color(0xFFE7E1D6), RoundedCornerShape(10.dp))
            // Consume empty-area taps so the sibling backdrop does not dismiss the calendar.
            .pointerInput(Unit) { detectTapGestures { } }
            .padding(12.dp),
    ) {
        HomeCalendarHeader(
            displayedMonth = displayedMonth,
            referenceDate = referenceDate,
            onMonthChanged = onMonthChanged,
            onSelectYearMonth = { isMonthPickerOpen = true },
        )
        Row(modifier = Modifier.fillMaxWidth().padding(top = if (darkMode) 16.dp else 8.dp)) {
            listOf("일", "월", "화", "수", "목", "금", "토").forEach { day ->
                Text(
                    text = day,
                    style = if (darkMode) FillsaTheme.typography.body3 else FillsaTheme.typography.body4,
                    color = Color(0xFF77736D),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        days.chunked(7).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth().padding(top = 6.dp)) {
                week.forEach { day ->
                    Box(
                        modifier = Modifier.weight(1f).height(34.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(31.dp)
                                .clip(RoundedCornerShape(99.dp))
                                .background(
                                    when {
                                        day.isSelected -> Color(0xFF212121)
                                        day.isToday -> Color(0xFF5C65FF)
                                        else -> Color.Transparent
                                    }
                                )
                                .noEffectClickable(enable = day.isInDisplayedMonth && isHomeCalendarDateSelectable(day.date, referenceDate)) {
                                    onDateSelected(day.date)
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = day.date.dayOfMonth.toString(),
                                style = if (darkMode) FillsaTheme.typography.body3 else FillsaTheme.typography.body4,
                                color = when {
                                    day.isSelected || day.isToday -> Color.White
                                    day.isInDisplayedMonth -> Color(0xFF212121)
                                    else -> Color(0xFF9E9E9E)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeCalendarHeader(
    displayedMonth: YearMonth,
    referenceDate: LocalDate,
    onMonthChanged: (YearMonth) -> Unit,
    onSelectYearMonth: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(32.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HomeCalendarArrow(R.drawable.home_calendar_chevron_left, "이전 달",
            enabled = displayedMonth > DateCondition.startMonth) {
            onMonthChanged(displayedMonth.minusMonths(1))
        }
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HomeCalendarSelector(displayedMonth.year.toString(), 65.dp, TextAlign.Center, onSelectYearMonth)
            HomeCalendarSelector(displayedMonth.monthValue.toString().padStart(2, '0'), 63.dp, TextAlign.Start, onSelectYearMonth)
        }
        HomeCalendarArrow(R.drawable.home_calendar_chevron_right, "다음 달",
            enabled = isHomeCalendarNextMonthEnabled(displayedMonth, referenceDate)) {
            onMonthChanged(displayedMonth.plusMonths(1))
        }
    }
}

@Composable
private fun HomeCalendarArrow(icon: Int, description: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier.size(32.dp)
            .noEffectClickable(enable = enabled, click = onClick)
            .semantics { role = Role.Button },
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = description,
            modifier = Modifier.size(16.dp).alpha(if (enabled) 1f else 0.3f),
        )
    }
}

@Composable
private fun HomeCalendarSelector(value: String, width: Dp, textAlign: TextAlign, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .width(width)
            .height(32.dp)
            .shadow(1.dp, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE5E5E5), RoundedCornerShape(8.dp))
            .noEffectClickable(click = onClick)
            .semantics { role = Role.Button }
            .padding(start = 8.dp, end = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = value,
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.inter_variable, FontWeight.Medium)),
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = false),
            ),
            color = Color(0xFF0A0A0A),
            textAlign = textAlign,
            maxLines = 1,
            modifier = Modifier.weight(1f),
        )
        Image(
            painter = painterResource(R.drawable.home_calendar_chevron_down),
            contentDescription = null,
            modifier = Modifier.size(12.dp),
        )
    }
}

@Preview(widthDp = 224, heightDp = 36, locale = "ko", fontScale = 1f, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO, showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun HomeCalendarHeaderLightPreview() {
    FillsaTheme {
        HomeCalendarHeader(YearMonth.of(2026, 8), LocalDate.of(2026, 10, 25), {}, {})
    }
}

@Preview(widthDp = 224, heightDp = 36, locale = "ko", fontScale = 1f, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun HomeCalendarHeaderDarkPreview() {
    FillsaTheme(darkTheme = true) {
        HomeCalendarHeader(YearMonth.of(2026, 8), LocalDate.of(2026, 10, 25), {}, {})
    }
}

@Preview(widthDp = 248, heightDp = 335, locale = "ko", fontScale = 1f, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun HomeInlineCalendarLightPreview() {
    FillsaTheme { HomeInlineCalendar(YearMonth.of(2026, 8), LocalDate.of(2026, 8, 16), LocalDate.of(2026, 8, 16), {}, {}, darkMode = false) }
}

@Preview(widthDp = 248, heightDp = 335, locale = "ko", fontScale = 1f, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeInlineCalendarDarkPreview() {
    FillsaTheme(darkTheme = true) { HomeInlineCalendar(YearMonth.of(2026, 8), LocalDate.of(2026, 8, 16), LocalDate.of(2026, 8, 16), {}, {}, darkMode = true) }
}
