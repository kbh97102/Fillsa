package com.arakene.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.arakene.domain.responses.MemberMonthlyQuoteResponse
import com.arakene.domain.responses.MemberQuotesData
import com.arakene.domain.responses.MonthlySummaryData
import com.arakene.domain.usecase.calendar.GetMonthlyQuotesNonMemberUseCase
import com.arakene.domain.usecase.calendar.GetQuotesMonthlyUseCase
import com.arakene.domain.usecase.common.GetLoginStatusUseCase
import com.arakene.domain.usecase.db.GetLocalQuoteListUseCase
import com.arakene.domain.util.YN
import com.arakene.presentation.util.Action
import com.arakene.presentation.util.BaseViewModel
import com.arakene.presentation.util.action.CalendarAction
import com.arakene.presentation.util.CommonEffect
import com.arakene.presentation.util.Effect
import com.arakene.presentation.util.Screens
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
    private val getMonthlyQuotesNonMemberUseCase: GetMonthlyQuotesNonMemberUseCase

) : BaseViewModel() {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM")
    private val dateFormatterWithDay = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    val data = mutableStateOf<MemberMonthlyQuoteResponse?>(null)

    val selectedDayQuote = mutableStateOf("")
    val selectedDay =
        mutableStateOf(CalendarDay(date = LocalDate.now(), position = DayPosition.InDate))

    override fun handleAction(action: Action) {
        when (val calendarAction = action as CalendarAction) {
            is CalendarAction.ChangeMonth -> {
                refreshData(calendarAction.target)
                changeDayToTargetMonth(calendarAction.target)
            }

            is CalendarAction.SelectDay -> {
                val list = data.value?.memberQuotes ?: emptyList()
                selectedDayQuote.value = list.find {
                    it.quoteDate == dateFormatterWithDay.format(
                        calendarAction.target.date
                    )
                }?.quote ?: ""
                selectedDay.value = calendarAction.target
            }

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

            else -> {

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
        viewModelScope.launch {
            val isLogged = getLoginStatusUseCase().firstOrNull() ?: false

            val requestDate = yearMonth.format(dateFormatter)

            if (isLogged) {
                getQuotesMonthly(requestDate)
            } else {
                getQuotesMonthlyNonMember(requestDate)
            }
        }
    }

    private fun getQuotesMonthlyNonMember(yearMonth: String) {
        viewModelScope.launch {
            val localData = getLocalQuoteListUseCase()
            getResponse(getMonthlyQuotesNonMemberUseCase(yearMonth))?.let { quotes ->

                data.value = quotes.map { quote ->
                    val localMatchingData =
                        localData.find { it.dailyQuoteSeq == quote.dailyQuoteSeq }
                    MemberQuotesData(
                        dailyQuoteSeq = quote.dailyQuoteSeq,
                        quote = quote.quote,
                        quoteDate = quote.quoteDate,
                        author = quote.author,
                        typingYnString = if (localMatchingData?.korTyping?.isNotEmpty() == true || localMatchingData?.engTyping?.isNotEmpty() == true) {
                            YN.Y.type
                        } else {
                            YN.N.type
                        },
                        likeYnString = localMatchingData?.likeYn ?: YN.N.type
                    )
                }.let {
                    MemberMonthlyQuoteResponse(
                        memberQuotes = it,
                        monthlySummary = MonthlySummaryData(
                            typingCount = it.count { data -> data.typingYn == YN.Y },
                            likeCount = it.count { data -> data.likeYn == YN.Y },
                        )
                    )
                }
            }
        }
    }

    private fun changeDayToTargetMonth(yearMonth: YearMonth) {
        selectedDay.value =
            CalendarDay(LocalDate.of(yearMonth.year, yearMonth.month, 1), DayPosition.InDate)
    }

    private fun getQuotesMonthly(yearMonth: String) = viewModelScope.launch {
        getResponse(getQuotesMonthlyUseCase(yearMonth))?.let {
            data.value = it
        }
    }

}