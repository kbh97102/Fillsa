package com.arakene.presentation.model

import com.arakene.domain.responses.DailyQuoteDto
import com.arakene.domain.responses.MemberQuoteDay
import com.arakene.domain.responses.MemberWeeklyQuoteResponse
import com.arakene.presentation.util.HomeAnswerUiState
import java.time.LocalDate
import java.time.temporal.ChronoUnit

sealed interface HomeLoadCommand {
    data class MemberWeekly(val endDate: String?) : HomeLoadCommand
    data class GuestDaily(val date: LocalDate?) : HomeLoadCommand
}

data class HomeMemberSelectionResult(
    val window: HomeMemberQuoteWindow,
    val requestedDate: LocalDate,
    val loadCommand: HomeLoadCommand.MemberWeekly?,
)

data class HomeMemberFlowState(
    val window: HomeMemberQuoteWindow,
    val questionKo: String?,
    val questionEn: String?,
    val answer: HomeAnswerUiState,
) {
    companion object {
        fun from(window: HomeMemberQuoteWindow): HomeMemberFlowState {
            val selectedDay = window.selectedDay
            return HomeMemberFlowState(
                window = window,
                questionKo = selectedDay?.questionKo,
                questionEn = selectedDay?.questionEn,
                answer = HomeAnswerUiState(
                    draft = selectedDay?.answer.orEmpty(),
                    recordedAnswer = selectedDay?.answer,
                    isEditing = selectedDay?.answer == null,
                ),
            )
        }
    }
}

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

fun homeInitialLoadCommand(
    isLoggedIn: Boolean,
    requestedDate: LocalDate?,
): HomeLoadCommand =
    if (isLoggedIn) {
        HomeLoadCommand.MemberWeekly(endDate = null)
    } else {
        HomeLoadCommand.GuestDaily(date = requestedDate)
    }

fun selectMemberDate(
    window: HomeMemberQuoteWindow,
    targetDate: LocalDate,
    cachedWindows: Map<LocalDate, HomeMemberQuoteWindow> = emptyMap(),
): HomeMemberSelectionResult {
    val selectedWindow = window.select(targetDate)
        ?: cachedWindows.values.firstNotNullOfOrNull { cached -> cached.select(targetDate) }
    if (selectedWindow != null) {
        return HomeMemberSelectionResult(
            window = selectedWindow,
            requestedDate = targetDate,
            loadCommand = null,
        )
    }

    val direction = if (targetDate < window.startDate) {
        WindowDirection.Previous
    } else {
        WindowDirection.Next
    }
    return HomeMemberSelectionResult(
        window = window,
        requestedDate = targetDate,
        loadCommand = HomeLoadCommand.MemberWeekly(window.requestEndDate(direction)),
    )
}

fun resolveMemberAnchorTarget(
    anchorWindow: HomeMemberQuoteWindow,
    requestedDate: LocalDate,
): HomeMemberSelectionResult {
    val clampedTarget = minOf(requestedDate, anchorWindow.endDate)
    anchorWindow.select(clampedTarget)?.let { selected ->
        return HomeMemberSelectionResult(
            window = selected,
            requestedDate = clampedTarget,
            loadCommand = null,
        )
    }

    val daysBack = ChronoUnit.DAYS.between(clampedTarget, anchorWindow.endDate)
    val windowOffset = daysBack / WINDOW_SIZE_DAYS
    val targetEndDate = anchorWindow.endDate.minusDays(windowOffset * WINDOW_SIZE_DAYS)
    return HomeMemberSelectionResult(
        window = anchorWindow,
        requestedDate = clampedTarget,
        loadCommand = HomeLoadCommand.MemberWeekly(targetEndDate.toString()),
    )
}

fun HomeMemberQuoteWindow.selectedMutationSequence(): Int? = selectedDay?.dailyQuoteSeq

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

private const val WINDOW_SIZE_DAYS = 7L
