package com.arakene.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.arakene.domain.responses.DailyQuoteDto
import com.arakene.domain.usecase.GetDailyQuotaNoTokenUseCase
import com.arakene.presentation.util.Action
import com.arakene.presentation.util.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getDailyQuotaNoTokenUseCase: GetDailyQuotaNoTokenUseCase
): BaseViewModel() {

    val date = mutableStateOf(LocalDate.now())

    var currentQuota by mutableStateOf(DailyQuoteDto())

    override fun handleAction(action: Action) {
        TODO("Not yet implemented")
    }

    fun getDailyQuotaNoToken(date: String) = viewModelScope.launch {
        getResponse(getDailyQuotaNoTokenUseCase(date))?.let {
            currentQuota = DailyQuoteDto(
                likeYn = "N",
                imagePath = "",
                dailyQuoteSeq = it.dailyQuoteSeq,
                korQuote = it.korQuote,
                engQuote = it.engQuote,
                korAuthor = it.korAuthor,
                engAuthor = it.engAuthor,
                authorUrl = it.authorUrl
            )
        }
    }

}