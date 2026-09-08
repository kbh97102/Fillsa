package com.arakene.presentation.ui.home

import com.arakene.domain.responses.AnswerResponse
import com.arakene.domain.responses.MemberQuoteDay
import com.arakene.domain.responses.MemberWeeklyQuoteResponse
import com.arakene.presentation.model.*
import com.arakene.presentation.util.*
import java.time.LocalDate
import org.junit.Assert.*
import org.junit.Test

class HomeMemberAnswerSaveTest {
    private fun memberDay(date: String, state: String, seq: Int, answer: String? = null) = MemberQuoteDay(
        date, "금", state, seq, "오늘의 한국어 명언", "Today's English quote", "한국어 작가",
        "English author", "https://example.com/author", "오늘 무엇을 기록하고 싶나요?",
        "What would you like to write today?", answer,
        if (answer == null) null else "2026-08-28 09:00:00", "N", null, state == "done",
    )
    private val weeklyFixture = MemberWeeklyQuoteResponse("2026-08-28", "2026-09-03", listOf(
        memberDay("2026-08-28", "done", 81, "기록 1"),
        memberDay("2026-08-29", "past", 82), memberDay("2026-08-30", "past", 83),
        memberDay("2026-08-31", "past", 84), memberDay("2026-09-01", "past", 85),
        memberDay("2026-09-02", "past", 86), memberDay("2026-09-03", "today", 87),
    ))
    private val memberState = HomeMemberFlowState.from(HomeMemberQuoteWindow.from(weeklyFixture))
    private val orchestration = storeHomeMemberWindow(HomeMemberOrchestrationState(), memberState.window, true)
    private val auth = HomeAuthBoundRequestCoordinator().transition(HomeAuthContext(true, "account-a"))
    private val answerResponse = AnswerResponse(91, "기록할 답변", "2026-09-03 14:01:13")
    private fun begin(draft: String = "기록할 답변") = HomeMemberAnswerCoordinator().begin(
        memberState, HomeAnswerUiState(draft = draft), auth.capture()!!,
    )

    @Test fun `member answer is recorded only after POST success using server normalized text`() {
        val start = begin("  기록할 답변  ")
        assertTrue(start.answer.isSaving)
        assertNull(start.answer.recordedAnswer)
        assertEquals(87, start.request!!.target.dailyQuoteSeq)
        assertEquals("  기록할 답변  ", start.request!!.answer.draft)
        val saved = start.coordinator.completePost(orchestration, auth, start.request!!, answerResponse)
        val answer = saved.coordinator.answerFor(saved.state.currentWindow!!)
        assertFalse(answer.isSaving)
        assertEquals("기록할 답변", answer.recordedAnswer)
        assertEquals(HomeAnswerRecordedSnackbar, saved.snackbarMessage)
        assertTrue(saved.shouldRefresh)
        assertEquals("2026-09-03 14:01:13", saved.state.currentWindow!!.selectedDay!!.answeredAt)
        assertFalse(saved.state.currentWindow!!.selectedDay!!.completed)
        assertEquals("today", saved.state.currentWindow!!.selectedDay!!.state)
        assertEquals("기록할 답변", saved.state.cachedWindows.values.single().selectedDay!!.answer)
    }

    @Test fun `blank input and absent sequence never start member mutation`() {
        assertNull(begin(" \n\t ").request)
        val absent = memberState.window.copy(days = memberState.window.days.map { it.copy(dailyQuoteSeq = null) })
        val start = HomeMemberAnswerCoordinator().begin(HomeMemberFlowState.from(absent),
            HomeAnswerUiState(draft = "답변"), auth.capture()!!)
        assertNull(start.request)
        assertFalse(start.answer.isSaving)
    }

    @Test fun `200 graphemes are accepted but unbounded 201 graphemes are rejected`() {
        assertNotNull(begin("a\u0301".repeat(200)).request)
        assertNull(begin("a\u0301".repeat(201)).request)
    }

    @Test fun `duplicate tap and return to in flight date keep one pending save`() {
        val start = begin()
        val duplicate = start.coordinator.begin(memberState, start.answer, auth.capture()!!)
        assertNull(duplicate.request)
        assertTrue(duplicate.coordinator.answerFor(memberState.window).isSaving)
        assertEquals("기록할 답변", duplicate.coordinator.answerFor(memberState.window).draft)
    }

    @Test fun `POST failure keeps draft editable without cache change snackbar or refresh`() {
        val start = begin()
        val failed = start.coordinator.completePost(orchestration, auth, start.request!!, null)
        assertEquals(orchestration, failed.state)
        assertNull(failed.snackbarMessage)
        assertFalse(failed.shouldRefresh)
        val answer = failed.coordinator.answerFor(memberState.window)
        assertEquals("기록할 답변", answer.draft)
        assertFalse(answer.isSaving)
        assertTrue(answer.isEditing)
        assertNull(answer.recordedAnswer)
    }

    @Test fun `failed daily refresh retains successful answer`() {
        val start = begin()
        val saved = start.coordinator.completePost(orchestration, auth, start.request!!, answerResponse)
        val refreshed = saved.coordinator.completeRefresh(saved.state, auth, start.request!!, null)
        assertEquals(saved.state, refreshed)
        assertEquals("기록할 답변", refreshed.currentWindow!!.selectedDay!!.answer)
        assertFalse(refreshed.currentWindow!!.selectedDay!!.completed)
    }

    @Test fun `save A then select B patches only A in current window and cache`() {
        val start = begin()
        val selected = acceptHomeMemberSelection(orchestration, memberState.window.select(LocalDate.parse("2026-09-02"))!!)
        val saved = start.coordinator.completePost(selected, auth, start.request!!, answerResponse)
        assertNull(saved.state.currentWindow!!.selectedDay!!.answer)
        assertNull(saved.coordinator.answerFor(saved.state.currentWindow!!).recordedAnswer)
        assertEquals(LocalDate.parse("2026-09-02"), saved.state.currentWindow!!.selectedDate)
        assertEquals("기록할 답변", saved.state.cachedWindows.values.single().days.last().answer)
        val daily = weeklyFixture.days.last().copy(answer = "서버 답변", answeredAt = "2026-09-03 14:01:15")
        val refreshed = saved.coordinator.completeRefresh(saved.state, auth, start.request!!, daily)
        assertNull(refreshed.currentWindow!!.selectedDay!!.answer)
        assertEquals("서버 답변", refreshed.currentWindow!!.days.last().answer)
    }

    @Test fun `account switch rejects late POST and daily response even for identical date sequence`() {
        val start = begin()
        val nextAuth = auth.transition(HomeAuthContext(true, "account-b"))
        val ignored = start.coordinator.completePost(orchestration, nextAuth, start.request!!, answerResponse)
        assertEquals(orchestration, ignored.state)
        assertNull(ignored.snackbarMessage)
        assertFalse(ignored.shouldRefresh)
        val saved = start.coordinator.completePost(orchestration, auth, start.request!!, answerResponse)
        assertEquals(orchestration, saved.coordinator.completeRefresh(orchestration, nextAuth,
            start.request!!, weeklyFixture.days.last().copy(answer = "old account")))
    }

    @Test fun `daily refresh rejects wrong date sequence and obsolete save revision`() {
        val start = begin()
        val saved = start.coordinator.completePost(orchestration, auth, start.request!!, answerResponse)
        assertEquals(saved.state, saved.coordinator.completeRefresh(saved.state, auth, start.request!!,
            weeklyFixture.days[5].copy(answer = "wrong date")))
        assertEquals(saved.state, saved.coordinator.completeRefresh(saved.state, auth, start.request!!,
            weeklyFixture.days.last().copy(dailyQuoteSeq = 999, answer = "wrong sequence")))
        val retry = saved.coordinator.begin(HomeMemberFlowState.from(saved.state.currentWindow!!),
            HomeAnswerUiState(draft = "new answer"), auth.capture()!!)
        assertEquals(saved.state, retry.coordinator.completeRefresh(saved.state, auth, start.request!!,
            weeklyFixture.days.last().copy(answer = "stale answer")))
    }

    @Test fun `guest answer stays session only and guest auth cannot start a member save`() {
        val guest = HomeAuthBoundRequestCoordinator().transition(HomeAuthContext(false, null))
        val start = HomeMemberAnswerCoordinator().begin(memberState, HomeAnswerUiState(draft = "guest"), guest.capture()!!)
        assertNull(start.request)
        val recorded = recordHomeAnswerForHome(HomeAnswerUiState(draft = "guest"))
        assertEquals("guest", recorded.state.recordedAnswer)
        assertEquals(HomeAnswerRecordedSnackbar, recorded.snackbarMessage)
        assertFalse(recorded.state.isSaving)
        assertNull(HomeAnswerUiState().recordedAnswer)
    }
}
