package com.arakene.presentation.ui.quotelist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arakene.presentation.ui.common.DurationCalendar
import com.arakene.presentation.util.action.QuoteListAction
import java.time.LocalDate

@Composable
fun DurationCalendarSection(
    displayCalendar: Boolean,
    startDate: LocalDate,
    endDate: LocalDate,
    selectDate: (QuoteListAction.SelectDate) -> Unit,
    modifier: Modifier = Modifier
) {

    AnimatedVisibility(
        visible = displayCalendar,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
    ) {
        DurationCalendar(
            startDate = startDate,
            endDate = endDate,
            selectDate = selectDate,
            modifier = modifier.padding(top = 10.dp)
        )
    }
}