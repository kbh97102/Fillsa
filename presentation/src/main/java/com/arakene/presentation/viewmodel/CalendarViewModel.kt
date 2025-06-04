package com.arakene.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.arakene.domain.responses.MemberMonthlyQuoteResponse
import com.arakene.domain.usecase.calendar.GetQuotesMonthlyUseCase
import com.arakene.presentation.util.Action
import com.arakene.presentation.util.BaseViewModel
import com.arakene.presentation.util.CalendarAction
import com.arakene.presentation.util.CommonEffect
import com.arakene.presentation.util.Effect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(

    private val getQuotesMonthlyUseCase: GetQuotesMonthlyUseCase

) : BaseViewModel() {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM")

    val data = mutableStateOf<MemberMonthlyQuoteResponse?>(null)

    init {

        // TODO: 데이터 초기화 좋은 방법 유튜브에서 봤던거 적용해보기
        emitEffect(CommonEffect.Refresh)
    }

    override fun handleAction(action: Action) {
        when (val calendarAction = action as CalendarAction) {
            is CalendarAction.ChangeMonth -> {
                getQuotesMonthly(calendarAction.target.format(dateFormatter))
            }

            else -> {

            }
        }
    }

    override fun emitEffect(effect: Effect) {
        when (effect) {
            is CommonEffect.Refresh -> {
                getQuotesMonthly(dateFormatter.format(LocalDate.now()))
            }

            else -> {
                super.emitEffect(effect)
            }
        }
    }

    private fun getQuotesMonthly(yearMonth: String) = viewModelScope.launch {
        getResponse(getQuotesMonthlyUseCase(yearMonth))?.let {
            data.value = it
        }
    }

}