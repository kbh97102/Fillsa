package com.arakene.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.arakene.domain.requests.LikeRequest
import com.arakene.domain.requests.Pageable
import com.arakene.domain.responses.MemberQuotesResponse
import com.arakene.domain.usecase.list.GetQuotesListUseCase
import com.arakene.presentation.util.Action
import com.arakene.presentation.util.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val getQuotesListUseCase: GetQuotesListUseCase
): BaseViewModel() {

    val list = mutableStateOf(
        emptyList<MemberQuotesResponse>()
    )

    override fun handleAction(action: Action) {
        TODO("Not yet implemented")
    }

    fun getQuotesList(pageable: Pageable, request: LikeRequest){
        viewModelScope.launch {
            getResponse(getQuotesListUseCase(pageable, request))?.let {
                list.value = it.content
            }
        }
    }

}