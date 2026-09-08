package com.arakene.presentation.ui.home

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.ui.theme.ImageSection
import com.arakene.presentation.model.HomeMemberFlowState
import com.arakene.presentation.model.toDailyQuoteDto
import com.arakene.presentation.util.CommonEffect
import com.arakene.presentation.util.DialogDataHolder
import com.arakene.presentation.util.DialogData
import com.arakene.presentation.util.DialogLayoutMode
import com.arakene.presentation.util.DateCondition
import com.arakene.presentation.util.DoubleBackPressHandler
import com.arakene.presentation.util.HandleViewEffect
import com.arakene.presentation.util.HomeEffect
import com.arakene.presentation.util.HomeAnswerRecordedSnackbar
import com.arakene.presentation.util.HomeAnswerUiState
import com.arakene.presentation.util.HomeQuoteLoadState
import com.arakene.presentation.util.ImageDialogDataHolder
import com.arakene.presentation.util.LocalDialogDataHolder
import com.arakene.presentation.util.LocalHomeRuntimeQaFixture
import com.arakene.presentation.util.LocalSnackbarHost
import com.arakene.presentation.util.LocaleType
import com.arakene.presentation.util.Screens
import com.arakene.presentation.util.action.HomeAction
import com.arakene.presentation.util.copyToClipboard
import com.arakene.presentation.util.noEffectClickable
import com.arakene.presentation.util.rememberBaseViewModel
import com.arakene.presentation.util.resizeImageToMaxSize
import com.arakene.presentation.util.uriToCacheFile
import com.arakene.presentation.viewmodel.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.arakene.domain.responses.DailyQuoteDto
import com.arakene.domain.util.YN
import java.time.LocalDate

@Composable
fun HomeView(
    requestDate: LocalDate?,
    navigate: (Screens) -> Unit,
    viewModel: HomeViewModel = rememberBaseViewModel(),
    snackbarHostState: SnackbarHostState = LocalSnackbarHost.current,
    dialogDataHolder: DialogDataHolder = LocalDialogDataHolder.current
) {

    val backgroundImageUrl by remember {
        viewModel.backgroundImageUri
    }

    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    val scope = rememberCoroutineScope()

    val clipboard = LocalClipboard.current

    val isLogged by viewModel.isLogged.collectAsState(false)

    val date by rememberSaveable {
        viewModel.date
    }

    var selectedLocale by remember {
        mutableStateOf(LocaleType.KOR)
    }
    val homeQaFixture = LocalHomeRuntimeQaFixture.current
    val qaMemberWindow = homeQaFixture?.memberWindow
    val qaSelectedDay = qaMemberWindow?.selectedDay
    val displayedQuota = qaSelectedDay?.toDailyQuoteDto() ?: viewModel.currentQuota

    LaunchedEffect(homeQaFixture?.showRecordedAnswer) {
        if (homeQaFixture?.showRecordedAnswer == true) {
            snackbarHostState.showSnackbar(
                message = HomeAnswerRecordedSnackbar,
                duration = SnackbarDuration.Indefinite,
            )
        }
    }

    val quote by remember(displayedQuota, selectedLocale) {
        mutableStateOf(
            if (selectedLocale == LocaleType.KOR) {
                displayedQuota.korQuote ?: ""
            } else {
                displayedQuota.engQuote ?: ""
            }
        )
    }

    val author by remember(displayedQuota, selectedLocale) {
        mutableStateOf(
            if (selectedLocale == LocaleType.KOR) {
                displayedQuota.korAuthor ?: ""
            } else {
                displayedQuota.engAuthor ?: ""
            }
        )
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    val imageDialogDataHolder = remember {
        ImageDialogDataHolder()
    }

    val qaImageUri = if (homeQaFixture?.showSelectedImageState == true || homeQaFixture?.showImageDialog == true) {
        "android.resource://${context.packageName}/${com.arakene.presentation.R.drawable.home_registered_image_fixture}"
    } else {
        null
    }
    val displayedBackgroundImageUrl = qaImageUri ?: backgroundImageUrl

    LaunchedEffect(homeQaFixture?.showImageDialog, homeQaFixture?.showTemplateDialog) {
        if (homeQaFixture?.showImageDialog == true || homeQaFixture?.showTemplateDialog == true) {
            imageDialogDataHolder.quote = homeQaFixture.quote
            imageDialogDataHolder.author = homeQaFixture.author
            imageDialogDataHolder.show = true
        }
    }

    LaunchedEffect(homeQaFixture?.showLoginDialog) {
        if (homeQaFixture?.showLoginDialog == true) {
            dialogDataHolder.data = DialogData.Builder()
                .title("로그인 후 사용하실 수 있습니다.")
                .layoutMode(DialogLayoutMode.HomeDarkMeasured)
                .okText("로그인 하기")
                .onClick { navigate(Screens.Login(isOnBoarding = true)) }
                .build()
            dialogDataHolder.show = true
        }
    }

    LaunchedEffect(homeQaFixture?.showMultilineDialog) {
        if (homeQaFixture?.showMultilineDialog == true) {
            dialogDataHolder.data = DialogData.Builder()
                .title("요청을 처리하는 중 여러 문제가 발생했습니다.\n잠시 후 다시 시도해 주세요.")
                .body("네트워크 상태를 확인한 뒤 다시 시도해 주세요. 문제가 계속되면 고객센터에 문의해 주세요.")
                .singleButton(true)
                .build()
            dialogDataHolder.show = true
        }
    }

    DoubleBackPressHandler(
        onExit = {
            (context as? Activity)?.finishAffinity()
        }
    )

    LaunchedEffect(requestDate, homeQaFixture) {
        if (homeQaFixture != null) {
            val qaDate = homeQaFixture.memberWindow.selectedDate
            viewModel.handleContract(HomeEffect.SetDate(qaDate))
            viewModel.currentQuota = homeQaFixture.memberWindow.selectedDay!!.toDailyQuoteDto()
            viewModel.quoteLoadState = HomeQuoteLoadState.Loaded
        } else {
            viewModel.initialRefresh(requestDate)
        }
    }

    HandleViewEffect(
        viewModel.effect,
        lifecycleOwner = lifecycleOwner
    ) {
        when (it) {
            is CommonEffect.Move -> {
                navigate(it.screen)
            }

            is HomeEffect.OpenImageDialog -> {
                imageDialogDataHolder.apply {
                    this.quote = it.quote
                    this.author = it.author
                }.run {
                    show = true
                }
            }

            is CommonEffect.ShowSnackBar -> {
                snackbarHostState.showSnackbar(it.message)
            }

            is CommonEffect.ShowDialog -> {
                dialogDataHolder.data = it.dialogData
                dialogDataHolder.show = true
            }

            is HomeEffect.ProcessImage -> {
                if (homeQaFixture != null) return@HandleViewEffect
                val file = withContext(Dispatchers.IO) {
                    uriToCacheFile(context = context, uri = it.uri.toUri())?.let { file ->
                        resizeImageToMaxSize(
                            originalFile = file,
                            cacheDir = context.cacheDir
                        )
                    }
                }
                viewModel.uploadImage(file)
            }
        }
    }

    val storedIsLike by remember {
        viewModel.isLike
    }
    val isLike = qaSelectedDay?.let { it.likeYn == YN.Y.type } ?: storedIsLike

    val completedDates = viewModel.completedDates
    val memberWindow = qaMemberWindow ?: viewModel.memberQuoteWindow
    val weekDays = if (memberWindow != null) {
        homeMemberWeekDayStates(memberWindow)
    } else {
        homeWeekDayStates(selectedDate = date, completedDates = completedDates)
    }
    val defaultQuestion = "누군가의 호의를 한참 뒤에야 받아들인 적 있나요?"
    val question = if (memberWindow != null) {
        val memberState = HomeMemberFlowState.from(memberWindow)
        if (selectedLocale == LocaleType.KOR) {
            memberState.questionKo.orEmpty()
        } else {
            memberState.questionEn.orEmpty()
        }
    } else {
        defaultQuestion
    }
    val isCalendarOpen = viewModel.isCalendarOpen
    val displayedMonth = viewModel.displayedMonth
    val isStreakTooltipOpen = viewModel.isStreakTooltipOpen
    val answerUiState = if (qaMemberWindow != null) {
        HomeMemberFlowState.from(qaMemberWindow).answer
    } else {
        viewModel.answerUiState
    }

    if (imageDialogDataHolder.show) {
        ImageDialog(
            author = imageDialogDataHolder.author,
            quote = imageDialogDataHolder.quote,
            onDismiss = { imageDialogDataHolder.show = false },
            uploadImage = {
                if (homeQaFixture == null) {
                    viewModel.handleContract(HomeAction.ClickChangeImage(uri = it.toString()))
                }
            },
            backgroundImageUrl = displayedBackgroundImageUrl,
            showDeleteAction = displayedBackgroundImageUrl.isNotEmpty() || homeQaFixture?.showTemplateDialog == true,
            deleteOnClick = {
                imageDialogDataHolder.show = false
                if (homeQaFixture == null) {
                    viewModel.handleContract(HomeAction.ClickDeleteImage)
                } else {
                    scope.launch {
                        withFrameNanos { }
                        dialogDataHolder.data = DialogData.Builder()
                            .title("이미지를 삭제하시겠습니까?")
                            .body("삭제 후 이미지를 되돌릴 수 없습니다. 😢")
                            .layoutMode(DialogLayoutMode.HomeDarkMeasured)
                            .reversed(true)
                            .cancelText("삭제하기")
                            .okText("취소")
                            .cancelOnClick { }
                            .build()
                        dialogDataHolder.show = true
                    }
                }
            }
        )
    }

    FigmaHomeContent(
        date = date,
        quote = quote,
        author = author,
        selectedLocale = selectedLocale,
        isLike = isLike,
        backgroundImageUrl = displayedBackgroundImageUrl,
        weekDays = weekDays,
        question = question,
        isCalendarOpen = isCalendarOpen,
        displayedMonth = displayedMonth,
        isStreakTooltipOpen = isStreakTooltipOpen,
        answerUiState = answerUiState,
        canGoNext = date.isBefore(viewModel.memberAnchorEndDate ?: DateCondition.currentDay()),
        onLocaleChanged = { selectedLocale = it },
        onHome = { navigate(Screens.Home()) },
        onProfile = { navigate(Screens.MyPage) },
        onCalendar = { viewModel.handleContract(HomeAction.ClickCalendar) },
        onDismissCalendar = { viewModel.handleContract(HomeAction.DismissHomeCalendar) },
        onMonthChanged = { viewModel.handleContract(HomeAction.ChangeHomeMonth(it)) },
        onDateSelected = { viewModel.handleContract(HomeAction.SelectHomeDate(it)) },
        onWeekDaySelected = { viewModel.handleContract(HomeAction.SelectWeekDay(it)) },
        onStreakStatus = { viewModel.handleContract(HomeAction.ClickStreakStatus) },
        onDismissStreakTooltip = { viewModel.handleContract(HomeAction.DismissStreakTooltip) },
        onStreakCalendar = { viewModel.handleContract(HomeAction.ClickStreakCalendar) },
        onQuote = {
            if (homeQaFixture != null) {
                navigate(
                    Screens.DailyQuote(
                        DailyQuoteDto(
                            quote = "상황을 가장 잘 활용하는 사람이 가장 좋은 상황을 맞는다.",
                            author = "존 우든",
                        )
                    )
                )
            } else {
                viewModel.handleContract(HomeAction.ClickQuote)
            }
        },
        onAnswerChanged = { viewModel.handleContract(HomeAction.ChangeAnswer(it)) },
        onRecordAnswer = { viewModel.handleContract(HomeAction.RecordAnswer) },
        onEditAnswer = { viewModel.handleContract(HomeAction.EditAnswer) },
        onAuthor = { uriHandler.openUri(homeAuthorUri(author)) },
        onPreviousQuote = { viewModel.handleContract(HomeAction.LoadPreviousWindow) },
        onNextQuote = { viewModel.handleContract(HomeAction.LoadNextWindow) },
        onCopy = { copyToClipboard(context, scope, clipboard, snackbarHostState, quote, author) },
        onShare = {
            viewModel.handleContract(HomeAction.ClickShare(author = author, quote = quote))
        },
        onLike = {
            if (homeQaFixture == null) viewModel.handleContract(HomeAction.ClickLike)
        },
        onImage = {
            viewModel.handleContract(
                HomeAction.ClickImage(isLogged = isLogged, author = author, quote = quote)
            )
        }
    )

}


@Preview
@Composable
private fun HomeViewPreview() {
    FillsaTheme {
        HomeView(
            requestDate = LocalDate.now(),
            navigate = {}
        )
    }
}
