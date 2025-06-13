package com.arakene.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.arakene.domain.requests.LikeRequest
import com.arakene.domain.requests.LocalQuoteInfo
import com.arakene.domain.requests.TypingQuoteRequest
import com.arakene.domain.responses.DailyQuoteDto
import com.arakene.domain.usecase.common.GetLoginStatusUseCase
import com.arakene.domain.usecase.db.AddLocalQuoteUseCase
import com.arakene.domain.usecase.db.GetLocalQuoteUseCase
import com.arakene.domain.usecase.db.UpdateLocalQuoteLikeUseCase
import com.arakene.domain.usecase.db.UpdateLocalQuoteUseCase
import com.arakene.domain.usecase.home.GetTypingUseCase
import com.arakene.domain.usecase.home.PostLikeUseCase
import com.arakene.domain.usecase.home.PostTypingUseCase
import com.arakene.domain.util.YN
import com.arakene.presentation.util.Action
import com.arakene.presentation.util.BaseViewModel
import com.arakene.presentation.util.CommonEffect
import com.arakene.presentation.util.Effect
import com.arakene.presentation.util.LocaleType
import com.arakene.presentation.util.Screens
import com.arakene.presentation.util.TypingAction
import com.arakene.presentation.util.TypingEffect
import com.arakene.presentation.util.getDayOfWeekEnglish
import com.arakene.presentation.util.logDebug
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TypingViewModel @Inject constructor(
    private val postLikeUseCase: PostLikeUseCase,
    private val updateLocalQuoteUseCase: UpdateLocalQuoteUseCase,
    private val addLocalQuoteUseCase: AddLocalQuoteUseCase,
    private val getLoginStateUseCase: GetLoginStatusUseCase,
    private val updateLocalQuoteLikeUseCase: UpdateLocalQuoteLikeUseCase,
    private val getTypingUseCase: GetTypingUseCase,
    private val postTypingUseCase: PostTypingUseCase,
    private val getLocalQuoteUseCase: GetLocalQuoteUseCase
) : BaseViewModel() {

    var isLike = mutableStateOf(false)

    val savedKorTyping = mutableStateOf("")
    val savedEngTyping = mutableStateOf("")

    override fun handleAction(action: Action) {
        when (val typingAction = action as TypingAction) {

            is TypingAction.ClickShare -> {
                emitEffect(
                    CommonEffect.Move(
                        Screens.Share(
                            quote = typingAction.quote,
                            author = typingAction.author
                        )
                    )
                )
            }

            is TypingAction.ClickLike -> {
                postLike(typingAction.like, typingAction.dailyQuoteSeq)
            }

            is TypingAction.Back -> {
                saveTyping(
                    typingAction.korTyping,
                    typingAction.engTyping,
                    typingAction.dailyQuote,
                    typingAction.localeType,
                    likeYn = typingAction.isLike
                )
            }

            else -> {

            }
        }
    }

    override fun emitEffect(effect: Effect) {
        when (effect) {
            is TypingEffect.Refresh -> {
                getQuote(effect.seq)
            }

            else -> super.emitEffect(effect)
        }
    }

    private fun getQuote(seq: Int) {
        viewModelScope.launch {

            val loginStatus = getLoginStateUseCase().firstOrNull() ?: false

            if (loginStatus) {
                getResponse(getTypingUseCase(seq))?.let { response ->
                    savedEngTyping.value = response.typingEngQuote ?: ""
                    savedKorTyping.value = response.typingKorQuote ?: ""
                    isLike.value = YN.getYN(response.likeYn) == YN.Y
                }
            } else {
                getLocalQuoteUseCase(seq)?.let {
                    savedEngTyping.value = it.engTyping
                    savedKorTyping.value = it.korTyping
                    isLike.value = YN.getYN(it.likeYn) == YN.Y
                }
            }
        }
    }

    private fun saveTyping(
        korTyping: String,
        engTyping: String,
        dailyQuoteDto: DailyQuoteDto,
        localeType: LocaleType,
        likeYn: Boolean
    ) {

        CoroutineScope(Dispatchers.IO).launch {
            val loginStatus = getLoginStateUseCase().firstOrNull() ?: false

            if (!loginStatus) {
                logDebug("date ${dailyQuoteDto.quoteDate}")

                addLocalQuoteUseCase(
                    LocalQuoteInfo(
                        memo = "",
                        korAuthor = dailyQuoteDto.korAuthor ?: "",
                        korQuote = dailyQuoteDto.korQuote ?: "",
                        engQuote = dailyQuoteDto.engQuote ?: "",
                        engAuthor = dailyQuoteDto.engAuthor ?: "",
                        korTyping = korTyping,
                        engTyping = engTyping,
                        dailyQuoteSeq = dailyQuoteDto.dailyQuoteSeq,
                        likeYn = if (likeYn) {
                            YN.Y.type
                        } else {
                            YN.N.type
                        },
                        date = dailyQuoteDto.quoteDate,
                        dayOfWeek = getDayOfWeekEnglish(dateStr = dailyQuoteDto.quoteDate)
                    )
                )
            } else {
                postTypingUseCase(
                    dailyQuoteSeq = dailyQuoteDto.dailyQuoteSeq,
                    request = TypingQuoteRequest(
                        typingKorQuote = korTyping,
                        typingEngQuote = engTyping
                    )
                )
            }
        }

    }

    private fun postLike(like: Boolean, dailyQuoteSeq: Int) = viewModelScope.launch {

        val isLogged = getLoginStateUseCase().firstOrNull() ?: false

        if (isLogged) {
            postLikeUseCase(
                LikeRequest(
                    likeYn =
                        if (like) {
                            YN.Y.type
                        } else {
                            YN.N.type
                        }
                ),
                dailyQuoteSeq = dailyQuoteSeq
            )
        } else {
            updateLocalQuoteLikeUseCase(
                likeYN = if (like) {
                    YN.Y
                } else {
                    YN.N
                }, seq = dailyQuoteSeq
            )
        }
    }

}