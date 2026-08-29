package com.arakene.presentation.util

import java.text.BreakIterator

internal const val HomeAnswerMaxGraphemes = 200

internal data class HomeAnswerInputState(
    val text: String,
    val remainingCount: Int,
)

internal fun homeAnswerInputState(text: String): HomeAnswerInputState {
    val graphemeIterator = BreakIterator.getCharacterInstance()
    graphemeIterator.setText(text)

    val acceptedText = StringBuilder()
    var graphemeCount = 0
    var start = graphemeIterator.first()
    var end = graphemeIterator.next()
    while (end != BreakIterator.DONE && graphemeCount < HomeAnswerMaxGraphemes) {
        acceptedText.append(text, start, end)
        graphemeCount += 1
        start = end
        end = graphemeIterator.next()
    }

    return HomeAnswerInputState(
        text = acceptedText.toString(),
        remainingCount = HomeAnswerMaxGraphemes - graphemeCount,
    )
}
