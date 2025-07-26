package com.arakene.presentation.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
    darkMode: Boolean = isSystemInDarkTheme()
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

    Row(
        modifier = modifier
            .background(
                color = if (darkMode) colorResource(R.color.gray_600) else Color.White,
                shape = RoundedCornerShape(10.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .padding(start = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                day,
                style = FillsaTheme.typography.heading4,
                color = MaterialTheme.colorScheme.onTertiary
            )
            Text(
                dayOfWeek,
                style = FillsaTheme.typography.body4,
                color = MaterialTheme.colorScheme.onTertiary
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
            color = MaterialTheme.colorScheme.onPrimary
        )

    }
}

@Preview
@Composable
private fun CalendarQuoteSectionPreview() {
    FillsaTheme {
        CalendarQuoteSection(
            selectedDayQuote = "",
            CalendarDay(LocalDate.now(), DayPosition.InDate)
        )
    }
}