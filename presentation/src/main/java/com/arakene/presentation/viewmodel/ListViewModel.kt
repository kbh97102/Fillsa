package com.arakene.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.arakene.domain.requests.LikeRequest
import com.arakene.domain.requests.MemoRequest
import com.arakene.domain.usecase.list.GetQuotesListUseCase
import com.arakene.domain.usecase.list.PostSaveMemoUseCase
import com.arakene.presentation.util.Action
import com.arakene.presentation.util.BaseViewModel
import com.arakene.presentation.util.YN
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val getQuotesListUseCase: GetQuotesListUseCase,
    private val postSaveMemoUseCase: PostSaveMemoUseCase
) : BaseViewModel() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

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

    fun postSaveMemo(memberQuoteSeq: String, memo: String) {
        scope.launch {
            postSaveMemoUseCase(memberQuoteSeq = memberQuoteSeq, MemoRequest(memo))
        }
    }

}