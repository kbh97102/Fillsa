package com.arakene.presentation.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.arakene.presentation.R
import com.arakene.presentation.ui.calendar.CalendarNavigationIcon
import com.arakene.presentation.ui.calendar.MonthHeader
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.DateCondition
import com.arakene.presentation.util.action.QuoteListAction
import com.arakene.presentation.util.logDebug
import com.arakene.presentation.util.noEffectClickable
import com.kizitonwose.calendar.compose.ContentHeightMode
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale


@Composable
fun DurationCalendar(
    startDate: LocalDate,
    endDate: LocalDate,
    selectDate: (QuoteListAction.SelectDate) -> Unit,
    modifier: Modifier = Modifier
) {

    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    val firstVisibleMonth = remember {
        YearMonth.now()
    }

    val startMonth = remember(startDate) {
        YearMonth.of(2025, 6).apply {
            atDay(10)
        }
    }
    val endMonth = remember { YearMonth.now() }
    val daysOfWeek = remember { daysOfWeek() }

    var tempStartDate by remember(startDate) {
        mutableStateOf(startDate)
    }

    var tempEndDate by remember(endDate) {
        mutableStateOf(endDate)
    }

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = firstVisibleMonth,
    )

    val scope = rememberCoroutineScope()

    LaunchedEffect(state) {
        snapshotFlow { state.firstVisibleMonth.yearMonth }
            .distinctUntilChanged()
            .collectLatest {
                currentMonth = it
            }
    }

    Column(
        modifier = modifier
            .padding(top = 20.dp)
            .background(
                Color.White,
                shape = MaterialTheme.shapes.medium
            )
            .border(
                1.dp,
                color = colorResource(R.color.purple01),
                shape = MaterialTheme.shapes.medium
            )
    ) {

        DurationCalendarTitle(
            modifier = Modifier
                .padding(top = 8.dp)
                .padding(horizontal = 16.dp),
            currentMonth = currentMonth,
            goToPrevious = {
                scope.launch {
                    val target = state.firstVisibleMonth.yearMonth.previousMonth
                    if (target >= state.startMonth) {
                        state.animateScrollToMonth(target)
                    }
                }
            },
            goToNext = {
                scope.launch {
                    val target = state.firstVisibleMonth.yearMonth.nextMonth
                    if (target <= state.endMonth) {
                        state.animateScrollToMonth(target)
                    }
                }
            },
        )

        HorizontalCalendar(
            modifier = Modifier
                .wrapContentWidth()
                .padding(top = 10.dp, bottom = 8.dp),
            state = state,
            contentHeightMode = ContentHeightMode.Wrap,
            dayContent = { day ->
                DateRangeDay(
                    day = day,
                    startDate = tempStartDate,
                    endDate = tempEndDate,
                    onClick = { clickedDay ->
                        if (clickedDay.position == DayPosition.MonthDate) {
                            when {
                                clickedDay.date == tempStartDate -> {
                                    // 시작일 클릭 시 아무것도 하지 않거나 새로운 범위 시작
                                    return@DateRangeDay
                                }

                                clickedDay.date == tempEndDate -> {
                                    // 종료일 클릭 시 아무것도 하지 않거나 새로운 범위 시작
                                    return@DateRangeDay
                                }

                                clickedDay.date.isBefore(tempStartDate) -> {
                                    // 시작일보다 이른 날짜 클릭 시 새로운 시작일로 설정
                                    tempEndDate = startDate
                                    tempStartDate = clickedDay.date
                                }

                                clickedDay.date.isAfter(tempEndDate) -> {
                                    // 종료일보다 늦은 날짜 클릭 시 새로운 종료일로 설정
                                    tempEndDate = clickedDay.date
                                }

                                clickedDay.date.isAfter(tempStartDate) && clickedDay.date.isBefore(
                                    tempEndDate
                                ) -> {
                                    // 범위 내의 날짜 클릭 시 새로운 범위 설정
                                    val diffFromStart =
                                        ChronoUnit.DAYS.between(tempStartDate, clickedDay.date)
                                    val diffFromEnd =
                                        ChronoUnit.DAYS.between(clickedDay.date, tempEndDate)

                                    if (diffFromStart > diffFromEnd) {
                                        tempStartDate = clickedDay.date
                                    } else {
                                        tempEndDate = clickedDay.date
                                    }
                                }
                            }
                        }
                    }
                )
            },
            monthHeader = {
                MonthHeader(
                    modifier = Modifier.padding(vertical = 8.dp),
                    daysOfWeek = daysOfWeek,
                )
            },
        )

        Spacer(Modifier.height(22.dp))

        Box(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp)
                .fillMaxWidth()
                .background(
                    color = colorResource(R.color.purple_5e),
                    shape = MaterialTheme.shapes.small
                )
                .padding(vertical = 15.dp, horizontal = 22.dp)
                .noEffectClickable {
                    selectDate(
                        QuoteListAction.SelectDate(
                            start = tempStartDate,
                            end = tempEndDate
                        )
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Text("확인", style = FillsaTheme.typography.buttonMediumBold, color = Color.White)
        }
    }
}

@Composable
fun DateRangeDay(
    day: CalendarDay,
    startDate: LocalDate?,
    endDate: LocalDate?,
    onClick: (CalendarDay) -> Unit,
) {
    val isStartDate = day.date == startDate
    val isEndDate = day.date == endDate
    val isInRange = startDate != null && endDate != null &&
            day.date.isAfter(startDate) && day.date.isBefore(endDate)
    val isRangeEdge = isStartDate || isEndDate

    // 범위의 시각적 배경
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(vertical = 2.dp)
    ) {
        // 범위 배경 (직사각형)
        if (isInRange || isRangeEdge) {
            Box(
                modifier = Modifier.getRangeBackgroundModifier(
                    isStartDate = isStartDate,
                    isEndDate = isEndDate,
                    isInRange = isInRange
                )
            )
        }

        // 선택된 날짜의 원형 배경
        if (isRangeEdge) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .background(
                        color = colorResource(R.color.purple01),
                        shape = CircleShape
                    )
            )
        }

        // 날짜 텍스트와 클릭 영역
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    enabled = day.position == DayPosition.MonthDate,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onClick(day)
                },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = day.date.dayOfMonth.toString(),
                color = when {
                    isRangeEdge -> Color.White
                    day.position == DayPosition.MonthDate -> Color.Black
                    else -> Color.Gray.copy(alpha = 0.4f)
                },
                fontSize = 16.sp,
                fontWeight = if (isRangeEdge) FontWeight.Bold else FontWeight.Normal,
            )
        }
    }
}

@Composable
private fun Modifier.getRangeBackgroundModifier(
    isStartDate: Boolean,
    isEndDate: Boolean,
    isInRange: Boolean,
    color: Color = colorResource(R.color.purple02)
): Modifier {
    return this.then(
        when {
            // 중간 범위 날짜들
            isInRange -> {
                Modifier
                    .fillMaxSize()
                    .padding(vertical = 6.dp)
                    .background(
                        color = color,
                        shape = RectangleShape
                    )
            }

            // 시작일 (왼쪽이 둥글고 오른쪽이 직선)
            isStartDate && !isEndDate -> {
                Modifier
                    .fillMaxSize()
                    .padding(vertical = 6.dp)
                    .padding(start = 6.dp)
                    .background(
                        color = color,
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            bottomStart = 16.dp,
                            topEnd = 0.dp,
                            bottomEnd = 0.dp
                        )
                    )

            }

            // 종료일 (오른쪽이 둥글고 왼쪽이 직선)
            isEndDate && !isStartDate -> {
                Modifier
                    .fillMaxSize()
                    .padding(vertical = 6.dp)
                    .padding(end = 6.dp)
                    .background(
                        color = color,
                        shape = RoundedCornerShape(
                            topStart = 0.dp,
                            bottomStart = 0.dp,
                            topEnd = 16.dp,
                            bottomEnd = 16.dp
                        )
                    )
            }

            // 시작일과 종료일이 같은 경우 (하루만 선택된 경우)
            isStartDate && isEndDate -> {
                Modifier
                    .fillMaxSize()
                    .padding(vertical = 6.dp)
                    .background(
                        color = color,
                        shape = RoundedCornerShape(16.dp)
                    )
            }

            // 기본값 (이 경우는 호출되지 않아야 함)
            else -> Modifier
        }
    )
}


@Composable
fun DurationCalendarTitle(
    modifier: Modifier,
    currentMonth: YearMonth,
    goToPrevious: () -> Unit,
    goToNext: () -> Unit,
) {

    val converter = remember {
        DateTimeFormatter.ofPattern("yyyy. MM", Locale.KOREA)
    }

    val convertedDate by remember(currentMonth) {
        mutableStateOf(currentMonth.format(converter))
    }

    val displayBeforeButton by remember(currentMonth) {
        mutableStateOf(currentMonth > DateCondition.startMonth)
    }

    val displayNextButton by remember(currentMonth) {
        mutableStateOf(currentMonth < YearMonth.now())
    }

    ConstraintLayout(
        modifier = modifier.fillMaxWidth(),
    ) {

        val (left, date, right) = createRefs()

        if (displayBeforeButton) {
            CalendarNavigationIcon(
                modifier = Modifier
                    .constrainAs(left) {
                        end.linkTo(date.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    },
                painter = painterResource(R.drawable.icn_arrow_black),
                contentDescription = "Previous",
                onClick = goToPrevious,
            )
        }
        Text(
            modifier = Modifier
                .constrainAs(date) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
            text = convertedDate,
            style = FillsaTheme.typography.heading4,
            textAlign = TextAlign.Center,
            color = colorResource(R.color.gray_700)
        )
        if (displayNextButton) {
            CalendarNavigationIcon(
                modifier = Modifier
                    .constrainAs(right) {
                        start.linkTo(date.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                    .rotate(180f),
                painter = painterResource(R.drawable.icn_arrow_black),
                contentDescription = "Next",
                onClick = goToNext,
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    DurationCalendar(
        startDate = LocalDate.now(),
        endDate = LocalDate.now().plusDays(4),
        selectDate = {}
    )
}
