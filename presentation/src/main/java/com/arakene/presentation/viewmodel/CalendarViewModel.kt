package com.arakene.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.arakene.domain.responses.MemberMonthlyQuoteResponse
import com.arakene.domain.responses.MemberQuotesData
import com.arakene.domain.responses.MonthlySummaryData
import com.arakene.domain.usecase.calendar.GetMonthlyQuotesNonMemberUseCase
import com.arakene.domain.usecase.calendar.GetQuotesMonthlyUseCase
import com.arakene.domain.usecase.common.GetLoginStatusUseCase
import com.arakene.domain.usecase.common.GetAccessTokenUseCase
import com.arakene.domain.usecase.home.GetMemberQuoteDayUseCase
import com.arakene.domain.usecase.home.SaveQuoteAnswerUseCase
import com.arakene.domain.util.ApiResult
import com.arakene.presentation.model.CalendarAnswerCoordinator
import com.arakene.presentation.model.CalendarMonthState
import com.arakene.presentation.model.HomeAuthBoundRequestCoordinator
import com.arakene.presentation.model.HomeAuthBoundRequestToken
import com.arakene.presentation.model.HomeAuthContext
import com.arakene.presentation.model.selectCalendarDay
import com.arakene.presentation.model.editCalendarAnswer
import com.arakene.presentation.model.changeCalendarAnswer
import com.arakene.presentation.util.HomeAnswerUiState
import com.arakene.presentation.util.recordHomeAnswerForHome
import com.arakene.presentation.util.HomeAnswerRecordedSnackbar
import com.arakene.domain.usecase.db.GetLocalQuoteListUseCase
import com.arakene.domain.usecase.db.GetTodayLocalStreakInfoUseCase
import com.arakene.domain.util.YN
import com.arakene.presentation.util.Action
import com.arakene.presentation.util.BaseViewModel
import com.arakene.presentation.util.CommonEffect
import com.arakene.presentation.util.Effect
import com.arakene.presentation.util.Screens
import com.arakene.presentation.util.action.CalendarAction
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(

    private val getQuotesMonthlyUseCase: GetQuotesMonthlyUseCase,
    private val getLocalQuoteListUseCase: GetLocalQuoteListUseCase,
    private val getLoginStatusUseCase: GetLoginStatusUseCase,
    private val getMonthlyQuotesNonMemberUseCase: GetMonthlyQuotesNonMemberUseCase,
    private val getTodayLocalStreakInfoUseCase: GetTodayLocalStreakInfoUseCase,
    private val saveQuoteAnswerUseCase: SaveQuoteAnswerUseCase,
    private val getMemberQuoteDayUseCase: GetMemberQuoteDayUseCase,
    private val getAccessTokenUseCase: GetAccessTokenUseCase,

) : BaseViewModel() {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM")

    val data = mutableStateOf<MemberMonthlyQuoteResponse?>(null)

    val selectedDayQuote = mutableStateOf("")
    val selectedDay =
        mutableStateOf(CalendarDay(date = LocalDate.now(), position = DayPosition.InDate))

    var answerUiState by mutableStateOf(HomeAnswerUiState())
        private set
    var isMemberSession by mutableStateOf(false)
        private set
    private var answerCoordinator = CalendarAnswerCoordinator()
    private var authCoordinator = HomeAuthBoundRequestCoordinator()
    private var monthlyRevision = 0L
    private var isAnswerEditExplicit = false
    private var loadRevision = 0L
    private var requestedMonth: YearMonth? = null
    private val guestAnswers = mutableMapOf<LocalDate, HomeAnswerUiState>()

    init {
        viewModelScope.launch {
            getLoginStatusUseCase().collect { loggedIn ->
                val changed = updateAuth(HomeAuthContext(loggedIn, if (loggedIn) getAccessTokenUseCase() else null))
                if (changed) requestedMonth?.let(::refreshData)
            }
        }
    }

    // Text changes must not be dropped by BaseViewModel's 250ms navigation throttle.
    override fun emitAction(action: Action) {
        if (action is CalendarAction.ChangeAnswer || action is CalendarAction.RecordAnswer || action is CalendarAction.EditAnswer)
            handleAction(action) else super.emitAction(action)
    }

    override fun handleAction(action: Action) {
        when (val calendarAction = action as CalendarAction) {
            is CalendarAction.ChangeMonth -> {
                changeDayToTargetMonth(calendarAction.target)
                refreshData(calendarAction.target)
            }

            is CalendarAction.SelectDay -> {
                val sameDate = selectedDay.value.date == calendarAction.target.date
                val selection = selectCalendarDay(monthState(), calendarAction.target.date)
                selectedDay.value = calendarAction.target
                selectedDayQuote.value = selection.state.selectedQuote?.quote.orEmpty()
                if (!sameDate) {
                    answerUiState = if (authCoordinator.context?.isLoggedIn == true)
                        answerCoordinator.answerFor(selection.state) else guestAnswers[calendarAction.target.date] ?: selection.state.answer
                    isAnswerEditExplicit = selection.state.isAnswerEditExplicit
                }
            }

            is CalendarAction.ChangeAnswer -> {
                applyMonthState(changeCalendarAnswer(monthState(), calendarAction.answer))
                if (authCoordinator.context?.isLoggedIn == false) guestAnswers[selectedDay.value.date] = answerUiState
            }
            CalendarAction.EditAnswer -> { applyMonthState(editCalendarAnswer(monthState())) }
            CalendarAction.RecordAnswer -> recordAnswer()

            is CalendarAction.ClickBottomQuote -> {
                emitEffect(
                    CommonEffect.Move(
                        Screens.Home(
                            targetYear = selectedDay.value.date.year,
                            targetMonth = selectedDay.value.date.monthValue,
                            targetDay = selectedDay.value.date.dayOfMonth
                        )
                    )
                )
            }

            is CalendarAction.ClickCount -> {
                emitEffect(
                    CommonEffect.Move(
                        Screens.QuoteList(
                            dateFormatter.format(selectedDay.value.date)
                        )
                    )
                )
            }

        }
    }

    override fun emitEffect(effect: Effect) {
        when (effect) {
            is CommonEffect.Refresh -> {
                refreshData(YearMonth.now())
            }

            else -> {
                super.emitEffect(effect)
            }
        }
    }

    private fun refreshData(yearMonth: YearMonth) {
        requestedMonth = yearMonth
        val requestId = ++loadRevision
        viewModelScope.launch {
            observeCurrentAuth()
            if (requestId != loadRevision) return@launch
            val token = authCoordinator.capture() ?: return@launch
            val requestDate = yearMonth.format(dateFormatter)
            if (token.context.isLoggedIn) {
                val answerRevision = answerCoordinator.revision
                val response = getQuotesMonthlyUseCase(requestDate)
                observeCurrentAuth()
                if (!authCoordinator.accepts(token) || requestId != loadRevision) return@launch
                getResponse(response)?.let {
                    if (authCoordinator.accepts(token) && requestId == loadRevision)
                        applyMonthState(answerCoordinator.acceptMonthly(monthState(), it, answerRevision))
                }
            } else {
                getQuotesMonthlyNonMember(requestDate, requestId, token)
            }
        }
    }

    private suspend fun getQuotesMonthlyNonMember(yearMonth: String, requestId: Long, token: HomeAuthBoundRequestToken) {
            val localData = getLocalQuoteListUseCase()
            val localStreakData = getTodayLocalStreakInfoUseCase()
            val response = getMonthlyQuotesNonMemberUseCase(yearMonth)
            observeCurrentAuth()
            if (!authCoordinator.accepts(token) || requestId != loadRevision) return
            getResponse(response)?.let { quotes ->
                if (!authCoordinator.accepts(token) || requestId != loadRevision) return
                data.value = quotes.map { quote ->
                    val localMatchingData =
                        localData.find { it.dailyQuoteSeq == quote.dailyQuoteSeq }
                    MemberQuotesData(
                        dailyQuoteSeq = quote.dailyQuoteSeq,
                        quote = quote.quote,
                        quoteDate = quote.quoteDate,
                        author = quote.author,
                        completed = localMatchingData?.korTyping?.isNotEmpty() == true || localMatchingData?.engTyping?.isNotEmpty() == true,
                        likeYnString = localMatchingData?.likeYn ?: YN.N.type,
                        todayCompleted = localMatchingData?.korTyping?.isNotEmpty() == true || localMatchingData?.engTyping?.isNotEmpty() == true
                    )
                }.let {
                    MemberMonthlyQuoteResponse(
                        memberQuotes = it,
                        monthlySummary = MonthlySummaryData(
                            typingCount = it.count { data -> data.completed },
                            likeCount = it.count { data -> data.likeYn == YN.Y },
                            streakCount = localStreakData?.streakDateCount ?: 0
                        )
                    )
                }
                answerUiState = guestAnswers[selectedDay.value.date] ?: CalendarMonthState.from(data.value!!, selectedDay.value.date).answer
            }
    }

    private fun changeDayToTargetMonth(yearMonth: YearMonth) {
        handleAction(CalendarAction.SelectDay(CalendarDay(LocalDate.of(yearMonth.year, yearMonth.month, 1), DayPosition.InDate)))
    }

    private fun monthState() = CalendarMonthState(data.value, selectedDay.value.date, answerUiState, monthlyRevision, isAnswerEditExplicit)

    private fun applyMonthState(state: CalendarMonthState) {
        data.value = state.data
        answerUiState = state.answer
        monthlyRevision = state.monthlyRevision
        isAnswerEditExplicit = state.isAnswerEditExplicit
        selectedDayQuote.value = state.selectedQuote?.quote.orEmpty()
    }

    private fun updateAuth(context: HomeAuthContext): Boolean {
        isMemberSession = context.isLoggedIn
        val previous = authCoordinator
        authCoordinator = authCoordinator.transition(context)
        val changed = previous.context != null && previous != authCoordinator
        if (changed) {
            loadRevision += 1
            monthlyRevision += 1
            data.value = null
            selectedDayQuote.value = ""
            answerUiState = HomeAnswerUiState()
            isAnswerEditExplicit = false
            answerCoordinator = CalendarAnswerCoordinator()
            guestAnswers.clear()
        }
        return changed
    }

    private suspend fun observeCurrentAuth() {
        val loggedIn = getLoginStatusUseCase().firstOrNull() ?: false
        if (updateAuth(HomeAuthContext(loggedIn, if (loggedIn) getAccessTokenUseCase() else null)))
            requestedMonth?.let(::refreshData)
    }

    private fun recordAnswer() {
        val token = authCoordinator.capture() ?: return
        if (!token.context.isLoggedIn) {
            val outcome = recordHomeAnswerForHome(answerUiState)
            answerUiState = outcome.state
            guestAnswers[selectedDay.value.date] = outcome.state
            emitEffect(CommonEffect.ShowSnackBar(outcome.snackbarMessage))
            return
        }
        val start = answerCoordinator.begin(monthState(), answerUiState, token)
        val request = start.request ?: return
        answerCoordinator = start.coordinator
        answerUiState = start.answer
        viewModelScope.launch {
            observeCurrentAuth()
            if (!authCoordinator.accepts(token)) return@launch
            val result = saveQuoteAnswerUseCase(request.target.dailyQuoteSeq, start.answer.draft)
            observeCurrentAuth()
            if (!authCoordinator.accepts(token)) return@launch
            val response = getResponse(result, useLoading = false)
            val post = answerCoordinator.completePost(monthState(), authCoordinator, request, response)
            answerCoordinator = post.coordinator
            applyMonthState(post.state)
            if (!post.shouldRefresh) return@launch
            emitEffect(CommonEffect.ShowSnackBar(HomeAnswerRecordedSnackbar))
            val refresh = answerCoordinator.captureRefresh(monthState(), request)
            val dailyResult = getMemberQuoteDayUseCase(request.target.date.toString())
            observeCurrentAuth()
            val daily = (dailyResult as? ApiResult.Success)?.data
            applyMonthState(answerCoordinator.completeRefresh(monthState(), authCoordinator, refresh, daily))
        }
    }

}
