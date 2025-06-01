package com.arakene.presentation.viewmodel

import com.arakene.presentation.util.Action
import com.arakene.presentation.util.BaseViewModel
import com.arakene.presentation.util.CommonEffect
import com.arakene.presentation.util.Screens
import com.arakene.presentation.util.TypingAction

class TypingViewModel : BaseViewModel() {

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

            else -> {

            }
        }

    }


}