package com.arakene.presentation.util

import java.text.BreakIterator

internal const val HomeAnswerMaxGraphemes = 200

internal data class HomeAnswerInputState(
    val text: String,
    val remainingCount: Int,
)

/** Session-only state for the Home question; no domain persistence contract exists yet. */
data class HomeAnswerUiState(
    val draft: String = "",
    val recordedAnswer: String? = null,
    val isEditing: Boolean = true,
) {
    internal val input: HomeAnswerInputState
        get() = homeAnswerInputState(if (isEditing) draft else recordedAnswer.orEmpty())

    val isRecorded: Boolean
        get() = recordedAnswer != null && !isEditing
}

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

internal fun changeHomeAnswer(state: HomeAnswerUiState, text: String): HomeAnswerUiState =
    state.copy(draft = homeAnswerInputState(text).text)

internal fun recordHomeAnswer(state: HomeAnswerUiState): HomeAnswerUiState =
    state.copy(recordedAnswer = homeAnswerInputState(state.draft).text, isEditing = false)

internal fun editHomeAnswer(state: HomeAnswerUiState): HomeAnswerUiState =
    state.copy(draft = state.recordedAnswer.orEmpty(), isEditing = true)
