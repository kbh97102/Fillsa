package com.arakene.presentation.util

sealed interface LoginEffect: Effect {

    data class Move(val screen: Screens): LoginEffect

}