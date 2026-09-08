package com.arakene.presentation.model

import com.arakene.domain.responses.DailyQuoteDto
import com.arakene.domain.responses.MemberQuoteDay
import com.arakene.domain.responses.MemberWeeklyQuoteResponse
import java.time.LocalDate

data class HomeMemberQuoteWindow(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val days: List<MemberQuoteDay>,
    val selectedDate: LocalDate,
) {
    val selectedDay: MemberQuoteDay?
        get() = days.firstOrNull { LocalDate.parse(it.date) == selectedDate }

    val visibleDates: List<LocalDate>
        get() = days.map { LocalDate.parse(it.date) }

    fun select(date: LocalDate): HomeMemberQuoteWindow? =
        takeIf { date in visibleDates }?.copy(selectedDate = date)

    fun requestEndDate(direction: WindowDirection): String =
        when (direction) {
            WindowDirection.Previous -> endDate.minusDays(WINDOW_SIZE_DAYS).toString()
            WindowDirection.Next -> endDate.plusDays(WINDOW_SIZE_DAYS).toString()
        }

    companion object {
        private const val WINDOW_SIZE_DAYS = 7L

        fun from(response: MemberWeeklyQuoteResponse): HomeMemberQuoteWindow {
            val endDate = LocalDate.parse(response.endDate)
            return HomeMemberQuoteWindow(
                startDate = LocalDate.parse(response.startDate),
                endDate = endDate,
                days = response.days.toList(),
                selectedDate = response.days
                    .firstOrNull { it.state == "today" }
                    ?.let { LocalDate.parse(it.date) }
                    ?: endDate,
            )
        }
    }
}

enum class WindowDirection {
    Previous,
    Next,
}

fun MemberQuoteDay.toDailyQuoteDto(): DailyQuoteDto =
    DailyQuoteDto(
        likeYn = likeYn,
        imagePath = imagePath,
        dailyQuoteSeq = dailyQuoteSeq ?: 0,
        korQuote = korQuote,
        engQuote = engQuote,
        korAuthor = korAuthor,
        engAuthor = engAuthor,
        authorUrl = authorUrl,
    ).apply {
        quoteDate = date
    }

fun MemberQuoteDay.withAnswer(answer: String, answeredAt: String): MemberQuoteDay =
    copy(answer = answer, answeredAt = answeredAt)

fun Map<LocalDate, HomeMemberQuoteWindow>.withCachedWindow(
    window: HomeMemberQuoteWindow,
): Map<LocalDate, HomeMemberQuoteWindow> = this + (window.endDate to window)

fun shouldAcceptHomeMemberQuoteResponse(
    requestId: Long,
    latestRequestId: Long,
): Boolean = requestId >= latestRequestId
