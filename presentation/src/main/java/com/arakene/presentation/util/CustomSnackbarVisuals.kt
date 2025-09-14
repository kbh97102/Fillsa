package com.arakene.presentation.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.ui.graphics.vector.ImageVector

// 1. 아이콘 정보를 담을 수 있는 우리만의 SnackbarVisuals 정의
data class CustomSnackbarVisuals(
    override val message: String,
    val displayIcon: Boolean = true,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false,
    override val duration: SnackbarDuration = SnackbarDuration.Short
) : SnackbarVisuals