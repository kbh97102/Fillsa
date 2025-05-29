package com.arakene.presentation.viewmodel

import com.arakene.domain.usecase.GetDailyQuotaNoTokenUseCase
import com.arakene.presentation.util.Action
import com.arakene.presentation.util.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getDailyQuotaNoTokenUseCase: GetDailyQuotaNoTokenUseCase
): BaseViewModel() {


    override fun handleAction(action: Action) {
        TODO("Not yet implemented")
    }


}