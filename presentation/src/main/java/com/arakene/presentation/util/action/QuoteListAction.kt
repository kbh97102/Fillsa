package com.arakene.presentation.util.action

import com.arakene.domain.responses.MemberQuotesResponse
import com.arakene.presentation.util.Action
import java.time.LocalDate

sealed interface QuoteListAction : Action {
    data class ClickItem(
        val memberQuotesResponse: MemberQuotesResponse
    ) : QuoteListAction

    data class ClickMemo(
        val memberQuoteSeq: String,
        val savedMemo: String
    ) : QuoteListAction

    data object ClickDateSection: QuoteListAction

    data class UpdateStartDate(
        val date: LocalDate
    ): QuoteListAction

    data class UpdateEndDate(
        val date: LocalDate
    ): QuoteListAction

    data class UpdateLikeFilter(
        val liked: Boolean
    ): QuoteListAction
}