package com.arakene.presentation.model

import com.arakene.domain.responses.AnswerResponse
import com.arakene.domain.responses.MemberMonthlyQuoteResponse
import com.arakene.domain.responses.MemberQuoteDay
import com.arakene.presentation.util.HomeAnswerUiState
import com.arakene.presentation.util.homeAnswerInputState
import java.time.LocalDate

internal data class CalendarMonthState(
    val data: MemberMonthlyQuoteResponse? = null,
    val selectedDate: LocalDate = LocalDate.now(),
    val answer: HomeAnswerUiState = HomeAnswerUiState(),
    val monthlyRevision: Long = 0,
) {
    val selectedQuote get() = data?.memberQuotes?.firstOrNull { it.quoteDate == selectedDate.toString() }
    companion object {
        fun from(data: MemberMonthlyQuoteResponse, selectedDate: LocalDate): CalendarMonthState =
            CalendarMonthState(data, selectedDate).projectAnswer()
    }
}

internal data class CalendarDaySelection(val state: CalendarMonthState, val networkCommand: String? = null)
internal fun selectCalendarDay(state: CalendarMonthState, date: LocalDate) =
    CalendarDaySelection(state.copy(selectedDate = date).projectAnswer())

private fun CalendarMonthState.projectAnswer(): CalendarMonthState = copy(answer = HomeAnswerUiState(
    draft = selectedQuote?.answer.orEmpty(), recordedAnswer = selectedQuote?.answer,
    isEditing = selectedQuote?.answer.isNullOrBlank(), dateKey = selectedDate,
))
internal data class CalendarAnswerRequest(val id: Long, val auth: HomeAuthBoundRequestToken, val target: HomeMemberMutationTarget)
internal data class CalendarAnswerStart(val coordinator: CalendarAnswerCoordinator, val answer: HomeAnswerUiState, val request: CalendarAnswerRequest? = null)
internal data class CalendarAnswerResult(val coordinator: CalendarAnswerCoordinator, val state: CalendarMonthState, val shouldRefresh: Boolean = false)
internal data class CalendarAnswerRefresh(val request: CalendarAnswerRequest, val monthlyRevision: Long)
internal data class CalendarAnswerEntry(
    val request: CalendarAnswerRequest,
    val answer: HomeAnswerUiState,
    val saved: AnswerResponse? = null,
    val savedRevision: Long = 0,
)
internal data class CalendarAnswerCoordinator(
    val revision: Long = 0,
    private val entries: Map<HomeMemberMutationTarget, CalendarAnswerEntry> = emptyMap(),
) {
    fun acceptMonthly(state: CalendarMonthState, response: MemberMonthlyQuoteResponse, requestRevision: Long): CalendarMonthState {
        var updated = CalendarMonthState.from(response, state.selectedDate).copy(monthlyRevision = state.monthlyRevision + 1)
        entries.values.filter { it.savedRevision > requestRevision }.forEach { entry ->
            entry.saved?.let { updated = updated.patchAnswer(entry.request.target, it.answer, it.answeredAt) }
        }
        return updated.copy(answer = if (state.answer.isEditing && state.answer.dateKey == state.selectedDate &&
            state.answer.draft != state.selectedQuote?.answer.orEmpty())
            state.answer else answerFor(updated))
    }

    fun answerFor(state: CalendarMonthState): HomeAnswerUiState {
        val target = state.selectedQuote?.let { HomeMemberMutationTarget(state.selectedDate, it.dailyQuoteSeq) }
        val entry = entries[target]
        return if (entry != null && entry.saved == null) entry.answer else state.projectAnswer().answer
    }

    fun begin(state: CalendarMonthState, answer: HomeAnswerUiState, auth: HomeAuthBoundRequestToken): CalendarAnswerStart {
        val sequence = state.selectedQuote?.dailyQuoteSeq
        if (!auth.context.isLoggedIn || sequence == null || answer.isSaving || answer.draft.isBlank() ||
            homeAnswerInputState(answer.draft).text != answer.draft) return CalendarAnswerStart(this, answer)
        val target = HomeMemberMutationTarget(state.selectedDate, sequence)
        if (entries[target]?.answer?.isSaving == true) return CalendarAnswerStart(this, answer)
        val request = CalendarAnswerRequest(revision + 1, auth, target)
        val saving = answer.copy(isSaving = true, dateKey = state.selectedDate)
        return CalendarAnswerStart(copy(revision = revision + 1,
            entries = entries + (target to CalendarAnswerEntry(request, saving))), saving, request)
    }

    fun completePost(state: CalendarMonthState, auth: HomeAuthBoundRequestCoordinator, request: CalendarAnswerRequest, response: AnswerResponse?): CalendarAnswerResult {
        val entry = entries[request.target]
        if (!auth.accepts(request.auth) || entry?.request != request || !entry.answer.isSaving) return CalendarAnswerResult(this, state)
        val coordinator = copy(revision = revision + 1, entries = entries + (request.target to entry.copy(
            answer = entry.answer.copy(isSaving = false), saved = response, savedRevision = revision + 1)))
        val updated = if (response == null) state else state.patchAnswer(request.target, response.answer, response.answeredAt)
        val selectedTarget = state.selectedDate == request.target.date && state.selectedQuote?.dailyQuoteSeq == request.target.dailyQuoteSeq
        return CalendarAnswerResult(coordinator, updated.copy(answer = if (selectedTarget)
            coordinator.answerFor(updated) else state.answer), response != null)
    }
    fun captureRefresh(state: CalendarMonthState, request: CalendarAnswerRequest) = CalendarAnswerRefresh(request, state.monthlyRevision)
    fun completeRefresh(state: CalendarMonthState, auth: HomeAuthBoundRequestCoordinator, refresh: CalendarAnswerRefresh, response: MemberQuoteDay?): CalendarMonthState {
        val request = refresh.request
        val entry = entries[request.target]
        if (!auth.accepts(request.auth) || entry?.request != request || entry.saved == null ||
            state.monthlyRevision != refresh.monthlyRevision || response?.date != request.target.date.toString() ||
            response.dailyQuoteSeq != request.target.dailyQuoteSeq || response.answer == null) return state
        // Daily reconciliation owns answer fields only. Calendar retains its monthly
        // completion, summary, image and like state, including any concurrent refresh.
        val updated = state.patchAnswer(request.target, response.answer ?: return state, response.answeredAt)
        return updated.copy(answer = if (state.answer.isEditing) state.answer else answerFor(updated))
    }
}

private fun CalendarMonthState.patchAnswer(target: HomeMemberMutationTarget, answer: String, answeredAt: String?): CalendarMonthState =
    copy(data = data?.copy(memberQuotes = data.memberQuotes.map { quote ->
        if (quote.quoteDate == target.date.toString() && quote.dailyQuoteSeq == target.dailyQuoteSeq)
            quote.copy(answer = answer, answeredAt = answeredAt) else quote
    }))
