package com.arakene.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.arakene.domain.requests.LikeRequest
import com.arakene.domain.requests.LocalQuoteInfo
import com.arakene.domain.responses.DailyQuoteDto
import com.arakene.domain.usecase.common.GetLoginStatusUseCase
import com.arakene.domain.usecase.db.AddLocalQuoteUseCase
import com.arakene.domain.usecase.db.UpdateLocalQuoteUseCase
import com.arakene.domain.usecase.home.PostLikeUseCase
import com.arakene.domain.util.YN
import com.arakene.presentation.util.Action
import com.arakene.presentation.util.BaseViewModel
import com.arakene.presentation.util.CommonEffect
import com.arakene.presentation.util.LocaleType
import com.arakene.presentation.util.Screens
import com.arakene.presentation.util.TypingAction
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
    private val getLoginStateUseCase: GetLoginStatusUseCase
) : BaseViewModel() {

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
                saveTyping(typingAction.typing, typingAction.dailyQuote, typingAction.localeType)
            }

            else -> {

            }
        }

    }

    private fun saveTyping(typing: String, dailyQuoteDto: DailyQuoteDto, localeType: LocaleType) {

        CoroutineScope(Dispatchers.IO).launch {
            val loginStatus = getLoginStateUseCase().firstOrNull() ?: false

            if (!loginStatus) {
                addLocalQuoteUseCase(
                    LocalQuoteInfo(
                        memo = "",
                        korAuthor = dailyQuoteDto.korAuthor ?: "",
                        korQuote = dailyQuoteDto.korQuote ?: "",
                        engQuote = dailyQuoteDto.engQuote ?: "",
                        engAuthor = dailyQuoteDto.engAuthor ?: "",
                        korTyping = if (localeType == LocaleType.KOR) {
                            typing
                        } else "",
                        engTyping = if (localeType == LocaleType.ENG) {
                            typing
                        } else "",
                        dailyQuoteSeq = dailyQuoteDto.dailyQuoteSeq,
                        likeYn = dailyQuoteDto.likeYn,
                        date = "",
                        dayOfWeek = ""
                    )
                )
            }
        }

    }

    private fun postLike(like: Boolean, dailyQuoteSeq: Int) = viewModelScope.launch {
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
    }

}