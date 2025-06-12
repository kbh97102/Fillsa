package com.arakene.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.arakene.domain.responses.MemberMonthlyQuoteResponse
import com.arakene.domain.responses.MemberQuotesData
import com.arakene.domain.responses.MonthlySummaryData
import com.arakene.domain.usecase.calendar.GetQuotesMonthlyUseCase
import com.arakene.domain.usecase.common.GetLoginStatusUseCase
import com.arakene.domain.usecase.db.GetLocalQuoteUseCase
import com.arakene.domain.util.YN
import com.arakene.presentation.util.Action
import com.arakene.presentation.util.BaseViewModel
import com.arakene.presentation.util.CalendarAction
import com.arakene.presentation.util.CommonEffect
import com.arakene.presentation.util.Effect
import com.arakene.presentation.util.Screens
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(

    private val getQuotesMonthlyUseCase: GetQuotesMonthlyUseCase,
    private val getLocalQuoteUseCase: GetLocalQuoteUseCase,
    private val getLoginStatusUseCase: GetLoginStatusUseCase

) : BaseViewModel() {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM")

    val data = mutableStateOf<MemberMonthlyQuoteResponse?>(null)

    val selectedDayQuote = mutableStateOf("")

    init {

        // TODO: 데이터 초기화 좋은 방법 유튜브에서 봤던거 적용해보기
        emitEffect(CommonEffect.Refresh)
    }

    override fun handleAction(action: Action) {
        when (val calendarAction = action as CalendarAction) {
            is CalendarAction.ChangeMonth -> {
                refreshData(calendarAction.target.format(dateFormatter))
            }

            is CalendarAction.SelectDay -> {
                val list = data.value?.memberQuotes ?: emptyList()

                selectedDayQuote.value = list.find {
                    it.quoteDate == dateFormatter.format(
                        calendarAction.target.date
                    )
                }?.quote ?: ""

            }

            is CalendarAction.ClickBottomQuote -> {
                emitEffect(
                    CommonEffect.Move(
                        Screens.QuoteList
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
                refreshData(dateFormatter.format(LocalDate.now()))
            }

            else -> {
                super.emitEffect(effect)
            }
        }
    }

    private fun refreshData(yearMonth: String) {
        viewModelScope.launch {
            val isLogged = getLoginStatusUseCase().firstOrNull() ?: false

            if (isLogged) {
                getQuotesMonthly(yearMonth)
            } else {
                getQuotesLocal()
            }

        }
    }

    private suspend fun getQuotesMonthly(yearMonth: String) =
        getResponse(getQuotesMonthlyUseCase(yearMonth))?.let {
            data.value = it
        }

    private suspend fun getQuotesLocal() {
        data.value = getLocalQuoteUseCase().map {
            MemberQuotesData(
                dailyQuoteSeq = it.dailyQuoteSeq,
                quoteDate = it.date,
                quote = it.korQuote,
                author = it.korAuthor,
                typingYnString = if (it.typing.isEmpty()) {
                    YN.N.type
                } else {
                    YN.Y.type
                },
                likeYnString = it.likeYn
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