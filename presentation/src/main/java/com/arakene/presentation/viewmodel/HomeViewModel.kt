package com.arakene.presentation.viewmodel


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.arakene.domain.requests.LikeRequest
import com.arakene.domain.requests.LocalQuoteInfo
import com.arakene.domain.responses.DailyQuoteDto
import com.arakene.domain.usecase.TestErrorCodeUseCase
import com.arakene.domain.usecase.common.GetAccessTokenUseCase
import com.arakene.domain.usecase.common.GetLoginStatusUseCase
import com.arakene.domain.usecase.common.GetStreakCountUseCase
import com.arakene.domain.usecase.db.AddLocalQuoteUseCase
import com.arakene.domain.usecase.db.FindLocalQuoteByIdUseCase
import com.arakene.domain.usecase.db.GetAllStreakInfoUseCase
import com.arakene.domain.usecase.db.GetLocalQuoteListUseCase
import com.arakene.domain.usecase.db.UpdateLocalQuoteLikeUseCase
import com.arakene.domain.usecase.home.DeleteUploadImageUseCase
import com.arakene.domain.usecase.home.GetDailyQuoteNoTokenUseCase
import com.arakene.domain.usecase.home.GetDailyQuoteUseCase
import com.arakene.domain.usecase.home.GetMemberQuoteDayUseCase
import com.arakene.domain.usecase.home.GetMemberWeeklyQuotesUseCase
import com.arakene.domain.usecase.home.PostLikeUseCase
import com.arakene.domain.usecase.home.PostUploadImageUseCase
import com.arakene.domain.usecase.home.SaveQuoteAnswerUseCase
import com.arakene.domain.util.YN
import com.arakene.domain.util.ApiResult
import com.arakene.presentation.model.HomeLoadCommand
import com.arakene.presentation.model.HomeAuthContext
import com.arakene.presentation.model.HomeAuthBoundCommand
import com.arakene.presentation.model.HomeAuthBoundRequestCoordinator
import com.arakene.presentation.model.HomeAuthBoundRequestToken
import com.arakene.presentation.model.HomeMemberFlowState
import com.arakene.presentation.model.HomeMemberMutationTarget
import com.arakene.presentation.model.HomeMemberOrchestrationState
import com.arakene.presentation.model.HomeMemberQuoteWindow
import com.arakene.presentation.model.acceptHomeMemberSelection
import com.arakene.presentation.model.beginHomeMemberRequest
import com.arakene.presentation.model.homeInitialLoadCommand
import com.arakene.presentation.model.homeMemberLoadStateAfterFailure
import com.arakene.presentation.model.patchHomeMemberImage
import com.arakene.presentation.model.patchHomeMemberLike
import com.arakene.presentation.model.resolveMemberAnchorTarget
import com.arakene.presentation.model.resolveInitialMemberWindow
import com.arakene.presentation.model.selectMemberDate
import com.arakene.presentation.model.selectedMutationSequence
import com.arakene.presentation.model.shouldAcceptHomeMemberQuoteResponse
import com.arakene.presentation.model.storeHomeMemberWindow
import com.arakene.presentation.model.toDailyQuoteDto
import com.arakene.presentation.model.transitionHomeAuthContext
import com.arakene.presentation.util.Action
import com.arakene.presentation.util.BaseViewModel
import com.arakene.presentation.util.CommonEffect
import com.arakene.presentation.util.DateCondition
import com.arakene.presentation.util.DialogData
import com.arakene.presentation.util.DialogLayoutMode
import com.arakene.presentation.util.Effect
import com.arakene.presentation.util.HomeEffect
import com.arakene.presentation.util.HomeAnswerUiState
import com.arakene.presentation.util.HomeMemberAnswerCoordinator
import com.arakene.presentation.util.HomeQuoteLoadState
import com.arakene.presentation.util.Screens
import com.arakene.presentation.util.TypographyEnum
import com.arakene.presentation.util.action.HomeAction
import com.arakene.presentation.util.changeHomeAnswer
import com.arakene.presentation.util.editHomeAnswer
import com.arakene.presentation.util.homeTypingDestination
import com.arakene.presentation.util.homeStreakCalendarDestination
import com.arakene.presentation.util.logDebug
import com.arakene.presentation.util.recordHomeAnswerForHome
import com.arakene.presentation.util.selectHomeCalendarDate
import com.arakene.presentation.util.toggleHomeCalendar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    // TODO: 이거 정리하는거 디자인패턴? 설계? 관련 글 봤는데 찾아보기
    private val getDailyQuoteNoTokenUseCase: GetDailyQuoteNoTokenUseCase,
    private val getDailyQuoteUseCase: GetDailyQuoteUseCase,
    private val getMemberWeeklyQuotesUseCase: GetMemberWeeklyQuotesUseCase,
    private val getMemberQuoteDayUseCase: GetMemberQuoteDayUseCase,
    private val saveQuoteAnswerUseCase: SaveQuoteAnswerUseCase,
    private val getLoginStatusUseCase: GetLoginStatusUseCase,
    private val postLikeUseCase: PostLikeUseCase,
    private val postUploadImageUseCase: PostUploadImageUseCase,
    private val deleteUploadImageUseCase: DeleteUploadImageUseCase,
    private val updateLocalQuoteLikeUseCase: UpdateLocalQuoteLikeUseCase,
    private val getLocalQuoteListUseCase: GetLocalQuoteListUseCase,
    private val findLocalQuoteByIdUseCase: FindLocalQuoteByIdUseCase,
    private val addLocalQuoteUseCase: AddLocalQuoteUseCase,
    private val testErrorCodeUseCase: TestErrorCodeUseCase,
    private val getStreakCountUseCase: GetStreakCountUseCase,
    private val getAllStreakInfoUseCase: GetAllStreakInfoUseCase,
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
) : BaseViewModel() {

    private val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    val isLogged = getLoginStatusUseCase()

    var currentQuota by mutableStateOf(DailyQuoteDto())

    internal var quoteLoadState by mutableStateOf(HomeQuoteLoadState.Loading)

    private var requestedQuoteDate: LocalDate? = null

    private var isMemberSession = false
    private var memberWindowRequestJob: Job? = null
    private var memberOrchestration by mutableStateOf(HomeMemberOrchestrationState())
    private var activeAuthContext: HomeAuthContext? = null
    private var homeStarted = false
    private var pendingExplicitTarget: LocalDate? = null
    private var loadedAuthContext: HomeAuthContext? = null
    private var authBoundCoordinator = HomeAuthBoundRequestCoordinator()
    private var authBoundJob = SupervisorJob(viewModelScope.coroutineContext[Job])
    private var pendingImageMutation: Pair<HomeAuthBoundRequestToken, HomeMemberMutationTarget>? = null
    private var memberAnswerCoordinator = HomeMemberAnswerCoordinator()

    val memberQuoteWindow: HomeMemberQuoteWindow?
        get() = memberOrchestration.currentWindow

    val memberAnchorEndDate: LocalDate?
        get() = memberOrchestration.anchorEndDate

    var memberQuestionKo by mutableStateOf<String?>(null)
        private set

    var memberQuestionEn by mutableStateOf<String?>(null)
        private set

    var isLike = mutableStateOf(false)

    val backgroundImageUri = mutableStateOf("")

    val date = mutableStateOf(LocalDate.now())

    val streakInfo = mutableStateOf(0)

    var isCalendarOpen by mutableStateOf(false)
        private set

    var displayedMonth by mutableStateOf(YearMonth.now())
        private set

    var isStreakTooltipOpen by mutableStateOf(false)
        private set

    var answerUiState by mutableStateOf(HomeAnswerUiState())
        private set

    var completedDates by mutableStateOf<Set<LocalDate>>(emptySet())
        private set

    init {
        viewModelScope.launch {
            isLogged.collect { loggedIn ->
                val context = HomeAuthContext(
                    isLoggedIn = loggedIn,
                    accountIdentity = if (loggedIn) getAccessTokenUseCase() else null,
                )
                handleObservedAuthContext(context)
            }
        }
    }

    // Answer text and submission are one stateful interaction. Sending them through
    // the navigation throttle can drop the latest draft or a fast submit tap.
    override fun emitAction(action: Action) {
        if (action is HomeAction.ChangeAnswer || action is HomeAction.RecordAnswer || action is HomeAction.EditAnswer) {
            handleAction(action)
        } else {
            super.emitAction(action)
        }
    }

    override fun handleAction(action: Action) {
        when (action) {
            is HomeAction.ClickBefore -> {
                selectAdjacentDate(previous = true)
            }

            is HomeAction.ClickNext -> {
                selectAdjacentDate(previous = false)
            }

            is HomeAction.SelectWeekDay -> {
                selectDate(action.date)
            }

            is HomeAction.LoadPreviousWindow -> {
                selectAdjacentDate(previous = true)
            }

            is HomeAction.LoadNextWindow -> {
                selectAdjacentDate(previous = false)
            }

            is HomeAction.ClickImage -> {
                clickImage(action)
            }

            is HomeAction.ClickLike -> {
                if (!canMutateSelectedQuote()) return
                postLike(date.value)
            }

            is HomeAction.ClickQuote -> {
                if (!canMutateSelectedQuote()) return
                homeTypingDestination(
                    quote = currentQuota,
                    loadState = quoteLoadState,
                )?.let { destination ->
                    emitEffect(CommonEffect.Move(destination))
                } ?: emitEffect(
                    CommonEffect.ShowSnackBar(
                        if (quoteLoadState == HomeQuoteLoadState.Loading) {
                            "글을 불러오는 중입니다."
                        } else {
                            "글을 불러오지 못했습니다."
                        }
                    )
                )
            }

            is HomeAction.ClickShare -> {
                emitEffect(
                    CommonEffect.Move(
                        Screens.Share(
                            action.quote,
                            action.author
                        )
                    )
                )
            }

            is HomeAction.ClickChangeImage -> {
                if (!canMutateSelectedQuote()) return
                uploadBackgroundImage(action)
            }

            is HomeAction.ClickDeleteImage -> {
                if (!canMutateSelectedQuote()) return
                deleteBackgroundImage()
            }

            is HomeAction.ClickCalendar -> {
                val calendarState = toggleHomeCalendar(isCalendarOpen, date.value)
                isCalendarOpen = calendarState.isOpen
                displayedMonth = calendarState.displayedMonth
                isStreakTooltipOpen = false
            }

            is HomeAction.SelectHomeDate -> {
                if (isMemberSession) {
                    if (action.date >= DateCondition.startDay) {
                        isCalendarOpen = false
                        displayedMonth = YearMonth.from(action.date)
                        selectMemberTarget(action.date, alignToServerAnchor = true)
                    }
                } else {
                    selectHomeCalendarDate(action.date)?.let { calendarState ->
                        date.value = calendarState.refreshDate ?: return@let
                        displayedMonth = calendarState.displayedMonth
                        isCalendarOpen = false
                        requestRefresh(date.value)
                    }
                }
            }

            is HomeAction.DismissHomeCalendar -> {
                isCalendarOpen = false
            }

            is HomeAction.ChangeHomeMonth -> {
                val lastMonth = memberAnchorEndDate
                    ?.takeIf { isMemberSession }
                    ?.let(YearMonth::from)
                    ?: YearMonth.from(DateCondition.currentDay())
                if (action.month in DateCondition.startMonth..lastMonth) {
                    displayedMonth = action.month
                }
            }

            is HomeAction.ClickStreakStatus -> {
                isStreakTooltipOpen = !isStreakTooltipOpen
                isCalendarOpen = false
            }

            is HomeAction.DismissStreakTooltip -> {
                isStreakTooltipOpen = false
            }

            is HomeAction.ClickStreakCalendar -> {
                isStreakTooltipOpen = false
                emitEffect(CommonEffect.Move(homeStreakCalendarDestination()))
            }

            is HomeAction.ChangeAnswer -> {
                if (!canMutateSelectedQuote()) return
                answerUiState = changeHomeAnswer(answerUiState, action.answer)
            }

            is HomeAction.RecordAnswer -> {
                if (isMemberSession) {
                    recordMemberAnswer()
                    return
                }
                val outcome = recordHomeAnswerForHome(answerUiState)
                answerUiState = outcome.state
                emitEffect(CommonEffect.ShowSnackBar(outcome.snackbarMessage))
            }

            is HomeAction.EditAnswer -> {
                if (!canMutateSelectedQuote()) return
                answerUiState = editHomeAnswer(answerUiState)
            }

            else -> {

            }
        }

    }

    private fun recordMemberAnswer() {
        val window = memberQuoteWindow ?: return
        val token = authBoundCoordinator.capture() ?: return
        val start = memberAnswerCoordinator.begin(HomeMemberFlowState.from(window), answerUiState, token)
        val request = start.request ?: return
        memberAnswerCoordinator = start.coordinator
        answerUiState = start.answer
        launchAuthBound(token) save@{
            val apiResult = saveQuoteAnswerUseCase(request.target.dailyQuoteSeq, request.answer.draft)
            if (!authBoundCoordinator.accepts(token)) return@save
            val response = getResponse(apiResult, useLoading = false)
            if (!authBoundCoordinator.accepts(token)) return@save
            val result = memberAnswerCoordinator.completePost(memberOrchestration, authBoundCoordinator, request, response)
            memberAnswerCoordinator = result.coordinator
            memberOrchestration = result.state
            if (isSelectedMemberTarget(request.target)) {
                memberQuoteWindow?.let(::applyMemberWindow)
            }
            result.snackbarMessage?.let { emitEffect(CommonEffect.ShowSnackBar(it)) }
            if (!result.shouldRefresh) return@save

            // This reconciliation is optional: a failed GET cannot undo a successful POST.
            val refresh = memberAnswerCoordinator.captureRefresh(memberOrchestration, request)
            val dailyResult = getMemberQuoteDayUseCase(request.target.date.toString())
            val daily = (dailyResult as? ApiResult.Success)?.data
            val refreshed = memberAnswerCoordinator.completeRefresh(memberOrchestration, authBoundCoordinator, refresh, daily)
            if (refreshed != memberOrchestration) {
                memberOrchestration = refreshed
                if (isSelectedMemberTarget(request.target)) {
                    val currentAnswer = answerUiState
                    memberQuoteWindow?.let(::applyMemberWindow)
                    // An edit started after POST success belongs to the user, not the delayed GET.
                    if (currentAnswer.isEditing) answerUiState = currentAnswer
                }
            }
        }
    }

    fun testErrorCode(code: Int) {
        viewModelScope.launch {
            getResponse(
                testErrorCodeUseCase(code)
            )
        }
    }

    override fun emitEffect(effect: Effect) {
        when (effect) {
            is HomeEffect.SetDate -> {
                date.value = effect.date
            }

            is HomeEffect.Refresh -> {
                requestRefresh(effect.date)
            }

            else -> super.emitEffect(effect)
        }
    }

    fun initialRefresh(requestDate: LocalDate?) {
        requestDate?.let { date.value = it }
        homeStarted = true
        pendingExplicitTarget = requestDate
        activeAuthContext?.let { context ->
            if (loadedAuthContext != context || requestDate != null) {
                loadedAuthContext = context
                pendingExplicitTarget = null
                loadHome(context, requestDate)
            }
        }
    }

    private fun uploadBackgroundImage(homeAction: HomeAction.ClickChangeImage) {
        val requestToken = authBoundCoordinator.capture() ?: return
        val target = if (isMemberSession) {
            selectedMemberMutationTarget() ?: return
        } else {
            HomeMemberMutationTarget(date.value, currentQuota.dailyQuoteSeq)
        }
        pendingImageMutation = requestToken to target
        viewModelScope.launch {
            emitEffect(HomeEffect.ProcessImage(homeAction.uri))
        }
    }

    fun uploadImage(file: File?) {
        val mutation = pendingImageMutation ?: return
        pendingImageMutation = null
        if (!authBoundCoordinator.accepts(mutation.first)) return
        launchAuthBound(mutation.first) upload@{
            getResponse(
                postUploadImageUseCase(
                    dailyQuoteSeq = mutation.second.dailyQuoteSeq,
                    imageFile = file ?: return@upload
                ), useLoading = false
            )?.let {
                if (!authBoundCoordinator.accepts(mutation.first)) return@let
                if (mutation.first.context.isLoggedIn) {
                    memberOrchestration = patchHomeMemberImage(
                        state = memberOrchestration,
                        target = mutation.second,
                        imagePath = it.imagePath,
                    )
                    if (isSelectedMemberTarget(mutation.second)) {
                        backgroundImageUri.value = it.imagePath
                    }
                } else if (isSelectedGuestTarget(mutation.second)) {
                    backgroundImageUri.value = it.imagePath
                }
                emitEffect(CommonEffect.ShowSnackBar("이미지가 변경되었습니다."))
            }
        }
    }

    private fun deleteBackgroundImage() {
        val requestToken = authBoundCoordinator.capture() ?: return
        val target = if (isMemberSession) {
            selectedMemberMutationTarget() ?: return
        } else {
            HomeMemberMutationTarget(date.value, currentQuota.dailyQuoteSeq)
        }
        viewModelScope.launch {
            emitEffect(
                CommonEffect.ShowDialog(
                    dialogData = DialogData.Builder()
                        .title("이미지를 삭제하시겠습니까?")
                        .body("삭제 후 이미지를 되돌릴 수 없습니다. \uD83D\uDE22")
                        .titleTextStyle(TypographyEnum.Heading4)
                        .bodyTextStyle(TypographyEnum.Body2)
                        .layoutMode(DialogLayoutMode.HomeDarkMeasured)
                        .reversed(true)
                        .cancelText("삭제하기")
                        .okText("취소")
                        .cancelOnClick {
                            launchAuthBound(requestToken) {
                                val command = authBoundCoordinator.commandIfCurrent(
                                    token = requestToken,
                                    command = HomeAuthBoundCommand.DeleteImage(target.dailyQuoteSeq),
                                ) ?: return@launchAuthBound
                                getResponse(
                                    deleteUploadImageUseCase(command.dailyQuoteSeq),
                                    useLoading = false
                                )?.let {
                                    if (!authBoundCoordinator.accepts(requestToken)) return@let
                                    if (requestToken.context.isLoggedIn) {
                                        memberOrchestration = patchHomeMemberImage(
                                            state = memberOrchestration,
                                            target = target,
                                            imagePath = null,
                                        )
                                        if (isSelectedMemberTarget(target)) {
                                            backgroundImageUri.value = ""
                                        }
                                    } else if (isSelectedGuestTarget(target)) {
                                        backgroundImageUri.value = ""
                                    }
                                    emitEffect(CommonEffect.ShowSnackBar("이미지가 삭제되었습니다."))
                                } ?: let {
                                    logDebug("Fail?")
                                }
                            }
                        }
                        .build()
                )
            )
        }
    }

    private fun clickImage(action: HomeAction.ClickImage) {
        if (!action.isLogged) {
            emitEffect(
                CommonEffect.ShowDialog(
                    // TODO: 이 구조가 과연 좋은거일까? , onClick의 시점, textStyle도 지정하고싶긴한데 viewModel에서 composable함수 참조 해야함
                    DialogData.Builder()
                        .title("로그인 후 사용하실 수 있습니다.")
                        .layoutMode(DialogLayoutMode.HomeDarkMeasured)
                        .okText("로그인 하기")
                        .onClick {
                            emitEffect(CommonEffect.Move(Screens.Login(isOnBoarding = true)))
                        }
                        .build()
                ))
        } else {
            if (!canMutateSelectedQuote()) return
            emitEffect(
                HomeEffect.OpenImageDialog(
                    quote = action.quote,
                    author = action.author
                )
            )
        }
    }

    private fun postLike(date: LocalDate) {
        val targetLikeYn = if (isLike.value) YN.N.type else YN.Y.type
        isLike.value = targetLikeYn == YN.Y.type
        if (isMemberSession) {
            val requestToken = authBoundCoordinator.capture() ?: return
            val target = selectedMemberMutationTarget() ?: return
            launchAuthBound(requestToken) {
                getResponse(
                    postLikeUseCase(
                        LikeRequest(targetLikeYn),
                        dailyQuoteSeq = target.dailyQuoteSeq,
                    )
                )?.let {
                    if (!authBoundCoordinator.accepts(requestToken)) return@let
                    memberOrchestration = patchHomeMemberLike(
                        state = memberOrchestration,
                        target = target,
                        likeYn = targetLikeYn,
                    )
                    if (isSelectedMemberTarget(target)) {
                        isLike.value = targetLikeYn == YN.Y.type
                    }
                }
            }
        } else {
            val requestToken = authBoundCoordinator.capture() ?: return
            launchAuthBound(requestToken) { postLocalLike(date) }
        }
    }

    private suspend fun postLocalLike(date: LocalDate) {
        findLocalQuoteByIdUseCase(currentQuota.dailyQuoteSeq)?.let {
            updateLocalQuoteLikeUseCase(
                likeYN = if (isLike.value) {
                    YN.Y
                } else {
                    YN.N
                }, seq = currentQuota.dailyQuoteSeq
            )
        } ?: addLocalQuote(date)
    }

    private suspend fun addLocalQuote(date: LocalDate) {
        addLocalQuoteUseCase(
            LocalQuoteInfo(
                dailyQuoteSeq = currentQuota.dailyQuoteSeq,
                korQuote = currentQuota.korQuote ?: "",
                engQuote = currentQuota.engQuote ?: "",
                korAuthor = currentQuota.korAuthor ?: "",
                engAuthor = currentQuota.engAuthor ?: "",
                korTyping = "",
                engTyping = "",
                likeYn = YN.Y.type,
                memo = "",
                date = date.format(dateFormat),
                dayOfWeek = date.dayOfWeek.name
            )
        )
    }

    private fun requestRefresh(requestedDate: LocalDate?) {
        homeStarted = true
        pendingExplicitTarget = requestedDate
        activeAuthContext?.let { context ->
            loadedAuthContext = context
            pendingExplicitTarget = null
            loadHome(context, requestedDate)
        }
    }

    private fun handleObservedAuthContext(context: HomeAuthContext) {
        val previous = activeAuthContext
        val nextCoordinator = authBoundCoordinator.transition(context)
        val changed = nextCoordinator != authBoundCoordinator
        if (changed) {
            cancelAuthBoundWork()
            authBoundCoordinator = nextCoordinator
            memberOrchestration = transitionHomeAuthContext(memberOrchestration, previous, context)
            clearMemberProjection()
            loadedAuthContext = null
            pendingImageMutation = null
            requestedQuoteDate = null
        }

        activeAuthContext = context
        isMemberSession = context.isLoggedIn
        if (homeStarted && loadedAuthContext != context) {
            val target = if (previous == null) pendingExplicitTarget else null
            pendingExplicitTarget = null
            loadedAuthContext = context
            loadHome(context, target)
        }
    }

    private fun loadHome(context: HomeAuthContext, requestedDate: LocalDate?) {
        val requestToken = authBoundCoordinator.capture()
            ?.takeIf { it.context == context }
            ?: return
        launchAuthBound(requestToken) {
            when (val command = homeInitialLoadCommand(context.isLoggedIn, requestedDate)) {
                is HomeLoadCommand.MemberWeekly -> {
                    quoteLoadState = HomeQuoteLoadState.Loading
                    requestMemberWindow(
                        endDate = command.endDate,
                        requestedDate = requestedDate,
                        resolveAgainstServerAnchor = true,
                        requestToken = requestToken,
                    )
                    getStreakCount(requestToken)
                }

                is HomeLoadCommand.GuestDaily -> {
                    clearMemberSessionState()
                    currentQuota = DailyQuoteDto()
                    quoteLoadState = HomeQuoteLoadState.Loading
                    val guestDate = command.date ?: date.value
                    requestedQuoteDate = guestDate
                    loadCompletedDates(requestToken)
                    getStreakCount(requestToken)
                    getDailyQuoteNoToken(convertDate(guestDate), guestDate, requestToken)
                }
            }
        }
    }

    private fun launchAuthBound(
        requestToken: HomeAuthBoundRequestToken,
        block: suspend () -> Unit,
    ): Job = CoroutineScope(viewModelScope.coroutineContext + authBoundJob).launch {
        if (!authBoundCoordinator.accepts(requestToken)) return@launch
        block()
    }

    private fun cancelAuthBoundWork() {
        authBoundJob.cancel()
        authBoundJob = SupervisorJob(viewModelScope.coroutineContext[Job])
        memberWindowRequestJob = null
    }

    private fun clearMemberSessionState() {
        memberWindowRequestJob?.cancel()
        memberOrchestration = HomeMemberOrchestrationState(
            latestRequestId = memberOrchestration.latestRequestId + 1,
        )
        clearMemberProjection()
    }

    private fun clearMemberProjection() {
        memberAnswerCoordinator = HomeMemberAnswerCoordinator()
        currentQuota = DailyQuoteDto()
        quoteLoadState = HomeQuoteLoadState.Loading
        memberQuestionKo = null
        memberQuestionEn = null
        answerUiState = HomeAnswerUiState()
        completedDates = emptySet()
        isLike.value = false
        backgroundImageUri.value = ""
    }

    private fun selectAdjacentDate(previous: Boolean) {
        val targetDate = if (previous) date.value.minusDays(1) else date.value.plusDays(1)
        selectDate(targetDate)
    }

    private fun selectDate(targetDate: LocalDate) {
        if (targetDate < DateCondition.startDay) return
        if (isMemberSession) {
            val anchorEndDate = memberAnchorEndDate ?: return
            selectMemberTarget(minOf(targetDate, anchorEndDate))
        } else if (targetDate <= DateCondition.currentDay()) {
            date.value = targetDate
            requestRefresh(targetDate)
        }
    }

    private fun selectMemberTarget(
        targetDate: LocalDate,
        alignToServerAnchor: Boolean = false,
    ) {
        val currentWindow = memberQuoteWindow ?: return
        val anchorEndDate = memberAnchorEndDate ?: currentWindow.endDate
        val clampedTarget = minOf(targetDate, anchorEndDate)
        val result = if (alignToServerAnchor && currentWindow.select(clampedTarget) == null) {
            val anchorWindow = memberOrchestration.cachedWindows[anchorEndDate] ?: currentWindow
            resolveMemberAnchorTarget(anchorWindow, clampedTarget)
        } else {
            selectMemberDate(currentWindow, clampedTarget, memberOrchestration.cachedWindows)
        }

        result.loadCommand?.let { command ->
            val requestToken = authBoundCoordinator.capture() ?: return
            requestMemberWindow(
                endDate = command.endDate,
                requestedDate = result.requestedDate,
                resolveAgainstServerAnchor = false,
                requestToken = requestToken,
            )
        } ?: acceptMemberSelection(result.window)
    }

    private fun requestMemberWindow(
        endDate: String?,
        requestedDate: LocalDate?,
        resolveAgainstServerAnchor: Boolean,
        requestToken: HomeAuthBoundRequestToken,
    ) {
        val requestedEndDate = endDate?.let(LocalDate::parse)
        val cachedWindow = requestedEndDate?.let(memberOrchestration.cachedWindows::get)
        if (cachedWindow != null) {
            acceptMemberSelection(requestedDate?.let(cachedWindow::select) ?: cachedWindow)
            return
        }

        memberOrchestration = beginHomeMemberRequest(memberOrchestration)
        val requestId = memberOrchestration.latestRequestId
        memberWindowRequestJob?.cancel()
        memberWindowRequestJob = launchAuthBound(requestToken) request@{
            val apiResult = getMemberWeeklyQuotesUseCase(endDate)
            if (!authBoundCoordinator.accepts(requestToken)) return@request
            if (!shouldAcceptHomeMemberQuoteResponse(requestId, memberOrchestration.latestRequestId)) return@request
            val response = getResponse(apiResult)
            if (!authBoundCoordinator.accepts(requestToken)) return@request
            if (!shouldAcceptHomeMemberQuoteResponse(requestId, memberOrchestration.latestRequestId)) return@request

            if (response == null) {
                quoteLoadState = homeMemberLoadStateAfterFailure(
                    hasUsableWindow = memberOrchestration.currentWindow != null,
                )
                return@request
            }

            val responseWindow = HomeMemberQuoteWindow.from(response)

            if (resolveAgainstServerAnchor) {
                memberOrchestration = storeHomeMemberWindow(
                    state = memberOrchestration,
                    window = responseWindow,
                    isAnchor = true,
                )
                val result = resolveInitialMemberWindow(responseWindow, requestedDate)
                result.loadCommand?.let { command ->
                    applyMemberWindow(responseWindow)
                    memberWindowRequestJob = null
                    requestMemberWindow(
                        endDate = command.endDate,
                        requestedDate = result.requestedDate,
                        resolveAgainstServerAnchor = false,
                        requestToken = requestToken,
                    )
                } ?: run {
                    memberOrchestration = storeHomeMemberWindow(
                        state = memberOrchestration,
                        window = result.window,
                        isAnchor = true,
                    )
                    applyMemberWindow(result.window)
                }
            } else {
                val selectedWindow = requestedDate?.let(responseWindow::select) ?: responseWindow
                memberOrchestration = storeHomeMemberWindow(
                    state = memberOrchestration,
                    window = selectedWindow,
                )
                applyMemberWindow(selectedWindow)
            }
        }
    }

    private fun acceptMemberSelection(window: HomeMemberQuoteWindow) {
        memberWindowRequestJob?.cancel()
        memberOrchestration = acceptHomeMemberSelection(memberOrchestration, window)
        applyMemberWindow(window)
    }

    private fun applyMemberWindow(window: HomeMemberQuoteWindow) {
        val selectedDay = window.selectedDay ?: return
        val memberState = HomeMemberFlowState.from(window)
        date.value = memberState.window.selectedDate
        displayedMonth = YearMonth.from(memberState.window.selectedDate)
        currentQuota = selectedDay.toDailyQuoteDto()
        isLike.value = selectedDay.likeYn == YN.Y.type
        backgroundImageUri.value = selectedDay.imagePath.orEmpty()
        completedDates = window.days
            .asSequence()
            .filter { it.state == "done" || it.completed }
            .map { LocalDate.parse(it.date) }
            .toSet()
        memberQuestionKo = memberState.questionKo
        memberQuestionEn = memberState.questionEn
        answerUiState = memberAnswerCoordinator.answerFor(window)
        quoteLoadState = HomeQuoteLoadState.Loaded
    }

    private fun canMutateSelectedQuote(): Boolean =
        !isMemberSession || memberQuoteWindow?.selectedMutationSequence() != null

    private fun selectedMemberMutationTarget(): HomeMemberMutationTarget? {
        val selectedDay = memberQuoteWindow?.selectedDay ?: return null
        val dailyQuoteSeq = selectedDay.dailyQuoteSeq ?: return null
        return HomeMemberMutationTarget(
            date = memberQuoteWindow?.selectedDate ?: return null,
            dailyQuoteSeq = dailyQuoteSeq,
        )
    }

    private fun isSelectedMemberTarget(target: HomeMemberMutationTarget): Boolean =
        memberQuoteWindow?.selectedDate == target.date &&
            memberQuoteWindow?.selectedDay?.dailyQuoteSeq == target.dailyQuoteSeq

    private fun isSelectedGuestTarget(target: HomeMemberMutationTarget): Boolean =
        date.value == target.date && currentQuota.dailyQuoteSeq == target.dailyQuoteSeq

    private suspend fun getStreakCount(requestToken: HomeAuthBoundRequestToken) {
        val responseValue = getStreakCountUseCase()
        streakInfo.value = authBoundCoordinator.valueIfCurrent(
            token = requestToken,
            currentValue = streakInfo.value,
            responseValue = responseValue,
        )
    }

    private suspend fun loadCompletedDates(requestToken: HomeAuthBoundRequestToken) {
        val responseValue = getAllStreakInfoUseCase()
            .asSequence()
            .filter { it.isDailyWritingCompleted }
            .map { it.date }
            .toSet()
        completedDates = authBoundCoordinator.valueIfCurrent(
            token = requestToken,
            currentValue = completedDates,
            responseValue = responseValue,
        )
    }

    private fun getDailyQuoteNoToken(
        date: String,
        requestedDate: LocalDate,
        requestToken: HomeAuthBoundRequestToken,
    ) = launchAuthBound(requestToken) guestQuote@{
        val localList = getLocalQuoteListUseCase()


        val quote = getResponse(getDailyQuoteNoTokenUseCase(date))
        if (!authBoundCoordinator.accepts(requestToken)) return@guestQuote
        if (requestedQuoteDate != requestedDate) return@guestQuote

        quote?.let {
            currentQuota = DailyQuoteDto(
                likeYn = "N",
                imagePath = "",
                dailyQuoteSeq = it.dailyQuoteSeq,
                korQuote = it.korQuote,
                engQuote = it.engQuote,
                korAuthor = it.korAuthor,
                engAuthor = it.engAuthor,
                authorUrl = it.authorUrl,
            ).apply {
                quoteDate = date
            }

            localList.find {
                it.dailyQuoteSeq == currentQuota.dailyQuoteSeq
            }

            localList.find { local ->
                local.dailyQuoteSeq == it.dailyQuoteSeq
            }?.let { find ->
                isLike.value = find.likeYn == YN.Y.type
            } ?: let {
                isLike.value = false
            }

            backgroundImageUri.value = ""
            quoteLoadState = HomeQuoteLoadState.Loaded
        } ?: run { quoteLoadState = HomeQuoteLoadState.Failed }
    }

    private fun convertDate(date: LocalDate) = dateFormat.format(date)

}
