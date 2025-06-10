package com.arakene.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.arakene.domain.requests.MemoRequest
import com.arakene.domain.usecase.home.GetImageUriUseCase
import com.arakene.domain.usecase.list.GetQuotesListUseCase
import com.arakene.domain.usecase.list.PostSaveMemoUseCase
import com.arakene.domain.util.YN
import com.arakene.presentation.util.Action
import com.arakene.presentation.util.BaseViewModel
import com.arakene.presentation.util.CommonEffect
import com.arakene.presentation.util.QuoteListAction
import com.arakene.presentation.util.Screens
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val getQuotesListUseCase: GetQuotesListUseCase,
    private val postSaveMemoUseCase: PostSaveMemoUseCase,
    private val getImageUriUseCase: GetImageUriUseCase
) : BaseViewModel() {

    val imageUri = mutableStateOf("")

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        getImageUri()
    }

    override fun handleAction(action: Action) {
        when (val listAction = action as QuoteListAction) {
            is QuoteListAction.ClickItem -> {
                val data = listAction.memberQuotesResponse
                emitEffect(
                    CommonEffect.Move(
                        Screens.QuoteDetail(
                            memo = data.memo,
                            authorUrl = data.authorUrl,
                            korAuthor = data.korAuthor ?: "",
                            engAuthor = data.engAuthor ?: "",
                            korQuote = data.korQuote ?: "",
                            engQuote = data.engQuote ?: "",
                            memberQuoteSeq = data.memberQuoteSeq.toString()
                        )
                    )
                )
            }

            is QuoteListAction.ClickMemo -> {
                emitEffect(
                    CommonEffect.Move(
                        Screens.MemoInsert(
                            memberQuoteSeq = listAction.memberQuoteSeq,
                            savedMemo = listAction.savedMemo
                        )
                    )
                )
            }

        }

    }

    private fun getImageUri() {
        viewModelScope.launch {
            getImageUriUseCase().collectLatest {
                imageUri.value = it
            }
        }
    }

    fun getQuotesList(likeYn: Boolean) = getQuotesListUseCase(
        if (likeYn) {
            YN.Y.type
        } else {
            YN.N.type
        }
    ).cachedIn(viewModelScope)

    fun postSaveMemo(memberQuoteSeq: String, memo: String) {
        scope.launch {
            postSaveMemoUseCase(memberQuoteSeq = memberQuoteSeq, MemoRequest(memo))
        }
    }

}