package com.arakene.presentation.util

sealed interface LoginEffect: Effect {

    data object Move: LoginEffect
}