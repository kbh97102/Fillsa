package com.arakene.presentation.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arakene.domain.responses.MemberQuotesData
import com.arakene.domain.util.YN
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.ui.theme.pretendard
import com.arakene.presentation.util.DateCondition
import com.arakene.presentation.util.IsDarkMode
import com.arakene.presentation.util.noEffectClickable
import com.arakene.presentation.util.toKoreanShort
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

internal data class CalendarRecordIndicators(val showFire: Boolean, val showHeart: Boolean)

internal data class CalendarQaDayCell(
    val label: String,
    val monthDate: Boolean = true,
    val selected: Boolean = false,
    val indicators: CalendarRecordIndicators = CalendarRecordIndicators(false, false),
)

private data class CalendarDayUi(
    val day: CalendarDay,
    val quoteData: MemberQuotesData?,
    val labelOverride: String? = null,
    val indicatorsOverride: CalendarRecordIndicators? = null,
    val selected: Boolean,
    val monthDate: Boolean,
)

internal fun calendarWeekdayOrder(): List<DayOfWeek> = listOf(
    DayOfWeek.SUNDAY,
    DayOfWeek.MONDAY,
    DayOfWeek.TUESDAY,
    DayOfWeek.WEDNESDAY,
    DayOfWeek.THURSDAY,
    DayOfWeek.FRIDAY,
    DayOfWeek.SATURDAY,
)

internal fun calendarRecordIndicators(quoteData: MemberQuotesData?) = CalendarRecordIndicators(
    showFire = quoteData?.let { it.completed || it.todayCompleted } == true,
    showHeart = quoteData?.likeYn == YN.Y,
)

/** Figma expands the entire selected/record-bearing week, not just an individual date. */
internal fun calendarWeekUsesExpandedCells(
    hasSelectedCell: Boolean,
    hasRecordIndicator: Boolean,
): Boolean = hasSelectedCell || hasRecordIndicator

/**
 * Fixed Figma shell: 8 + 30 + 10 + 40 + (6 * 50) + 8 = 396dp.
 * CalendarViewModel continues to own month bounds and selection; only layout is local.
 */
@Composable
internal fun CalendarSection(
    memberQuotes: List<MemberQuotesData>,
    changeMonth: (YearMonth) -> Unit,
    selectDay: (CalendarDay) -> Unit,
    selectedDay: CalendarDay,
    qaState: CalendarRuntimeQaState? = null,
    modifier: Modifier = Modifier,
    darkMode: Boolean = IsDarkMode.current,
) {
    val currentMonth = YearMonth.from(selectedDay.date)
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE
    val today = LocalDate.now()
    val startDay = DateCondition.startDay

    Column(
        modifier = modifier
            .height(396.dp)
            .background(
                if (darkMode) Color(0xFF424242) else Color.White.copy(alpha = 0.5f),
                RoundedCornerShape(12.dp),
            )
            .border(1.dp, if (darkMode) Color(0xFF616161) else Color(0xFFFFCB5C), RoundedCornerShape(12.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(8.dp))
        SimpleCalendarTitle(
            modifier = Modifier.height(30.dp),
            currentMonth = currentMonth,
            goToPrevious = { changeMonth(currentMonth.minusMonths(1)) },
            goToNext = { changeMonth(currentMonth.plusMonths(1)) },
            darkMode = darkMode,
        )
        Spacer(Modifier.height(10.dp))
        MonthHeader()
        val cells = qaState?.let(::calendarQaGridCells)?.map { cell ->
            CalendarDayUi(
                day = selectedDay,
                quoteData = null,
                labelOverride = cell.label,
                indicatorsOverride = cell.indicators,
                selected = cell.selected,
                monthDate = cell.monthDate,
            )
        } ?: calendarGridDays(currentMonth).map { day ->
            CalendarDayUi(
                day = day,
                quoteData = memberQuotes.firstOrNull { it.quoteDate == formatter.format(day.date) },
                selected = selectedDay.date == day.date,
                monthDate = day.position == DayPosition.MonthDate && day.date in startDay..today,
            )
        }
        cells.chunked(7).forEach { week ->
            val usesExpandedCells = calendarWeekUsesExpandedCells(
                hasSelectedCell = week.any { it.selected },
                hasRecordIndicator = week.any { cell ->
                    val indicators = cell.indicatorsOverride ?: calendarRecordIndicators(cell.quoteData)
                    indicators.showFire || indicators.showHeart
                },
            )
            Row(
                modifier = Modifier.height(50.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(11.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                week.forEach { cell ->
                    Day(
                        day = cell.day,
                        quoteData = cell.quoteData,
                        labelOverride = cell.labelOverride,
                        indicatorsOverride = cell.indicatorsOverride,
                        usesExpandedCellGeometry = usesExpandedCells,
                        isSelected = cell.selected,
                        isMonthDate = cell.monthDate,
                        darkMode = darkMode,
                        onClick = selectDay,
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

internal fun calendarQaGridCells(state: CalendarRuntimeQaState): List<CalendarQaDayCell> {
    val inactive = listOf("27", "28", "29", "30", "31").map {
        CalendarQaDayCell(label = it, monthDate = false)
    }
    val firstHalf = inactive + (1..16).map { CalendarQaDayCell(it.toString()) }
    val fire = CalendarRecordIndicators(showFire = true, showHeart = false)
    val heartFire = CalendarRecordIndicators(showFire = true, showHeart = true)
    val focusWeek = when (state) {
        CalendarRuntimeQaState.Basic -> listOf(
            CalendarQaDayCell("17", selected = true),
            CalendarQaDayCell("17"),
            CalendarQaDayCell("18", indicators = fire),
            CalendarQaDayCell("19", indicators = heartFire),
            CalendarQaDayCell("20", indicators = heartFire),
            CalendarQaDayCell("22"),
            CalendarQaDayCell("23"),
        )
        CalendarRuntimeQaState.CompletedUnanswered,
        CalendarRuntimeQaState.CompletedAnsweredImage -> listOf(
            CalendarQaDayCell("17"),
            CalendarQaDayCell("18", indicators = heartFire),
            CalendarQaDayCell("19", indicators = heartFire),
            CalendarQaDayCell("20", indicators = heartFire),
            CalendarQaDayCell("21", selected = true, indicators = heartFire),
            CalendarQaDayCell("22"),
            CalendarQaDayCell("23"),
        )
    }
    val lastRows = (24..31).map { CalendarQaDayCell(it.toString()) } +
        List(6) { CalendarQaDayCell(label = "", monthDate = false) }
    return firstHalf + focusWeek + lastRows
}

internal fun calendarGridDays(month: YearMonth): List<CalendarDay> {
    val first = month.atDay(1)
    val firstOffset = first.dayOfWeek.value % 7
    return (0 until 42).map { index ->
        val date = first.minusDays(firstOffset.toLong()).plusDays(index.toLong())
        CalendarDay(
            date = date,
            position = when {
                date.year == month.year && date.month == month.month -> DayPosition.MonthDate
                date.isBefore(first) -> DayPosition.InDate
                else -> DayPosition.OutDate
            },
        )
    }
}

@Composable
fun SimpleCalendarTitle(
    modifier: Modifier,
    currentMonth: YearMonth,
    goToPrevious: () -> Unit,
    goToNext: () -> Unit,
    darkMode: Boolean = IsDarkMode.current,
) {
    val converter = DateTimeFormatter.ofPattern("yyyy. MM", Locale.KOREA)
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(70.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CalendarNavigationArrow(
            previous = true,
            enabled = currentMonth > DateCondition.startMonth,
            contentDescription = "이전 달",
            onClick = goToPrevious,
            darkMode = darkMode,
        )
        Text(
            text = currentMonth.format(converter),
            modifier = Modifier.size(width = 100.dp, height = 30.dp),
            fontFamily = pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            lineHeight = 30.sp,
            textAlign = TextAlign.Center,
            color = colorResource(R.color.purple01),
        )
        CalendarNavigationArrow(
            previous = false,
            enabled = currentMonth < YearMonth.now(),
            contentDescription = "다음 달",
            onClick = goToNext,
            darkMode = darkMode,
        )
    }
}

@Composable
private fun CalendarNavigationArrow(
    previous: Boolean,
    enabled: Boolean,
    contentDescription: String,
    onClick: () -> Unit,
    darkMode: Boolean,
) {
    Box(
        modifier = Modifier.size(24.dp).semantics { this.contentDescription = contentDescription }
            .noEffectClickable(enable = enabled, click = onClick),
        contentAlignment = Alignment.Center,
    ) {
        CalendarFigmaAsset(
            fileName = "calendar_arrow.svg",
            modifier = Modifier.size(24.dp).rotate(if (previous) 180f else 0f),
            darkMode = darkMode,
        )
    }
}

@Composable
internal fun Day(
    day: CalendarDay,
    quoteData: MemberQuotesData?,
    labelOverride: String? = null,
    indicatorsOverride: CalendarRecordIndicators? = null,
    usesExpandedCellGeometry: Boolean = false,
    isSelected: Boolean = false,
    isMonthDate: Boolean = true,
    darkMode: Boolean = IsDarkMode.current,
    onClick: (CalendarDay) -> Unit = {},
) {
    val indicators = indicatorsOverride ?: calendarRecordIndicators(quoteData)
    val cellHeight = if (usesExpandedCellGeometry) 50.dp else 40.dp
    val dateLabelOffset = if (usesExpandedCellGeometry) 3.dp else 2.dp
    Column(
        modifier = Modifier
            .size(width = 36.dp, height = cellHeight)
            .background(if (isSelected) colorResource(R.color.purple01) else Color.Transparent, RoundedCornerShape(10.dp))
            .noEffectClickable(enable = isMonthDate) { onClick(day) },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.height(24.dp).offset(y = dateLabelOffset),
            text = labelOverride ?: day.date.dayOfMonth.toString(),
            color = when {
                isSelected -> Color.White
                isMonthDate -> FillsaTheme.colorScheme.onBackground1
                else -> Color(0xFF9E9E9E)
            },
            fontFamily = pretendard,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
        )
        Row(
            modifier = Modifier.height(14.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (indicators.showHeart) CalendarFigmaAsset("calendar_record_heart.svg", Modifier.size(12.dp), darkMode)
            if (indicators.showFire) CalendarFigmaAsset("calendar_record_fire.svg", Modifier.size(12.dp), darkMode)
        }
    }
}

@Composable
fun MonthHeader(
    modifier: Modifier = Modifier,
    daysOfWeek: List<DayOfWeek> = calendarWeekdayOrder(),
) {
    Row(
        modifier = modifier.height(40.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(11.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        daysOfWeek.forEach { day ->
            Text(
                modifier = Modifier.size(width = 36.dp, height = 40.dp).offset(y = 8.dp),
                textAlign = TextAlign.Center,
                text = day.toKoreanShort(),
                fontFamily = pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = if (IsDarkMode.current) Color.White else Color(0xFF212121),
            )
        }
    }
}

@Composable
fun CalendarNavigationIcon(
    painter: Painter,
    contentDescription: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(modifier = modifier.noEffectClickable(click = onClick), contentAlignment = Alignment.Center) {
        Image(painter = painter, contentDescription = contentDescription)
    }
}
