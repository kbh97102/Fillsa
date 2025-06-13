package com.arakene.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.arakene.domain.requests.MemoRequest
import com.arakene.domain.responses.MemberQuotesResponse
import com.arakene.domain.usecase.common.GetLoginStatusUseCase
import com.arakene.domain.usecase.db.GetLocalQuotePagingUseCase
import com.arakene.domain.usecase.db.UpdateLocalQuoteMemoUseCase
import com.arakene.domain.usecase.db.UpdateLocalQuoteUseCase
import com.arakene.domain.usecase.list.GetQuotesListUseCase
import com.arakene.domain.usecase.list.PostSaveMemoUseCase
import com.arakene.domain.util.YN
import com.arakene.presentation.util.Action
import com.arakene.presentation.util.BaseViewModel
import com.arakene.presentation.util.CommonEffect
import com.arakene.presentation.util.Effect
import com.arakene.presentation.util.QuoteListAction
import com.arakene.presentation.util.QuoteListEffect
import com.arakene.presentation.util.Screens
import com.arakene.presentation.util.logDebug
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val getQuotesListUseCase: GetQuotesListUseCase,
    private val postSaveMemoUseCase: PostSaveMemoUseCase,
    private val getLocalQuotePagingUseCase: GetLocalQuotePagingUseCase,
    private val getLoginStatusUseCase: GetLoginStatusUseCase,
    private val updateLocalQuoteMemoUseCase: UpdateLocalQuoteMemoUseCase
) : BaseViewModel() {

    val imageUri = mutableStateOf("")

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

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
                            memberQuoteSeq = data.memberQuoteSeq.toString(),
                            imagePath = data.imagePath ?: ""
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

    override fun emitEffect(effect: Effect) {
        when(effect) {
            is QuoteListEffect.Refresh -> {
                getQuotesList(effect.likeYn)
            }
            else -> {
                super.emitEffect(effect)
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

    fun getLocalQuotesList() = getLocalQuotePagingUseCase()
        .map { pagingData ->
            pagingData.map {
                logDebug("Mapping? ${it}")
                MemberQuotesResponse(
                    memberQuoteSeq = it.dailyQuoteSeq,
                    quoteDate = it.date,
                    quoteDayOfWeek = it.dayOfWeek,
                    korQuote = it.korQuote,
                    engQuote = it.engQuote,
                    korAuthor = it.korAuthor,
                    engAuthor = it.engAuthor,
                    authorUrl = "",
                    memo = it.memo,
                    memoYn = if (it.memo.isEmpty()) {
                        YN.N.type
                    } else {
                        YN.Y.type
                    },
                    imagePath = "",
                    likeYn = it.likeYn,
                )
            }
        }
        .cachedIn(viewModelScope)


    val isLogged = getLoginStatusUseCase()

    fun postSaveMemo(memberQuoteSeq: String, memo: String) {
        scope.launch {
            val isLogged = getLoginStatusUseCase().firstOrNull() ?: false

            if (isLogged) {
                postSaveMemoUseCase(memberQuoteSeq = memberQuoteSeq, MemoRequest(memo))
            } else {
                updateLocalQuoteMemoUseCase(memo, seq = memberQuoteSeq.toInt())
            }

        }
    }

}