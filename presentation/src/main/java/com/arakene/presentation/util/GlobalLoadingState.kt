package com.arakene.presentation.util

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateMapOf

@Stable
class GlobalLoadingState {
    private val owners = mutableStateMapOf<Any, Unit>()

    val isLoading: Boolean
        get() = owners.isNotEmpty()

    fun set(owner: Any, loading: Boolean) {
        if (loading) {
            owners[owner] = Unit
        } else {
            owners.remove(owner)
        }
    }
}
