package com.arakene.presentation.viewmodel

import com.arakene.domain.usecase.GetAdStateUseCase
import com.arakene.presentation.util.Action
import com.arakene.presentation.util.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AdViewModel @Inject constructor(
    private val getAdStateUseCase: GetAdStateUseCase
): BaseViewModel() {


    val adState = getAdStateUseCase()

    override fun handleAction(action: Action) {
        TODO("Not yet implemented")
    }
}