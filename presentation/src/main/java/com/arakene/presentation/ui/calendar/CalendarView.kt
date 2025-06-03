package com.arakene.presentation.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arakene.presentation.ui.theme.FillsaTheme

@Composable
fun CalendarView(

) {


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 20.dp)
    ) {

        CalendarSection()

        CalendarCountSection(
            modifier = Modifier.padding(top = 15.dp)
        )

        CalendarQuoteSection(
            modifier = Modifier.padding(top = 15.dp, bottom = 30.dp)
        )

    }

}

@Composable
@Preview
private fun CalendarViewPreview() {
    FillsaTheme { CalendarView() }
}