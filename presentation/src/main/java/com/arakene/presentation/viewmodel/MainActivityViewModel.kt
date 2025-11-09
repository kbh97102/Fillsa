package com.arakene.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arakene.domain.responses.DailyQuotaNoToken
import com.arakene.domain.responses.MemberStreakResponse
import com.arakene.domain.usecase.common.GetDarkModeTypeUseCase
import com.arakene.domain.usecase.common.GetStreaksUseCase
import com.arakene.domain.usecase.db.SetLocalQuoteForWidgetUseCase
import com.arakene.domain.usecase.home.GetDailyQuoteNoTokenUseCase
import com.arakene.domain.util.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val getDarkModeTypeUseCase: GetDarkModeTypeUseCase,
    private val setLocalQuoteForWidgetUseCase: SetLocalQuoteForWidgetUseCase,
    private val getDailyQuoteNoTokenUseCase: GetDailyQuoteNoTokenUseCase,
    private val getStreaksUseCase: GetStreaksUseCase
) : ViewModel() {

    val streakCount = mutableStateOf<MemberStreakResponse?>(null)

    fun getStreaks(){
        viewModelScope.launch {
            getStreaksUseCase().let {
                when(it){
                    is ApiResult.Success -> {
                        streakCount.value = it.data
                    }
                    else -> {
                        streakCount.value = null
                    }
                }
            }
        }
    }

    fun getDarkModeType() = getDarkModeTypeUseCase()

    fun initWidgetData() {
        viewModelScope.launch {
            getDailyQuoteNoTokenUseCase(
                DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    .format(LocalDate.now())
            ).let {
                if (it is ApiResult.Success) {
                    setLocalQuoteForWidgetUseCase(
                        DailyQuotaNoToken(
                            dailyQuoteSeq = it.data.dailyQuoteSeq,
                            authorUrl = "",
                            engQuote = it.data.engQuote,
                            korQuote = it.data.korQuote,
                            engAuthor = it.data.engAuthor,
                            korAuthor = it.data.korAuthor
                        )
                    )
                }
            }
        }
    }

}