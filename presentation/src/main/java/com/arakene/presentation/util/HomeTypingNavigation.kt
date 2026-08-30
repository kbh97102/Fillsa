package com.arakene.presentation.util

import com.arakene.domain.responses.DailyQuoteDto

internal enum class HomeQuoteLoadState {
    Loading,
    Loaded,
    Failed,
}

internal fun homeTypingDestination(
    quote: DailyQuoteDto,
    loadState: HomeQuoteLoadState,
): Screens.DailyQuote? {
    return if (
        loadState == HomeQuoteLoadState.Loaded &&
        quote.dailyQuoteSeq > 0 &&
        !quote.korQuote.isNullOrBlank()
    ) {
        Screens.DailyQuote(quote)
    } else {
        null
    }
}
