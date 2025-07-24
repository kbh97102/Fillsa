package com.arakene.presentation.util

import android.net.Uri
import java.time.LocalDate

sealed interface HomeEffect : Effect {

    data class OpenImageDialog(
        val quote: String,
        val author: String
    ) : HomeEffect

    data class Refresh(val date: LocalDate) : HomeEffect

    data class SetDate(val date: LocalDate) : HomeEffect

    data class ProcessImage(val uri: String): HomeEffect
}