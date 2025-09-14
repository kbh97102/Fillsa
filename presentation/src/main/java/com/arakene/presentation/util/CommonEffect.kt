package com.arakene.presentation.util

import com.arakene.domain.util.CommonError

interface CommonEffect : Effect {

    data class Move(val screen: Screens) : CommonEffect
    data object PopBackStack: CommonEffect
    data class ShowDialog(val dialogData: DialogData) : CommonEffect
    data object Refresh : CommonEffect
    data class OpenUri(val uri: String) : CommonEffect
    data class ShowSnackBar(val message: String) : CommonEffect
    data object HideKeyboard: CommonEffect
    data class EmitError(val commonError: CommonError): CommonEffect
}