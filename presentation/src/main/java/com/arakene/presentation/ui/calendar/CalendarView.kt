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
import com.arakene.presentation.ui.home.ImageDialog
import com.arakene.presentation.util.CalendarEffect
import com.arakene.presentation.util.CommonEffect
import com.arakene.presentation.util.HandleViewEffect
import com.arakene.presentation.util.LocalSnackbarHost
import com.arakene.presentation.util.LocalDialogDataHolder
import com.arakene.presentation.util.Navigate
import com.arakene.presentation.util.Screens
import com.arakene.presentation.util.StreakProvider
import com.arakene.presentation.util.action.CalendarAction
import com.arakene.presentation.util.copyToClipboard
import com.arakene.presentation.util.noEffectClickable
import com.arakene.presentation.util.uriToCacheFile
import com.arakene.presentation.util.resizeImageToMaxSize
import com.arakene.presentation.viewmodel.CalendarViewModel
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalConfiguration
import com.arakene.presentation.util.HomeAnswerUiState
import com.arakene.presentation.util.IsDarkMode
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun CalendarView(
    navigate: Navigate,
    popBackStack: () -> Unit,
    viewModel: CalendarViewModel = hiltViewModel(),
) {
    val productionData by remember { viewModel.data }
    val productionSelectedDay by remember { viewModel.selectedDay }
    val qaFixture = LocalCalendarRuntimeQaFixture.current
    val data = qaFixture?.data ?: productionData
    val selectedDay = qaFixture?.selectedDay ?: productionSelectedDay
    val lifecycleOwner = LocalLifecycleOwner.current
    val selectedQuote = data?.memberQuotes?.firstOrNull {
        it.quoteDate == selectedDay.date.toString()
    }
    val context = LocalContext.current
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    val snackbarHost = LocalSnackbarHost.current
    val dialogDataHolder = LocalDialogDataHolder.current
    val imageTarget = viewModel.imageDialogTarget

    val registeredImageUri = remember(context.packageName) {
        "android.resource://${context.packageName}/${com.arakene.presentation.R.drawable.home_registered_image_fixture}"
    }
    val selectedDayPresentation = qaFixture?.selectedDayPresentation(registeredImageUri)
        ?: calendarSelectedDayPresentation(
            selectedQuote,
            language = LocalConfiguration.current.locales[0].language,
            isMember = viewModel.isMemberSession,
        )
    val answerState = if (qaFixture == null) viewModel.answerUiState else HomeAnswerUiState(
        draft = selectedDayPresentation.answer,
        recordedAnswer = selectedDayPresentation.answer.takeIf { it.isNotBlank() },
        isEditing = !selectedDayPresentation.hasRecordedAnswer,
        dateKey = selectedDay.date,
    )

    LaunchedEffect(qaFixture) {
        if (qaFixture == null) viewModel.handleContract(CommonEffect.Refresh)
    }
    LaunchedEffect(data?.memberQuotes, qaFixture) {
        if (qaFixture == null && !data?.memberQuotes.isNullOrEmpty()) {
            viewModel.handleContract(CalendarAction.SelectDay(selectedDay))
        }
    }
    BackHandler(enabled = qaFixture == null) { popBackStack() }
    HandleViewEffect(viewModel.effect, lifecycleOwner) {
        if (qaFixture == null) {
            when (it) {
                is CommonEffect.Move -> navigate(it.screen)
                is CommonEffect.ShowSnackBar -> snackbarHost.showSnackbar(it.message)
                is CommonEffect.ShowDialog -> {
                    dialogDataHolder.data = it.dialogData
                    dialogDataHolder.show = true
                }
                is CalendarEffect.ProcessImage -> {
                    val file = withContext(Dispatchers.IO) {
                        uriToCacheFile(context, it.uri.toUri())?.let { cached ->
                            resizeImageToMaxSize(cached, context.cacheDir)
                        }
                    }
                    viewModel.uploadImage(file, it.target)
                }
            }
        }
    }

    if (imageTarget != null && qaFixture == null) {
        val image = data?.memberQuotes?.firstOrNull {
            it.quoteDate == imageTarget.quoteDate && it.dailyQuoteSeq == imageTarget.dailyQuoteSeq
        }
        ImageDialog(
            quote = imageTarget.quote,
            author = imageTarget.author,
            backgroundImageUrl = image?.imagePath.orEmpty(),
            onDismiss = viewModel::closeImageDialog,
            uploadImage = { viewModel.handleContract(CalendarAction.ChangeImage(it.toString())) },
            deleteOnClick = { viewModel.handleContract(CalendarAction.DeleteImage) },
        )
    }

    CalendarContent(data, selectedDay, selectedQuote, selectedDayPresentation, answerState,
        navigate, viewModel::handleContract, qaFixture)

}

@Composable
private fun CalendarHeader(onHome: () -> Unit, onProfile: () -> Unit) {
    val streak = StreakProvider.current
    val darkMode = IsDarkMode.current
    Row(
        modifier = Modifier.fillMaxWidth().height(50.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CalendarFigmaAsset(
            fileName = "home_logo.svg",
            modifier = Modifier.size(width = 60.dp, height = 27.dp).noEffectClickable(click = onHome),
            darkMode = darkMode,
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
            darkMode = darkMode,
            assetSet = "home",
        )
    }
}


@Composable
internal fun CalendarContent(
    data: com.arakene.domain.responses.MemberMonthlyQuoteResponse?,
    selectedDay: com.kizitonwose.calendar.core.CalendarDay,
    selectedQuote: com.arakene.domain.responses.MemberQuotesData?,
    selectedDayPresentation: CalendarSelectedDayPresentation,
    answerState: HomeAnswerUiState, navigate: Navigate,
    onAction: (com.arakene.presentation.util.Contract) -> Unit,
    qaFixture: CalendarRuntimeQaFixture? = null,
) {
    val context = LocalContext.current
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    val snackbarHost = LocalSnackbarHost.current
    Column(
        modifier = Modifier.fillMaxSize()
            .background(FillsaTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .offset(y = 6.dp),
    ) {
        CalendarHeader(
            onHome = { if (qaFixture == null) navigate(Screens.Home()) },
            onProfile = { if (qaFixture == null) navigate(Screens.MyPage) },
        )
        CalendarSection(
            memberQuotes = data?.memberQuotes ?: emptyList(),
            changeMonth = {
                if (qaFixture == null) onAction(CalendarAction.ChangeMonth(it))
            },
            selectDay = {
                if (qaFixture == null) onAction(CalendarAction.SelectDay(it))
            },
            selectedDay = selectedDay,
            qaState = qaFixture?.state,
            modifier = Modifier.padding(top = 10.dp),
        )
        CalendarCountSection(
            typingCount = data?.monthlySummary?.typingCount ?: 0,
            likeCount = data?.monthlySummary?.likeCount ?: 0,
            modifier = Modifier.padding(top = 10.dp),
            countOnClick = {
                if (qaFixture == null) onAction(CalendarAction.ClickCount)
            },
        )
        CalendarQuoteSection(
            quoteData = selectedQuote,
            selectedDay = selectedDay,
            presentation = selectedDayPresentation,
            answerState = answerState,
            onAnswerChanged = { if (qaFixture == null) onAction(CalendarAction.ChangeAnswer(it)) },
            onRecordAnswer = { if (qaFixture == null) onAction(CalendarAction.RecordAnswer) },
            onEditAnswer = { if (qaFixture == null) onAction(CalendarAction.EditAnswer) },
            onCopy = {
                if (qaFixture == null) {
                    selectedQuote?.let {
                        copyToClipboard(context, scope, clipboard, snackbarHost, it.quote, it.author)
                    }
                }
            },
            onShare = {
                if (qaFixture == null) {
                    selectedQuote?.let { navigate(Screens.Share(it.quote, it.author)) }
                }
            },
            onLike = {
                if (qaFixture == null) onAction(CalendarAction.ClickLike)
            },
            onImage = {
                if (qaFixture == null) onAction(CalendarAction.ClickImage)
            },
            onOpenQuote = {
                if (qaFixture == null) onAction(CalendarAction.ClickBottomQuote)
            },
            modifier = Modifier.padding(top = if (selectedDayPresentation.completed) 16.dp else 12.dp),
        )
        Spacer(Modifier.height(16.dp))
    }
}
