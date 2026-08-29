package com.arakene.presentation.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.IsDarkMode
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarQuoteSection(
    selectedDayQuote: String,
    selectedDay: CalendarDay,
    modifier: Modifier = Modifier,
    darkMode: Boolean = IsDarkMode.current
) {
    val day = remember(selectedDay) {
        selectedDay.date.dayOfMonth.toString()
    }

    val dayOfWeek = remember {
        selectedDay.date
            .dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREA)
            .let {
                "($it)"
            }
    }

    if (selectedDayQuote.isBlank()) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CalendarFigmaAsset(
                fileName = "calendar_empty_handwriting.svg",
                modifier = Modifier.size(100.dp),
                darkMode = darkMode,
            )
            Column(modifier = Modifier.padding(start = 0.dp)) {
                Text(
                    text = "필사하지 않은 날이에요.",
                    style = FillsaTheme.typography.body3,
                    color = if (darkMode) Color.White else colorResource(R.color.purple01),
                )
                Text(
                    text = "아래 텍스트를 선택하여 기록해주세요!",
                    style = FillsaTheme.typography.body4,
                    color = if (darkMode) Color.White else colorResource(R.color.purple01),
                )
            }
        }
    } else {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(
                    color = if (darkMode) Color(0xFF424242) else Color.White,
                    shape = RoundedCornerShape(10.dp),
                )
                .border(
                    width = 1.dp,
                    color = if (darkMode) Color(0xFF616161) else colorResource(R.color.purple01),
                    shape = RoundedCornerShape(10.dp),
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            Column(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .padding(start = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    day,
                    style = FillsaTheme.typography.heading4,
                    color = if (darkMode) colorResource(R.color.white) else colorResource(R.color.purple01),
                )
                Text(
                    dayOfWeek,
                    style = FillsaTheme.typography.body4,
                    color = if (darkMode) colorResource(R.color.white) else colorResource(R.color.purple01),
                )
            }

            Text(
                selectedDayQuote,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 20.dp, end = 10.dp)
                    .padding(vertical = 10.dp),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                style = FillsaTheme.typography.body3,
                color = FillsaTheme.colorScheme.onBackground1,
            )

        }
    }
}

@Preview
@Composable
private fun CalendarQuoteSectionPreview() {
    FillsaTheme(darkTheme = false) {
        CalendarQuoteSection(
            selectedDayQuote = "",
            CalendarDay(LocalDate.now(), DayPosition.InDate)
        )
    }
}
