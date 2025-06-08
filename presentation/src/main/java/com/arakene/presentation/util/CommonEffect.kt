package com.arakene.presentation.util

interface CommonEffect : Effect {

    data class Move(val screen: Screens) : CommonEffect
    data class ShowDialog(val dialogData: DialogData): CommonEffect
    data object Refresh: CommonEffect
    data class OpenUri(val uri: String): CommonEffect
}