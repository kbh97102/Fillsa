package com.arakene.presentation.ui.quotelist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arakene.presentation.ui.common.DurationCalendar
import java.time.LocalDate

@Composable
fun DurationCalendarSection(
    displayCalendar: Boolean,
    startDate: LocalDate,
    endDate: LocalDate,
    setStartDate: (LocalDate) -> Unit,
    setEndDate: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(visible = displayCalendar) {
        DurationCalendar(
            startDate = startDate,
            endDate = endDate,
            setStartDate = setStartDate,
            setEndDate = setEndDate,
            modifier = modifier.padding(top = 10.dp)
        )
    }
}