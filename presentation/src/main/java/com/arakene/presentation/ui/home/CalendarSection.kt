package com.arakene.presentation.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.ui.theme.pretendard
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarSection(
    modifier: Modifier = Modifier
) {

    val today = remember {
        val now = LocalDate.now()
        Triple(
            now.format(DateTimeFormatter.ofPattern("yyyy.MM")),
            now.dayOfMonth.toString(),
            now.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.KOREA)
        )
    }

    Column(
        modifier = modifier
            .aspectRatio(155/120f)
            .background(Color.Transparent)
            .border(
                width = 1.dp,
                shape = MaterialTheme.shapes.medium,
                color = colorResource(R.color.blue_d3)
            )
    ) {

        CalendarTop(
            yearMonth = today.first,
            day = today.third
        )

        CalendarBottom(
            modifier = Modifier.weight(1f),
            date = today.second
        )

    }

}

@Composable
private fun CalendarTop(
    yearMonth: String,
    day: String,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = colorResource(R.color.blue_d3),
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
            )
            .padding(12.dp), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        // 년.월
        Text(
            yearMonth,
            style = FillsaTheme.typography.buttonXSmallBold,
            color = colorResource(R.color.gray_700)
        )
        // 요일
        Text(
            day,
            style = FillsaTheme.typography.buttonXSmallBold,
            color = colorResource(R.color.gray_700)
        )

    }

}

@Composable
private fun CalendarBottom(
    date: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                Color.White,
                shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
            )
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 18.dp), contentAlignment = Alignment.Center
    ) {
        Text(
            date,
            color = colorResource(R.color.gray_700),
            // TODO: 디자인 시스템에 없는 폰트는 익스텐션으로 관리하는게 좋은가?
            style = androidx.compose.ui.text.TextStyle(
                fontWeight = FontWeight.Bold,
                fontFamily = pretendard,
                fontSize = 40.sp,
                lineHeight = 48.sp
            )
        )
    }
}


@Preview(widthDp = 150)
@Composable
private fun CalendarSectionPreview() {
    CalendarSection()
}