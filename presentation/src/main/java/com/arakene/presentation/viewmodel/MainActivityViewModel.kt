package com.arakene.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arakene.domain.responses.DailyQuotaNoToken
import com.arakene.domain.responses.PopupResponse
import com.arakene.domain.usecase.common.GetDarkModeTypeUseCase
import com.arakene.domain.usecase.common.GetPopupGeneralUseCase
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
    private val getPopupGeneralUseCase: GetPopupGeneralUseCase
) : ViewModel() {

    val popupResponse = mutableStateOf<PopupResponse?>(null)

    fun getPopupGeneral(){
        viewModelScope.launch {
            getPopupGeneralUseCase().let {
                if (it is ApiResult.Success){
                    popupResponse.value = it.data
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