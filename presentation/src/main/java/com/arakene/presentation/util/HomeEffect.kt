package com.arakene.presentation.util

sealed interface HomeEffect: Effect {

    data class OpenImageDialog(
        val quote: String,
        val author: String
    ): HomeEffect

}