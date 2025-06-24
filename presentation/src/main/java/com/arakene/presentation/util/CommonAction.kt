package com.arakene.presentation.util

sealed interface CommonAction: Action {

    data object PopBackStack: CommonAction

}