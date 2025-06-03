package com.arakene.presentation.ui.calendar

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.toKoreanShort
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CalendarView(

) {
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(500) }
    val endMonth = remember { currentMonth.plusMonths(500) }
    var selection by remember { mutableStateOf<CalendarDay?>(null) }
    val daysOfWeek = remember { daysOfWeek() }

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
    )

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 20.dp)
    ) {

        Column(
            modifier = Modifier
                .padding(top = 20.dp)
                .background(
                    MaterialTheme.colorScheme.secondary,
                    shape = MaterialTheme.shapes.medium
                )
                .border(
                    1.dp,
                    color = colorResource(R.color.yellow02),
                    shape = MaterialTheme.shapes.medium
                )
        ) {

            SimpleCalendarTitle(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .padding(horizontal = 16.dp),
                currentMonth = currentMonth,
                goToPrevious = {
                    scope.launch {
                        state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.previousMonth)
                    }
                },
                goToNext = {
                    scope.launch {
                        state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.nextMonth)
                    }
                },
            )

            HorizontalCalendar(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(top = 10.dp, bottom = 8.dp),
                state = state,
                dayContent = { day ->
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Day(
                            day = day,
                            isSelected = selection == day,
                            isMonthDate = day.position == DayPosition.MonthDate
                        ) { clicked ->
                            selection = clicked
                        }
                    }
                },
                monthHeader = {
                    MonthHeader(
                        modifier = Modifier.padding(vertical = 8.dp),
                        daysOfWeek = daysOfWeek,
                    )
                },
            )
        }

    }

}


@Composable
private fun CalendarNavigationIcon(
    painter: Painter,
    contentDescription: String,
    modifier: Modifier = Modifier,
    isHorizontal: Boolean = true,
    onClick: () -> Unit,
) = Box(
    modifier = modifier
        .clip(shape = CircleShape)
        .clickable(role = Role.Button, onClick = onClick),
) {
    val rotation by animateFloatAsState(
        targetValue = if (isHorizontal) 0f else 90f,
        label = "CalendarNavigationIconAnimation",
    )
    Icon(
        modifier = Modifier
            .padding(4.dp)
            .align(Alignment.Center)
            .rotate(rotation),
        painter = painter,
        contentDescription = contentDescription,
    )
}

@Composable
fun SimpleCalendarTitle(
    modifier: Modifier,
    currentMonth: YearMonth,
    isHorizontal: Boolean = true,
    goToPrevious: () -> Unit,
    goToNext: () -> Unit,
) {

    val converter = remember {
        DateTimeFormatter.ofPattern("yyyy. MM", Locale.KOREA)
    }

    val convertedDate by remember(currentMonth) {
        mutableStateOf(currentMonth.format(converter))
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CalendarNavigationIcon(
            modifier = Modifier.rotate(180f),
            painter = painterResource(R.drawable.icn_arror_purple),
            contentDescription = "Previous",
            onClick = goToPrevious,
            isHorizontal = isHorizontal,
        )
        Text(
            modifier = Modifier
                .weight(1f),
            text = convertedDate,
            style = FillsaTheme.typography.buttonLargeBold,
            textAlign = TextAlign.Center,
            color = colorResource(R.color.purple01)
        )
        CalendarNavigationIcon(
            painter = painterResource(R.drawable.icn_arror_purple),
            contentDescription = "Next",
            onClick = goToNext,
            isHorizontal = isHorizontal,
        )
    }
}

@Composable
private fun Day(
    day: CalendarDay,
    isSelected: Boolean = false,
    isMonthDate: Boolean = true,
    onClick: (CalendarDay) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .background(
                color = if (isSelected) colorResource(R.color.purple01) else Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 5.dp, vertical = 4.dp)
            .clickable(
                enabled = day.position == DayPosition.MonthDate,
                onClick = { onClick(day) },
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            modifier = Modifier,
            text = day.date.dayOfMonth.toString(),
            color = if (isMonthDate) {
                if (isSelected) {
                    Color.White
                } else {
                    colorResource(R.color.gray_700)
                }
            } else {
                colorResource(R.color.gray_400)
            },
            style = FillsaTheme.typography.buttonSmallNormal
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 3.dp)
        ) {
            Image(
                painterResource(R.drawable.icn_note_calendar),
                contentDescription = null,
                modifier = Modifier.size(12.dp)
            )
            Image(
                painterResource(R.drawable.icn_fill_heart),
                contentDescription = null,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

@Composable
private fun MonthHeader(
    modifier: Modifier = Modifier,
    daysOfWeek: List<DayOfWeek> = emptyList(),
) {
    Row(modifier.fillMaxWidth()) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = FillsaTheme.typography.buttonSmallBold,
                color = colorResource(R.color.gray_700),
                text = dayOfWeek.toKoreanShort(),
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun CalendarViewPreview() {
    FillsaTheme {
        CalendarView()
    }
}