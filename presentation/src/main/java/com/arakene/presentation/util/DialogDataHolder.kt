package com.arakene.presentation.util

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
class ImageDialogDataHolder{
    var show by mutableStateOf(false)
    var quote by mutableStateOf("")
    var author by mutableStateOf("")
}