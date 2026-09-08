package com.arakene.presentation.model

import com.arakene.domain.responses.MemberQuoteDay
import com.arakene.domain.responses.MemberWeeklyQuoteResponse
import com.arakene.presentation.util.HomeQuoteLoadState
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeMemberFlowReducerTest {

    private fun memberDay(
        date: String,
        state: String,
        dailyQuoteSeq: Int?,
        answer: String?,
        answeredAt: String?,
        completed: Boolean,
    ) = MemberQuoteDay(
        date = date,
        dayOfWeek = "금",
        state = state,
        dailyQuoteSeq = dailyQuoteSeq,
        korQuote = "오늘의 한국어 명언",
        engQuote = "Today's English quote",
        korAuthor = "한국어 작가",
        engAuthor = "English author",
        authorUrl = "https://example.com/author",
        questionKo = "오늘 무엇을 기록하고 싶나요?",
        questionEn = "What would you like to write today?",
        answer = answer,
        answeredAt = answeredAt,
        likeYn = "N",
        imagePath = null,
        completed = completed,
    )

    private val weeklyFixture = MemberWeeklyQuoteResponse(
        startDate = "2026-08-28",
        endDate = "2026-09-03",
        days = listOf(
            memberDay("2026-08-28", "done", 81, "기록 1", "2026-08-28 09:00:00", true),
            memberDay("2026-08-29", "past", 82, null, null, false),
            memberDay("2026-08-30", "past", 83, null, null, false),
            memberDay("2026-08-31", "past", 84, null, null, false),
            memberDay("2026-09-01", "past", 85, null, null, false),
            memberDay("2026-09-02", "past", 86, null, null, false),
            memberDay("2026-09-03", "today", 87, null, null, false),
        ),
    )

    private val window = HomeMemberQuoteWindow.from(weeklyFixture)

    @Test
    fun `first authenticated load requests weekly without endDate`() {
        val command = homeInitialLoadCommand(isLoggedIn = true, requestedDate = null)

        assertEquals(HomeLoadCommand.MemberWeekly(endDate = null), command)
    }

    @Test
    fun `selecting a cached date produces no network command`() {
        val result = selectMemberDate(window, LocalDate.of(2026, 9, 1))

        assertNull(result.loadCommand)
        assertEquals(LocalDate.of(2026, 9, 1), result.window.selectedDate)
    }

    @Test
    fun `selecting a date from a cached adjacent window produces no network command`() {
        val previousResponse = weeklyFixture.copy(
            startDate = "2026-08-21",
            endDate = "2026-08-27",
            days = listOf(
                memberDay("2026-08-21", "past", 74, null, null, false),
                memberDay("2026-08-22", "past", 75, null, null, false),
                memberDay("2026-08-23", "past", 76, null, null, false),
                memberDay("2026-08-24", "past", 77, null, null, false),
                memberDay("2026-08-25", "past", 78, null, null, false),
                memberDay("2026-08-26", "past", 79, null, null, false),
                memberDay("2026-08-27", "past", 80, null, null, false),
            ),
        )
        val previousWindow = HomeMemberQuoteWindow.from(previousResponse)

        val result = selectMemberDate(
            window = window,
            targetDate = LocalDate.of(2026, 8, 27),
            cachedWindows = mapOf(LocalDate.of(2026, 8, 27) to previousWindow),
        )

        assertNull(result.loadCommand)
        assertEquals(LocalDate.of(2026, 8, 27), result.window.selectedDate)
    }

    @Test
    fun `crossing the previous boundary requests exactly seven days before response endDate`() {
        val selectedAtStart = window.copy(selectedDate = LocalDate.of(2026, 8, 28))

        val result = selectMemberDate(selectedAtStart, LocalDate.of(2026, 8, 27))

        assertEquals(HomeLoadCommand.MemberWeekly("2026-08-27"), result.loadCommand)
        assertEquals(LocalDate.of(2026, 8, 27), result.requestedDate)
    }

    @Test
    fun `crossing the next boundary requests exactly seven days after response endDate`() {
        val result = selectMemberDate(window, LocalDate.of(2026, 9, 4))

        assertEquals(HomeLoadCommand.MemberWeekly("2026-09-10"), result.loadCommand)
        assertEquals(LocalDate.of(2026, 9, 4), result.requestedDate)
    }

    @Test
    fun `calendar target aligns to a server anchored seven day window`() {
        val result = resolveMemberAnchorTarget(window, LocalDate.of(2026, 8, 20))

        assertEquals(HomeLoadCommand.MemberWeekly("2026-08-20"), result.loadCommand)
        assertEquals(LocalDate.of(2026, 8, 20), result.requestedDate)
    }

    @Test
    fun `future calendar target clamps to server response endDate without another request`() {
        val result = resolveMemberAnchorTarget(window, LocalDate.of(2026, 9, 10))

        assertNull(result.loadCommand)
        assertEquals(LocalDate.of(2026, 9, 3), result.window.selectedDate)
        assertEquals(LocalDate.of(2026, 9, 3), result.requestedDate)
    }

    @Test
    fun `selected day without a server sequence cannot mutate`() {
        val noSequenceWindow = HomeMemberQuoteWindow.from(
            weeklyFixture.copy(
                days = weeklyFixture.days.map { day ->
                    if (day.date == "2026-09-03") day.copy(dailyQuoteSeq = null) else day
                },
            ),
        )

        assertNull(noSequenceWindow.selectedMutationSequence())
    }

    @Test
    fun `member flow state derives question and recorded answer from selected server day`() {
        val selectedWindow = window.select(LocalDate.of(2026, 8, 28))!!

        val state = HomeMemberFlowState.from(selectedWindow)

        assertEquals("오늘 무엇을 기록하고 싶나요?", state.questionKo)
        assertEquals("What would you like to write today?", state.questionEn)
        assertEquals("기록 1", state.answer.recordedAnswer)
        assertEquals("기록 1", state.answer.draft)
        assertFalse(state.answer.isEditing)
    }

    @Test
    fun `late weekly response cannot replace state from the latest request`() {
        assertTrue(shouldAcceptHomeMemberQuoteResponse(requestId = 12, latestRequestId = 12))
        assertFalse(shouldAcceptHomeMemberQuoteResponse(requestId = 11, latestRequestId = 12))
    }

    @Test
    fun `cached selection supersedes an uncached request before its response arrives`() {
        val cachedWindow = window.select(LocalDate.of(2026, 9, 1))!!
        val initialState = HomeMemberOrchestrationState(
            currentWindow = window,
            cachedWindows = mapOf(window.endDate to window),
            anchorEndDate = window.endDate,
            latestRequestId = 20,
        )
        val requesting = beginHomeMemberRequest(initialState)
        val staleRequestId = requesting.latestRequestId
        val selected = acceptHomeMemberSelection(requesting, cachedWindow)

        assertEquals(22, selected.latestRequestId)
        assertEquals(LocalDate.of(2026, 9, 1), selected.currentWindow!!.selectedDate)
        assertFalse(shouldAcceptHomeMemberQuoteResponse(staleRequestId, selected.latestRequestId))
    }

    @Test
    fun `initial response keeps server today when there is no explicit target`() {
        val result = resolveInitialMemberWindow(window, requestedDate = null)

        assertNull(result.loadCommand)
        assertEquals(LocalDate.of(2026, 9, 3), result.window.selectedDate)
        assertEquals(LocalDate.of(2026, 9, 3), result.requestedDate)
    }

    @Test
    fun `successful member mutations survive selecting away and back`() {
        val initialState = HomeMemberOrchestrationState(
            currentWindow = window,
            cachedWindows = mapOf(window.endDate to window),
            anchorEndDate = window.endDate,
        )
        val target = HomeMemberMutationTarget(
            date = LocalDate.of(2026, 9, 3),
            dailyQuoteSeq = 87,
        )
        val selectedAway = initialState.copy(
            currentWindow = window.select(LocalDate.of(2026, 9, 2))!!,
        )

        val patched = patchHomeMemberImage(
            state = patchHomeMemberLike(selectedAway, target, likeYn = "Y"),
            target = target,
            imagePath = "https://example.com/member-image.jpg",
        )
        assertEquals(LocalDate.of(2026, 9, 2), patched.currentWindow!!.selectedDate)
        val back = patched.currentWindow.select(LocalDate.of(2026, 9, 3))!!
        val cachedBack = patched.cachedWindows.getValue(LocalDate.of(2026, 9, 3))
            .select(LocalDate.of(2026, 9, 3))!!

        assertEquals("Y", back.selectedDay!!.likeYn)
        assertEquals("https://example.com/member-image.jpg", back.selectedDay!!.imagePath)
        assertEquals("Y", cachedBack.selectedDay!!.likeYn)
        assertEquals("https://example.com/member-image.jpg", cachedBack.selectedDay!!.imagePath)

        val deleted = patchHomeMemberImage(patched, target, imagePath = null)
        val deletedBack = deleted.cachedWindows.getValue(LocalDate.of(2026, 9, 3))
            .select(LocalDate.of(2026, 9, 3))!!
        assertNull(deletedBack.selectedDay!!.imagePath)
    }

    @Test
    fun `account identity change clears member cache and invalidates requests`() {
        val initialState = HomeMemberOrchestrationState(
            currentWindow = window,
            cachedWindows = mapOf(window.endDate to window),
            anchorEndDate = window.endDate,
            latestRequestId = 30,
        )

        val changed = transitionHomeAuthContext(
            state = initialState,
            previous = HomeAuthContext(isLoggedIn = true, accountIdentity = "account-a"),
            current = HomeAuthContext(isLoggedIn = true, accountIdentity = "account-b"),
        )

        assertNull(changed.currentWindow)
        assertTrue(changed.cachedWindows.isEmpty())
        assertNull(changed.anchorEndDate)
        assertEquals(31, changed.latestRequestId)
    }

    @Test
    fun `failed refresh retains loaded state when a usable member window exists`() {
        assertEquals(
            HomeQuoteLoadState.Loaded,
            homeMemberLoadStateAfterFailure(hasUsableWindow = true),
        )
        assertEquals(
            HomeQuoteLoadState.Failed,
            homeMemberLoadStateAfterFailure(hasUsableWindow = false),
        )
    }
}
