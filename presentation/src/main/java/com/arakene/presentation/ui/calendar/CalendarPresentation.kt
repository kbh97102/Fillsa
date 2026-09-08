package com.arakene.presentation.ui.calendar

import androidx.compose.runtime.staticCompositionLocalOf
import com.arakene.domain.responses.MemberMonthlyQuoteResponse
import com.arakene.domain.responses.MemberQuotesData
import com.arakene.domain.responses.MonthlySummaryData
import com.arakene.domain.util.YN
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import java.time.LocalDate

/** Selected monthly detail; explicit fixture overrides remain presentation-only. */
internal data class CalendarSelectedDayPresentation(
    val completed: Boolean,
    val answer: String = "",
    val registeredImageUri: String? = null,
    val quoteDateOverride: LocalDate? = null,
    val displayedCountOverride: Int? = null,
    val question: String = "",
    val answeredAt: String? = null,
    val engQuote: String? = null,
    val engAuthor: String? = null,
    val authorUrl: String? = null,
) {
    val hasRecordedAnswer: Boolean get() = answer.isNotBlank()
    val hasRegisteredImage: Boolean get() = !registeredImageUri.isNullOrBlank()

    companion object {
        val Empty = CalendarSelectedDayPresentation(completed = false)
        val Completed = CalendarSelectedDayPresentation(completed = true)
    }
}

internal fun calendarSelectedDayPresentation(
    quoteData: MemberQuotesData?,
    language: String = "ko",
    isMember: Boolean = true,
): CalendarSelectedDayPresentation {
    val completed = quoteData?.let { it.completed || it.todayCompleted } == true
    return CalendarSelectedDayPresentation(
        completed = completed,
        question = when {
            !isMember -> "누군가의 호의를 한참 뒤에야 받아들인 적 있나요?"
            language == "en" -> quoteData?.questionEn.orEmpty()
            else -> quoteData?.questionKo.orEmpty()
        },
        answer = quoteData?.answer.orEmpty(),
        answeredAt = quoteData?.answeredAt,
        registeredImageUri = quoteData?.imagePath,
        engQuote = quoteData?.engQuote,
        engAuthor = quoteData?.engAuthor,
        authorUrl = quoteData?.authorUrl,
    )
}

internal enum class CalendarRuntimeQaState(val launchValue: String) {
    Basic("basic"),
    CompletedUnanswered("completed_unanswered"),
    CompletedAnsweredImage("completed_answered_image");

    companion object {
        fun fromLaunchValue(value: String?): CalendarRuntimeQaState? = entries.firstOrNull {
            it.launchValue == value
        }
    }
}

/** Debug-only, process-local visual input. It has no repository or persistence dependency. */
internal data class CalendarRuntimeQaFixture(val state: CalendarRuntimeQaState) {
    val selectedDay: CalendarDay = CalendarDay(
        date = when (state) {
            CalendarRuntimeQaState.Basic -> LocalDate.of(2025, 3, 17)
            CalendarRuntimeQaState.CompletedUnanswered,
            CalendarRuntimeQaState.CompletedAnsweredImage -> LocalDate.of(2025, 3, 21)
        },
        position = DayPosition.MonthDate,
    )

    val data: MemberMonthlyQuoteResponse = MemberMonthlyQuoteResponse(
        memberQuotes = calendarQaQuotes(state),
        monthlySummary = MonthlySummaryData(
            typingCount = if (state == CalendarRuntimeQaState.Basic) 2 else 4,
            likeCount = if (state == CalendarRuntimeQaState.Basic) 2 else 4,
            streakCount = 100,
        ),
    )

    fun selectedDayPresentation(registeredImageUri: String): CalendarSelectedDayPresentation = when (state) {
        CalendarRuntimeQaState.Basic -> CalendarSelectedDayPresentation.Empty.copy(
            // The binding Figma basic frame selects March 17 but previews the March 22 quote.
            quoteDateOverride = LocalDate.of(2025, 3, 22),
        )
        CalendarRuntimeQaState.CompletedUnanswered -> CalendarSelectedDayPresentation.Completed.copy(
            question = "누군가의 호의를 한참 뒤에야 받아들인 적 있나요?",
        )
        CalendarRuntimeQaState.CompletedAnsweredImage -> CalendarSelectedDayPresentation.Completed.copy(
            question = "누군가의 호의를 한참 뒤에야 받아들인 적 있나요?",
            answer = "친구가 힘들 때 언제든 연락하라고 했는데, 한참 뒤에야 그 말이\n진심이었다는 걸 믿고 먼저 연락한 적이 있어요.",
            registeredImageUri = registeredImageUri,
            // The reference shows 0 / 200 with a non-empty sample answer. Limit/count logic
            // remains production-correct; this override is confined to the exact QA fixture.
            displayedCountOverride = 0,
        )
    }
}

internal val LocalCalendarRuntimeQaFixture = staticCompositionLocalOf<CalendarRuntimeQaFixture?> { null }

private fun calendarQaQuotes(state: CalendarRuntimeQaState): List<MemberQuotesData> {
    val previewQuote = "인간은 자연에서 가장 연약한 한 줄기 갈대일 뿐이지만, 생각하는 갈대이다."
    val completedQuote = "영광은 먼지와 땀과 피로 얼굴이 얼룩진 채 경기장에 서 있는 사람의 것이다."
    return when (state) {
        CalendarRuntimeQaState.Basic -> listOf(
            qaQuote(17, previewQuote, completed = false, liked = false),
            qaQuote(19, completedQuote, completed = true, liked = false),
            qaQuote(20, completedQuote, completed = true, liked = true),
            qaQuote(21, completedQuote, completed = true, liked = true),
            qaQuote(22, previewQuote, completed = true, liked = true),
        )
        CalendarRuntimeQaState.CompletedUnanswered -> listOf(
            qaQuote(18, completedQuote, completed = true, liked = true),
            qaQuote(19, completedQuote, completed = true, liked = true),
            qaQuote(20, completedQuote, completed = true, liked = true),
            qaQuote(21, completedQuote, completed = true, liked = false),
        )
        CalendarRuntimeQaState.CompletedAnsweredImage -> listOf(
            qaQuote(18, completedQuote, completed = true, liked = true),
            qaQuote(19, completedQuote, completed = true, liked = true),
            qaQuote(20, completedQuote, completed = true, liked = true),
            qaQuote(21, completedQuote, completed = true, liked = true),
        )
    }
}

private fun qaQuote(
    day: Int,
    quote: String,
    completed: Boolean,
    liked: Boolean,
) = MemberQuotesData(
    dailyQuoteSeq = day,
    quoteDate = "2025-03-${day.toString().padStart(2, '0')}",
    quote = quote,
    author = "Fillsa QA",
    completed = completed,
    likeYnString = if (liked) YN.Y.type else YN.N.type,
    todayCompleted = false,
)
