package com.arakene.presentation.util

import com.arakene.domain.responses.DailyQuoteDto

internal enum class HomeQuoteLoadState {
    Loading,
    Loaded,
    Failed,
}

internal fun homeTypingDestination(
    quote: DailyQuoteDto,
    localeType: LocaleType,
    loadState: HomeQuoteLoadState,
): Screens.DailyQuote? {
    val localizedQuote = when (localeType) {
        LocaleType.KOR -> quote.korQuote
        LocaleType.ENG -> quote.engQuote
    }

    return if (
        loadState == HomeQuoteLoadState.Loaded &&
        quote.dailyQuoteSeq > 0 &&
        !localizedQuote.isNullOrBlank()
    ) {
        Screens.DailyQuote(quote)
    } else {
        null
    }
}
