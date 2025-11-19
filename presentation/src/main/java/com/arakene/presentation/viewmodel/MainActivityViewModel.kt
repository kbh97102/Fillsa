package com.arakene.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arakene.domain.responses.DailyQuotaNoToken
import com.arakene.domain.responses.MemberStreakResponse
import com.arakene.domain.responses.PopupResponse
import com.arakene.domain.usecase.common.GetDarkModeTypeUseCase
import com.arakene.domain.usecase.common.GetMemberStreaksUseCase
import com.arakene.domain.usecase.common.GetPopupGeneralUseCase
import com.arakene.domain.usecase.common.GetPopupVersionUpdateUseCase
import com.arakene.domain.usecase.db.AddHiddenPopupUseCase
import com.arakene.domain.usecase.db.CheckPopupIsHiddenUseCase
import com.arakene.domain.usecase.db.SetLocalQuoteForWidgetUseCase
import com.arakene.domain.usecase.home.GetDailyQuoteNoTokenUseCase
import com.arakene.domain.util.ApiResult
import com.arakene.presentation.util.GeneralPopupType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.PriorityQueue
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val getDarkModeTypeUseCase: GetDarkModeTypeUseCase,
    private val setLocalQuoteForWidgetUseCase: SetLocalQuoteForWidgetUseCase,
    private val getDailyQuoteNoTokenUseCase: GetDailyQuoteNoTokenUseCase,
    private val getMemberStreaksUseCase: GetMemberStreaksUseCase,
    private val getPopupGeneralUseCase: GetPopupGeneralUseCase,
    private val getPopupVersionUpdateUseCase: GetPopupVersionUpdateUseCase,
    private val addHiddenPopupUseCase: AddHiddenPopupUseCase,
    private val isHiddenUseCase: CheckPopupIsHiddenUseCase
) : ViewModel() {

    private val popupResponsesQueue =
        PriorityQueue<PopupResponse>(compareBy { GeneralPopupType.valueOf(it.popupType) })

    val streakCount = mutableStateOf<MemberStreakResponse?>(null)

    val popupResponse = MutableSharedFlow<PopupResponse?>()

    fun addHiddenPopUp(seq: Int) {
        viewModelScope.launch {
            addHiddenPopupUseCase(seq)
        }
    }

    fun getPopupGeneral() {
        viewModelScope.launch {
            launch {
                getPopupGeneralUseCase().let {
                    if (it is ApiResult.Success) {
                        if (isHiddenUseCase(it.data.popupSeq)) {
                            return@let
                        }
                        popupResponsesQueue.offer(it.data)
                    }
                }

                getPopupVersionUpdateUseCase().let {
                    if (it is ApiResult.Success) {
                        popupResponsesQueue.offer(it.data)
                    }
                }
            }.join()

            getNextGeneralPopUp()
        }
    }

    fun getNextGeneralPopUp() {
        viewModelScope.launch {
            popupResponse.emit(popupResponsesQueue.poll())
        }
    }

    fun getStreakInfo() {
        viewModelScope.launch {
            getMemberStreaksUseCase().let {
                streakCount.value = it
            }
        }
    }

    fun updateStreakInfo(route: String?) {
        when {
            route?.contains("Home") == true || route?.contains("Calendar") == true || route?.contains(
                "QuoteList"
            ) == true -> {
                getStreakInfo()
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