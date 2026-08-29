package com.arakene.presentation.util

import com.arakene.domain.responses.DailyQuoteDto
import com.arakene.domain.model.PromptAnswerRecord
import java.text.BreakIterator

internal const val HomeAnswerMaxGraphemes = 200
internal const val HomePromptQuestion = "누군가의 호의를 한참 뒤에야 받아들인 적 있나요?"

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

internal fun homeAnswerRoute(
    dailyQuote: DailyQuoteDto,
    answer: String,
    promptDate: String = "",
    promptQuestion: String = "",
): Screens.DailyQuote = Screens.DailyQuote(
    dailyQuoteDto = dailyQuote,
    initialAnswer = homeAnswerInputState(answer).text,
    promptDate = promptDate,
    promptQuestion = promptQuestion,
)

internal fun homePromptAnswerRecord(
    date: String,
    question: String,
    answer: String,
): PromptAnswerRecord = PromptAnswerRecord(
    date = date,
    question = question,
    answer = homeAnswerInputState(answer).text,
)
