package com.arakene.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.arakene.domain.usecase.common.GetIsDarkModeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val getIsDarkModeUseCase: GetIsDarkModeUseCase
) : ViewModel() {


    fun getIsDarkMode() = getIsDarkModeUseCase()

}