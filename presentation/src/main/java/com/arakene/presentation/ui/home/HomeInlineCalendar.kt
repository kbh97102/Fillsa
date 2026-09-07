package com.arakene.presentation.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
    onMonthChanged: (YearMonth) -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val today = DateCondition.currentDay()
    val days = homeMonthGrid(displayedMonth, selectedDate, today)
    Column(
        modifier = modifier
            .width(248.dp)
            .height(335.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFFFFDFC))
            .border(1.dp, Color(0xFFE7E1D6), RoundedCornerShape(10.dp))
            .padding(horizontal = 13.dp, vertical = 11.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(37.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            HomeCalendarArrow("‹", enabled = displayedMonth > DateCondition.startMonth) {
                onMonthChanged(displayedMonth.minusMonths(1))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                HomeCalendarSelector(displayedMonth.year.toString())
                HomeCalendarSelector(displayedMonth.monthValue.toString().padStart(2, '0'))
            }
            HomeCalendarArrow("›", enabled = displayedMonth < YearMonth.from(today)) {
                onMonthChanged(displayedMonth.plusMonths(1))
            }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            listOf("일", "월", "화", "수", "목", "금", "토").forEach { day ->
                Text(
                    text = day,
                    style = FillsaTheme.typography.body4,
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
                                .noEffectClickable(enable = day.isInDisplayedMonth && isHomeCalendarDateSelectable(day.date, today)) {
                                    onDateSelected(day.date)
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = day.date.dayOfMonth.toString(),
                                style = FillsaTheme.typography.body4,
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
private fun HomeCalendarArrow(label: String, enabled: Boolean, onClick: () -> Unit) {
    Text(
        text = label,
        style = FillsaTheme.typography.subtitle1,
        color = if (enabled) Color(0xFF212121) else Color(0xFFBDBDBD),
        textAlign = TextAlign.Center,
        modifier = Modifier.size(28.dp).noEffectClickable(enable = enabled, click = onClick),
    )
}

@Composable
private fun HomeCalendarSelector(value: String) {
    Row(
        modifier = Modifier
            .height(31.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE7E1D6), RoundedCornerShape(7.dp))
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(value, style = FillsaTheme.typography.body4, color = Color(0xFF212121))
        Text("⌄", style = FillsaTheme.typography.body4, color = Color(0xFF77736D), modifier = Modifier.padding(start = 4.dp))
    }
}
