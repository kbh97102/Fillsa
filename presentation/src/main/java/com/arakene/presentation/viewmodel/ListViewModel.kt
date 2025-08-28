package com.arakene.presentation.viewmodel

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.arakene.domain.requests.MemoRequest
import com.arakene.domain.responses.MemberQuotesResponse
import com.arakene.domain.usecase.common.GetLoginStatusUseCase
import com.arakene.domain.usecase.db.GetLocalQuotePagingUseCase
import com.arakene.domain.usecase.db.UpdateLocalQuoteMemoUseCase
import com.arakene.domain.usecase.list.GetQuotesListUseCase
import com.arakene.domain.usecase.list.PostSaveMemoUseCase
import com.arakene.domain.util.YN
import com.arakene.presentation.util.Action
import com.arakene.presentation.util.BaseViewModel
import com.arakene.presentation.util.CommonEffect
import com.arakene.presentation.util.Screens
import com.arakene.presentation.util.action.QuoteListAction
import com.arakene.presentation.util.state.QuoteListState
import com.arakene.presentation.util.toLocalDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val getQuotesListUseCase: GetQuotesListUseCase,
    private val postSaveMemoUseCase: PostSaveMemoUseCase,
    private val getLocalQuotePagingUseCase: GetLocalQuotePagingUseCase,
    private val getLoginStatusUseCase: GetLoginStatusUseCase,
    private val updateLocalQuoteMemoUseCase: UpdateLocalQuoteMemoUseCase,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val dateFormatterWithDay = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private val startDay =
        savedStateHandle.get<String>("startDate")?.toLocalDate() ?: LocalDate.now()

    private val endDay =
        savedStateHandle.get<String>("endDate")?.toLocalDate() ?: startDay.plusDays(7)

    private val _state = MutableStateFlow(
        QuoteListState(
            startDate = startDay,
            endDate = endDay
        )
    )

    val state get() = _state.asStateFlow()

    private val endDateFlow = snapshotFlow {
        state.value.endDate
    }

    private val startDateFlow = snapshotFlow {
        state.value.startDate
    }

    private val likeFlow = snapshotFlow { state.value.likeFilter }

    val quoteListFlow = combine(
        endDateFlow,
        startDateFlow,
        likeFlow
    ){ start, end, like ->
        Triple(start, end, like)
    }.flatMapLatest {
        val (startDate, endDate, likeFilter) = it
        getQuotesListUseCase(
            likeYn = if (likeFilter) {
                YN.Y.type
            } else {
                YN.N.type
            },
            startDate = dateFormatterWithDay.format(startDate),
            endDate = dateFormatterWithDay.format(endDate)
        )
    }.cachedIn(viewModelScope)

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun updateState(update: (QuoteListState) -> QuoteListState) {
        _state.value = update(_state.value)
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

            is QuoteListAction.UpdateLikeFilter -> {
                updateState {
                    it.copy(likeFilter = listAction.liked)
                }
            }

            is QuoteListAction.SelectDate -> {
                updateState {
                    it.copy(
                        startDate = listAction.start,
                        endDate = listAction.end,
                        displayCalendar = false
                    )
                }
            }

            is QuoteListAction.ClickDateSection -> {
                updateState {
                    it.copy(displayCalendar = !it.displayCalendar)
                }
            }
        }
    }

    fun getLocalQuotesList(likeYn: Boolean) = getLocalQuotePagingUseCase(
        if (likeYn) {
            YN.Y
        } else {
            YN.N
        }
    )
        .map { pagingData ->
            pagingData.map {
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
                    memoYnString = if (it.memo.isEmpty()) {
                        YN.N.type
                    } else {
                        YN.Y.type
                    },
                    imagePath = "",
                    likeYnString = it.likeYn,
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