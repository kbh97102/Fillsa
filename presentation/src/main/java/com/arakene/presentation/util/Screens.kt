package com.arakene.presentation.util

import kotlinx.serialization.Serializable

sealed interface Screens {
    @Serializable
    data object Login: Screens

    @Serializable
    data object Home: Screens
}