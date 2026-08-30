package com.arakene.presentation.ui.calendar

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.CommonEffect
import com.arakene.presentation.util.HandleViewEffect
import com.arakene.presentation.util.LocalSnackbarHost
import com.arakene.presentation.util.Navigate
import com.arakene.presentation.util.Screens
import com.arakene.presentation.util.StreakProvider
import com.arakene.presentation.util.action.CalendarAction
import com.arakene.presentation.util.copyToClipboard
import com.arakene.presentation.util.noEffectClickable
import com.arakene.presentation.viewmodel.CalendarViewModel
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalClipboard

@Composable
fun CalendarView(
    navigate: Navigate,
    popBackStack: () -> Unit,
    viewModel: CalendarViewModel = hiltViewModel(),
) {
    val data by remember { viewModel.data }
    val selectedDay by remember { viewModel.selectedDay }
    val lifecycleOwner = LocalLifecycleOwner.current
    val selectedQuote = data?.memberQuotes?.firstOrNull {
        it.quoteDate == selectedDay.date.toString()
    }
    val context = LocalContext.current
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    val snackbarHost = LocalSnackbarHost.current

    LaunchedEffect(Unit) { viewModel.handleContract(CommonEffect.Refresh) }
    LaunchedEffect(data?.memberQuotes) {
        if (!data?.memberQuotes.isNullOrEmpty()) viewModel.handleContract(CalendarAction.SelectDay(selectedDay))
    }
    BackHandler { popBackStack() }
    HandleViewEffect(viewModel.effect, lifecycleOwner) {
        if (it is CommonEffect.Move) navigate(it.screen)
    }

    Column(
        modifier = Modifier.fillMaxSize()
            .background(FillsaTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .offset(y = (-20).dp),
    ) {
        CalendarHeader(
            onHome = { navigate(Screens.Home()) },
            onProfile = { navigate(Screens.MyPage) },
        )
        CalendarSection(
            memberQuotes = data?.memberQuotes ?: emptyList(),
            changeMonth = { viewModel.handleContract(CalendarAction.ChangeMonth(it)) },
            selectDay = { viewModel.handleContract(CalendarAction.SelectDay(it)) },
            selectedDay = selectedDay,
            modifier = Modifier.padding(top = 10.dp),
        )
        CalendarCountSection(
            typingCount = data?.monthlySummary?.typingCount ?: 0,
            likeCount = data?.monthlySummary?.likeCount ?: 0,
            todayCompleteCount = data?.monthlySummary?.streakCount ?: 0,
            modifier = Modifier.padding(top = 10.dp),
            countOnClick = { viewModel.handleContract(CalendarAction.ClickCount) },
        )
        CalendarQuoteSection(
            quoteData = selectedQuote,
            selectedDay = selectedDay,
            presentation = calendarSelectedDayPresentation(selectedQuote, expanded = false),
            onCopy = {
                selectedQuote?.let {
                    copyToClipboard(context, scope, clipboard, snackbarHost, it.quote, it.author)
                }
            },
            onShare = {
                selectedQuote?.let { navigate(Screens.Share(it.quote, it.author)) }
            },
            // Calendar has no independent action contract; these retain the established
            // selected-date Home flow, where like/image behavior already exists.
            onLike = { viewModel.handleContract(CalendarAction.ClickBottomQuote) },
            onImage = { viewModel.handleContract(CalendarAction.ClickBottomQuote) },
            onOpenQuote = { viewModel.handleContract(CalendarAction.ClickBottomQuote) },
            modifier = Modifier.padding(top = 10.dp),
        )
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun CalendarHeader(onHome: () -> Unit, onProfile: () -> Unit) {
    val streak = StreakProvider.current
    Row(
        modifier = Modifier.fillMaxWidth().height(50.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CalendarFigmaAsset(
            fileName = "home_logo.svg",
            modifier = Modifier.size(width = 60.dp, height = 27.dp).noEffectClickable(click = onHome),
            assetSet = "home",
        )
        Spacer(Modifier.weight(1f))
        if (streak != null) {
            CalendarFigmaAsset(
                fileName = "home_streak.svg",
                modifier = Modifier.size(20.dp),
                assetSet = "home",
            )
            androidx.compose.material3.Text(
                streak.currentStreak.toString() + "일",
                style = FillsaTheme.typography.subtitle1,
                color = FillsaTheme.colorScheme.onBackground1,
                modifier = Modifier.padding(start = 2.dp),
            )
        }
        CalendarFigmaAsset(
            fileName = "home_profile.svg",
            modifier = Modifier.padding(start = 12.dp).size(24.dp).noEffectClickable(click = onProfile),
            assetSet = "home",
        )
    }
}
