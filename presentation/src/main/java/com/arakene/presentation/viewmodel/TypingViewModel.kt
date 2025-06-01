package com.arakene.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.arakene.domain.requests.LikeRequest
import com.arakene.domain.usecase.home.PostLikeUseCase
import com.arakene.presentation.util.Action
import com.arakene.presentation.util.BaseViewModel
import com.arakene.presentation.util.CommonEffect
import com.arakene.presentation.util.Screens
import com.arakene.presentation.util.TypingAction
import com.arakene.presentation.util.YN
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TypingViewModel @Inject constructor(
    private val postLikeUseCase: PostLikeUseCase
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

            else -> {

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