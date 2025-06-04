package com.arakene.presentation.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.CalendarAction
import com.arakene.presentation.viewmodel.CalendarViewModel

@Composable
fun CalendarView(
    viewModel: CalendarViewModel = hiltViewModel()
) {

    val data by remember {
        viewModel.data
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 20.dp)
    ) {

        CalendarSection(
            changeMonth = {
                viewModel.handleContract(CalendarAction.ChangeMonth(it))
            }
        )

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