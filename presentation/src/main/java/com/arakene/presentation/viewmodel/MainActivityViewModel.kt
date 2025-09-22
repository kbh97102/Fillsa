package com.arakene.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.arakene.domain.usecase.common.GetDarkModeTypeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val getDarkModeTypeUseCase: GetDarkModeTypeUseCase
) : ViewModel() {


    fun getDarkModeType() = getDarkModeTypeUseCase()

}