package com.arakene.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.arakene.domain.requests.LikeRequest
import com.arakene.domain.responses.MemberQuotesResponse
import com.arakene.domain.usecase.list.GetQuotesListUseCase
import com.arakene.presentation.util.Action
import com.arakene.presentation.util.BaseViewModel
import com.arakene.presentation.util.YN
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val getQuotesListUseCase: GetQuotesListUseCase
) : BaseViewModel() {

    override fun handleAction(action: Action) {
        TODO("Not yet implemented")
    }

    fun getQuotesList(likeYn: Boolean) = getQuotesListUseCase(
        LikeRequest(
            if (likeYn) {
                YN.Y.type
            } else {
                YN.N.type
            }
        )
    ).cachedIn(viewModelScope)


}