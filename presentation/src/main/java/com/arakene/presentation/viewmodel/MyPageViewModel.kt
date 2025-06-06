package com.arakene.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.arakene.domain.usecase.GetNoticeUseCase
import com.arakene.presentation.util.Action
import com.arakene.presentation.util.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val getNoticeUseCase: GetNoticeUseCase
) : BaseViewModel() {


    val getNotice = getNoticeUseCase()
        .cachedIn(viewModelScope)


    override fun handleAction(action: Action) {
        TODO("Not yet implemented")
    }
}