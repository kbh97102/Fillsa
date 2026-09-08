package com.arakene.presentation.util

import java.time.LocalDate
import java.util.regex.Pattern
import com.arakene.domain.responses.AnswerResponse
import com.arakene.domain.responses.MemberQuoteDay
import com.arakene.presentation.model.HomeAuthBoundRequestCoordinator
import com.arakene.presentation.model.HomeAuthBoundRequestToken
import com.arakene.presentation.model.HomeMemberFlowState
import com.arakene.presentation.model.HomeMemberMutationTarget
import com.arakene.presentation.model.HomeMemberMutationRevisions
import com.arakene.presentation.model.HomeMemberOrchestrationState
import com.arakene.presentation.model.HomeMemberQuoteWindow
import com.arakene.presentation.model.patchDay

internal const val HomeAnswerMaxGraphemes = 200
private val homeAnswerGraphemePattern = Pattern.compile("\\X")

internal data class HomeAnswerInputState(
    val text: String,
    val remainingCount: Int,
)

/** Selected-date answer projection; member persistence is coordinated below. */
data class HomeAnswerUiState(
    val draft: String = "",
    val recordedAnswer: String? = null,
    val isEditing: Boolean = true,
    val isSaving: Boolean = false,
    val dateKey: LocalDate? = null,
) {
    internal val input: HomeAnswerInputState
        get() = homeAnswerInputState(if (isEditing) draft else recordedAnswer.orEmpty())

    val isRecorded: Boolean
        get() = recordedAnswer != null && !isEditing
}

internal fun homeAnswerInputState(text: String): HomeAnswerInputState {
    val graphemes = homeAnswerGraphemePattern.matcher(text)
    var graphemeCount = 0
    var acceptedEnd = 0
    while (graphemeCount < HomeAnswerMaxGraphemes && graphemes.find()) {
        graphemeCount += 1
        acceptedEnd = graphemes.end()
    }

    return HomeAnswerInputState(
        text = text.substring(0, acceptedEnd),
        remainingCount = HomeAnswerMaxGraphemes - graphemeCount,
    )
}

internal fun changeHomeAnswer(state: HomeAnswerUiState, text: String): HomeAnswerUiState =
    if (state.isSaving) state else state.copy(draft = homeAnswerInputState(text).text)

internal fun recordHomeAnswer(state: HomeAnswerUiState): HomeAnswerUiState =
    state.copy(recordedAnswer = homeAnswerInputState(state.draft).text, isEditing = false)

internal fun editHomeAnswer(state: HomeAnswerUiState): HomeAnswerUiState =
    if (state.isSaving) state else state.copy(draft = state.recordedAnswer.orEmpty(), isEditing = true)

internal data class HomeMemberAnswerRequest(
    val id: Long,
    val auth: HomeAuthBoundRequestToken,
    val target: HomeMemberMutationTarget,
    val answer: HomeAnswerUiState,
)

internal data class HomeMemberAnswerEntry(
    val request: HomeMemberAnswerRequest,
    val answer: HomeAnswerUiState,
    val postSucceeded: Boolean = false,
)

internal data class HomeMemberAnswerRefresh(
    val request: HomeMemberAnswerRequest,
    val mutationRevisions: HomeMemberMutationRevisions,
)

internal data class HomeMemberAnswerStart(
    val coordinator: HomeMemberAnswerCoordinator,
    val answer: HomeAnswerUiState,
    val request: HomeMemberAnswerRequest? = null,
)

internal data class HomeMemberAnswerResult(
    val coordinator: HomeMemberAnswerCoordinator,
    val state: HomeMemberOrchestrationState,
    val snackbarMessage: String? = null,
    val shouldRefresh: Boolean = false,
)

/** Keeps POST and its optional refresh bound to the originating date, auth, and save revision. */
internal data class HomeMemberAnswerCoordinator(
    private val entries: Map<HomeMemberMutationTarget, HomeMemberAnswerEntry> = emptyMap(),
    private val nextId: Long = 0,
) {
    fun begin(
        state: HomeMemberFlowState,
        answer: HomeAnswerUiState,
        auth: HomeAuthBoundRequestToken,
    ): HomeMemberAnswerStart {
        val sequence = state.window.selectedDay?.dailyQuoteSeq
        if (!auth.context.isLoggedIn || sequence == null || answer.isSaving ||
            answer.draft.isBlank() || homeAnswerInputState(answer.draft).text != answer.draft
        ) return HomeMemberAnswerStart(this, answer)
        val target = HomeMemberMutationTarget(state.window.selectedDate, sequence)
        if (entries[target]?.answer?.isSaving == true) return HomeMemberAnswerStart(this, answer)
        val saving = answer.copy(isSaving = true, dateKey = target.date)
        val request = HomeMemberAnswerRequest(nextId, auth, target, saving)
        return HomeMemberAnswerStart(
            copy(entries = entries + (target to HomeMemberAnswerEntry(request, saving)), nextId = nextId + 1),
            saving,
            request,
        )
    }

    fun answerFor(window: HomeMemberQuoteWindow): HomeAnswerUiState {
        val mapped = HomeMemberFlowState.from(window).answer
        val target = window.selectedDay?.dailyQuoteSeq?.let { HomeMemberMutationTarget(window.selectedDate, it) }
        val entry = entries[target]
        return if (entry != null && !entry.postSucceeded) entry.answer else mapped
    }

    fun completePost(
        state: HomeMemberOrchestrationState,
        auth: HomeAuthBoundRequestCoordinator,
        request: HomeMemberAnswerRequest,
        response: AnswerResponse?,
    ): HomeMemberAnswerResult {
        val entry = entries[request.target]
        if (!auth.accepts(request.auth) || entry?.request != request || !entry.answer.isSaving) {
            return HomeMemberAnswerResult(this, state)
        }
        val updated = entry.copy(answer = entry.answer.copy(isSaving = false), postSucceeded = response != null)
        val coordinator = copy(entries = entries + (request.target to updated))
        if (response == null) return HomeMemberAnswerResult(coordinator, state)
        return HomeMemberAnswerResult(
            coordinator,
            state.patchDay(request.target) { it.copy(answer = response.answer, answeredAt = response.answeredAt) },
            HomeAnswerRecordedSnackbar,
            shouldRefresh = true,
        )
    }

    fun captureRefresh(
        state: HomeMemberOrchestrationState,
        request: HomeMemberAnswerRequest,
    ): HomeMemberAnswerRefresh = HomeMemberAnswerRefresh(
        request, state.mutationRevisions[request.target] ?: HomeMemberMutationRevisions(),
    )

    fun completeRefresh(
        state: HomeMemberOrchestrationState,
        auth: HomeAuthBoundRequestCoordinator,
        refresh: HomeMemberAnswerRefresh,
        response: MemberQuoteDay?,
    ): HomeMemberOrchestrationState {
        val request = refresh.request
        val entry = entries[request.target]
        if (!auth.accepts(request.auth) || entry?.request != request || !entry.postSucceeded ||
            response?.date != request.target.date.toString() || response.dailyQuoteSeq != request.target.dailyQuoteSeq
        ) return state
        val currentRevisions = state.mutationRevisions[request.target] ?: HomeMemberMutationRevisions()
        return state.patchDay(request.target) { currentDay ->
            response.copy(
                likeYn = if (currentRevisions.like > refresh.mutationRevisions.like) currentDay.likeYn else response.likeYn,
                imagePath = if (currentRevisions.image > refresh.mutationRevisions.image) currentDay.imagePath else response.imagePath,
            )
        }
    }
}
