package com.arakene.presentation.ui.calendar

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.arakene.presentation.ui.home.HomeTopSection
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.CalendarAction
import com.arakene.presentation.util.CommonEffect
import com.arakene.presentation.util.HandleViewEffect
import com.arakene.presentation.util.Navigate
import com.arakene.presentation.util.noEffectClickable
import com.arakene.presentation.viewmodel.CalendarViewModel

@Composable
fun CalendarView(
    navigate: Navigate,
    popBackStack: () -> Unit,
    viewModel: CalendarViewModel = hiltViewModel()
) {

    val data by remember {
        viewModel.data
    }

    val selectedDayQuote by remember {
        viewModel.selectedDayQuote
    }

    val selectedDay by remember {
        viewModel.selectedDay
    }

    LaunchedEffect(Unit) {
        viewModel.handleContract(CommonEffect.Refresh)
    }

    LaunchedEffect(data?.memberQuotes) {
        if (!data?.memberQuotes.isNullOrEmpty()) {
            viewModel.handleContract(CalendarAction.SelectDay(selectedDay))
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    BackHandler {
        popBackStack()
    }

    HandleViewEffect(
        viewModel.effect,
        lifecycleOwner
    ) {
        when (it) {
            is CommonEffect.Move -> {
                navigate(it.screen)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 20.dp)
    ) {

        HomeTopSection(
            navigate = navigate
        )

        BoxWithConstraints(modifier = Modifier.weight(1f)) {

            val calendarHeight = remember {
                maxHeight * 0.6f
            }

            Column {
                CalendarSection(
                    memberQuotes = data?.memberQuotes ?: emptyList(),
                    changeMonth = {
                        viewModel.handleContract(CalendarAction.ChangeMonth(it))
                    },
                    selectDay = {
                        viewModel.handleContract(CalendarAction.SelectDay(it))
                    },
                    selectedDay = selectedDay,
                    modifier = Modifier.height(calendarHeight)
                )

                CalendarCountSection(
                    typingCount = data?.monthlySummary?.typingCount ?: 0,
                    likeCount = data?.monthlySummary?.likeCount ?: 0,
                    modifier = Modifier.padding(top = 15.dp),
                    countOnClick = {
                        viewModel.handleContract(CalendarAction.ClickCount)
                    }
                )

                CalendarQuoteSection(
                    selectedDayQuote = selectedDayQuote,
                    selectedDay = selectedDay,
                    modifier = Modifier
                        .padding(top = 15.dp, bottom = 30.dp)
                        .noEffectClickable {
                            viewModel.handleContract(CalendarAction.ClickBottomQuote)
                        }
                )
            }

        }


    }

}

@Composable
@Preview
private fun CalendarViewPreview() {
    FillsaTheme { CalendarView(navigate = {}, popBackStack = {}) }
}