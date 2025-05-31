package com.arakene.presentation.viewmodel


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.arakene.domain.requests.LikeRequest
import com.arakene.domain.responses.DailyQuoteDto
import com.arakene.domain.usecase.common.GetLoginStatusUseCase
import com.arakene.domain.usecase.home.GetDailyQuoteNoTokenUseCase
import com.arakene.domain.usecase.home.GetDailyQuoteUseCase
import com.arakene.domain.usecase.home.PostLikeUseCase
import com.arakene.presentation.util.Action
import com.arakene.presentation.util.BaseViewModel
import com.arakene.presentation.util.CommonEffect
import com.arakene.presentation.util.HomeAction
import com.arakene.presentation.util.HomeEffect
import com.arakene.presentation.util.Screens
import com.arakene.presentation.util.YN
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getDailyQuoteNoTokenUseCase: GetDailyQuoteNoTokenUseCase,
    private val getDailyQuoteUseCase: GetDailyQuoteUseCase,
    private val getLoginStatusUseCase: GetLoginStatusUseCase,
    private val postLikeUseCase: PostLikeUseCase
) : BaseViewModel() {

    private val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    val isLogged = getLoginStatusUseCase()

    val date = mutableStateOf(LocalDate.now())

    var currentQuota by mutableStateOf(DailyQuoteDto())

    var isLike = mutableStateOf(false)

    override fun handleAction(action: Action) {
        when (val homeAction = action as HomeAction) {
            is HomeAction.ClickBefore -> {
                date.value = date.value.minusDays(1)
                refresh()
            }

            is HomeAction.ClickNext -> {
                date.value = date.value.plusDays(1)
                refresh()
            }

            is HomeAction.Refresh -> {
                refresh()
            }

            is HomeAction.ClickImage -> {
                clickImage(homeAction)
            }

            is HomeAction.ClickLike -> {
                postLike()
            }

            else -> {

            }
        }

    }


    private fun clickImage(action: HomeAction.ClickImage) {
        if (!action.isLogged) {
            emitEffect(CommonEffect.Move(Screens.Login))
        } else {
            emitEffect(
                HomeEffect.OpenImageDialog(
                    quote = action.quote,
                    author = action.author
                )
            )
        }
    }

    private fun postLike() = viewModelScope.launch {
        isLike.value = !isLike.value
        postLikeUseCase(
            LikeRequest(
                if (isLike.value) {
                    YN.Y.type
                } else {
                    YN.N.type
                }
            ),
            dailyQuoteSeq = currentQuota.dailyQuoteSeq
        )
    }

    private fun refresh() = viewModelScope.launch {
        val isLogged = getLoginStatusUseCase().firstOrNull() ?: false
        val convertedDate = convertDate(date.value)

        if (isLogged) {
            getDailyQuote(convertedDate)
        } else {
            getDailyQuoteNoToken(convertedDate)
        }
    }

    private fun getDailyQuote(date: String) = viewModelScope.launch {
        getResponse(getDailyQuoteUseCase(date))?.let {
            currentQuota = it
            isLike.value = it.likeYn == YN.Y.type
        }
    }

    private fun getDailyQuoteNoToken(date: String) = viewModelScope.launch {
        getResponse(getDailyQuoteNoTokenUseCase(date))?.let {
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

    private fun convertDate(date: LocalDate) = dateFormat.format(date)

}