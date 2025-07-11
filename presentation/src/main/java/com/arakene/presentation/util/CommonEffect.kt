package com.arakene.presentation.util

interface CommonEffect : Effect {

    data class Move(val screen: Screens) : CommonEffect
    data object PopBackStack: CommonEffect
    data class ShowDialog(val dialogData: DialogData) : CommonEffect
    data object Refresh : CommonEffect
    data class OpenUri(val uri: String) : CommonEffect
    data class ShowSnackBar(val message: String) : CommonEffect
    data object HideKeyboard: CommonEffect
}