package com.arakene.presentation.model

import com.arakene.domain.responses.*
import com.arakene.presentation.ui.calendar.calendarSelectedDayPresentation
import com.arakene.presentation.util.HomeAnswerUiState
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class CalendarAnswerStateTest {
    private val date = LocalDate.of(2026, 9, 1)
    private val quote = MemberQuotesData(7, date.toString(), "명언", "저자", true, "Y", false,
        engQuote = "Quote", engAuthor = "Author", authorUrl = "https://example.com/author",
        questionKo = "질문", questionEn = "Question", answer = "답변", answeredAt = "old",
        imagePath = "https://example.com/image.jpg")
    private val response = MemberMonthlyQuoteResponse(listOf(quote, quote.copy(dailyQuoteSeq = 8,
        quoteDate = "2026-09-02", answer = null)), MonthlySummaryData(9, 8, 7))
    private val auth = HomeAuthBoundRequestCoordinator().transition(HomeAuthContext(true, "account-a"))
    private fun state() = CalendarMonthState.from(response, date)

    @Test fun `monthly response maps localized question answer time image and English metadata`() {
        val selected = state().selectedQuote
        val actual = calendarSelectedDayPresentation(selected)
        assertEquals("질문", actual.question)
        assertEquals("답변", actual.answer)
        assertEquals("old", actual.answeredAt)
        assertEquals("https://example.com/image.jpg", actual.registeredImageUri)
        assertEquals("Quote", actual.engQuote)
        assertEquals("Author", actual.engAuthor)
        assertEquals("https://example.com/author", actual.authorUrl)
        assertEquals("Question", calendarSelectedDayPresentation(selected, "en").question)
        assertTrue(actual.completed)
        assertEquals(response.monthlySummary, state().data?.monthlySummary)
    }

    @Test fun `ordinary selection projects another monthly record without a network command`() {
        val selection = selectCalendarDay(state(), date.plusDays(1))
        assertNull(selection.networkCommand)
        assertEquals(8, selection.state.selectedQuote?.dailyQuoteSeq)
        assertNull(selection.state.answer.recordedAnswer)
    }

    @Test fun `save gates blank oversized missing sequence guest and duplicate requests`() {
        val coordinator = CalendarAnswerCoordinator()
        for (draft in listOf("  ", "a".repeat(201))) {
            assertNull(coordinator.begin(state(), HomeAnswerUiState(draft), auth.capture()!!).request)
        }
        assertNull(coordinator.begin(state().copy(selectedDate = date.plusDays(20)), HomeAnswerUiState("valid"), auth.capture()!!).request)
        val guest = auth.transition(HomeAuthContext(false, null))
        assertNull(coordinator.begin(state(), HomeAnswerUiState("valid"), guest.capture()!!).request)
        val start = coordinator.begin(state(), HomeAnswerUiState("valid"), auth.capture()!!)
        assertTrue(start.answer.isSaving)
        assertNull(start.coordinator.begin(state(), HomeAnswerUiState("other"), auth.capture()!!).request)
        assertEquals("답변", state().selectedQuote?.answer)
    }

    @Test fun `POST patches server normalized answer only and follows origin after selection`() {
        val start = CalendarAnswerCoordinator().begin(state(), HomeAnswerUiState("draft"), auth.capture()!!)
        val selected = selectCalendarDay(state(), date.plusDays(1)).state
        val result = start.coordinator.completePost(selected, auth, start.request!!, AnswerResponse(99, "normalized", "new"))
        assertTrue(result.shouldRefresh)
        assertEquals("normalized", result.state.data?.memberQuotes?.first()?.answer)
        assertEquals("new", result.state.data?.memberQuotes?.first()?.answeredAt)
        assertEquals(quote.copy(answer = "normalized", answeredAt = "new"), result.state.data?.memberQuotes?.first())
        assertEquals(response.monthlySummary, result.state.data?.monthlySummary)
        assertNull(result.state.answer.recordedAnswer)
    }

    @Test fun `POST failure retains draft and answer without daily refresh and account switch ignores success`() {
        val start = CalendarAnswerCoordinator().begin(state(), HomeAnswerUiState("draft"), auth.capture()!!)
        val failure = start.coordinator.completePost(state(), auth, start.request!!, null)
        assertFalse(failure.shouldRefresh)
        assertEquals("draft", failure.state.answer.draft)
        assertFalse(failure.state.answer.isSaving)
        assertEquals("답변", failure.state.selectedQuote?.answer)
        val switched = auth.transition(HomeAuthContext(true, "account-b"))
        val stale = start.coordinator.completePost(state(), switched, start.request, AnswerResponse(99, "wrong", "new"))
        assertEquals(state(), stale.state)
        assertFalse(stale.shouldRefresh)
    }

    @Test fun `daily failure cannot roll back POST and daily success cannot overwrite like image or completion`() {
        val start = CalendarAnswerCoordinator().begin(state(), HomeAnswerUiState("draft"), auth.capture()!!)
        val post = start.coordinator.completePost(state(), auth, start.request!!, AnswerResponse(99, "normalized", "new"))
        val refresh = post.coordinator.captureRefresh(post.state, start.request)
        assertEquals(post.state, post.coordinator.completeRefresh(post.state, auth, refresh, null))
        val day = MemberQuoteDay(date.toString(), "Tue", "completed", 7, "other", "Other", "Other", "Other", null,
            "other?", "Other?", "canonical", "later", "N", "wrong-image", false)
        val result = post.coordinator.completeRefresh(post.state, auth, refresh, day)
        assertEquals(quote.copy(answer = "canonical", answeredAt = "later"), result.selectedQuote)
        assertEquals(response.monthlySummary, result.data?.monthlySummary)
        assertEquals(post.state, post.coordinator.completeRefresh(post.state, auth, refresh, day.copy(dailyQuoteSeq = 88)))
    }

    @Test fun `old daily response cannot replace a newer save or monthly refresh`() {
        val first = CalendarAnswerCoordinator().begin(state(), HomeAnswerUiState("one"), auth.capture()!!)
        val post = first.coordinator.completePost(state(), auth, first.request!!, AnswerResponse(99, "one", "1"))
        val refresh = post.coordinator.captureRefresh(post.state, first.request)
        val second = post.coordinator.begin(post.state, HomeAnswerUiState("two"), auth.capture()!!)
        val secondPost = second.coordinator.completePost(post.state, auth, second.request!!, AnswerResponse(99, "two", "2"))
        val day = MemberQuoteDay(date.toString(), "Tue", "completed", 7, null, null, null, null, null,
            null, null, "old", "0", "N", null, false)
        assertEquals(secondPost.state, secondPost.coordinator.completeRefresh(secondPost.state, auth, refresh, day))
        val refreshed = post.state.copy(monthlyRevision = post.state.monthlyRevision + 1)
        assertEquals(refreshed, post.coordinator.completeRefresh(refreshed, auth, refresh, day))
    }

    @Test fun `monthly request started before POST cannot erase the accepted answer`() {
        val first = CalendarAnswerCoordinator().begin(state(), HomeAnswerUiState("one"), auth.capture()!!)
        val monthlyRequestRevision = first.coordinator.revision
        val post = first.coordinator.completePost(state(), auth, first.request!!, AnswerResponse(99, "one", "1"))
        val refreshed = post.coordinator.acceptMonthly(post.state, response, monthlyRequestRevision)
        assertEquals("one", refreshed.selectedQuote?.answer)
        assertEquals("1", refreshed.selectedQuote?.answeredAt)
        assertEquals(response.monthlySummary, refreshed.data?.monthlySummary)
    }

    @Test fun `daily completion preserves a new draft started after successful POST`() {
        val first = CalendarAnswerCoordinator().begin(state(), HomeAnswerUiState("one"), auth.capture()!!)
        val post = first.coordinator.completePost(state(), auth, first.request!!, AnswerResponse(99, "one", "1"))
        val refresh = post.coordinator.captureRefresh(post.state, first.request)
        val editing = post.state.copy(answer = HomeAnswerUiState("new draft", "one", true, dateKey = date))
        val day = MemberQuoteDay(date.toString(), "Tue", "completed", 7, null, null, null, null, null,
            null, null, "canonical", "2", "N", null, false)
        val actual = post.coordinator.completeRefresh(editing, auth, refresh, day)
        assertEquals("new draft", actual.answer.draft)
        assertTrue(actual.answer.isEditing)
        assertEquals("canonical", actual.selectedQuote?.answer)
    }

    @Test fun `origin POST completion cannot discard a different selected dates draft`() {
        val start = CalendarAnswerCoordinator().begin(state(), HomeAnswerUiState("one"), auth.capture()!!)
        val other = selectCalendarDay(state(), date.plusDays(1)).state.copy(
            answer = HomeAnswerUiState("other draft", dateKey = date.plusDays(1)))
        val actual = start.coordinator.completePost(other, auth, start.request!!, AnswerResponse(99, "one", "1"))
        assertEquals("other draft", actual.state.answer.draft)
    }

    @Test fun `loading an answered month replaces an untouched empty selection editor`() {
        val emptySelection = selectCalendarDay(CalendarMonthState(), date).state
        val actual = CalendarAnswerCoordinator().acceptMonthly(emptySelection, response, 0)
        assertEquals("답변", actual.answer.recordedAnswer)
        assertFalse(actual.answer.isEditing)
    }

    @Test fun `pending second save and failure cannot discard the prior accepted answer during stale monthly refresh`() {
        val coordinator = CalendarAnswerCoordinator()
        val monthlyRequestRevision = coordinator.revision
        val first = coordinator.begin(state(), HomeAnswerUiState("draft A"), auth.capture()!!)
        val postA = first.coordinator.completePost(state(), auth, first.request!!, AnswerResponse(99, "accepted A", "time A"))
        assertEquals("accepted A", postA.state.selectedQuote?.answer)
        val editorB = postA.state.answer.copy(draft = "draft B", isEditing = true)
        val second = postA.coordinator.begin(postA.state, editorB, auth.capture()!!)
        val pendingB = postA.state.copy(answer = second.answer)
        val monthly = second.coordinator.acceptMonthly(pendingB, response, monthlyRequestRevision)
        assertEquals("accepted A", monthly.selectedQuote?.answer)
        assertEquals("time A", monthly.selectedQuote?.answeredAt)
        assertEquals("accepted A", monthly.answer.recordedAnswer)
        assertEquals("draft B", monthly.answer.draft)
        assertTrue(monthly.answer.isSaving)
        val failedB = second.coordinator.completePost(monthly, auth, second.request!!, null)
        assertEquals("accepted A", failedB.state.selectedQuote?.answer)
        assertEquals("time A", failedB.state.selectedQuote?.answeredAt)
        assertEquals("accepted A", failedB.state.answer.recordedAnswer)
        assertEquals("draft B", failedB.state.answer.draft)
        assertFalse(failedB.state.answer.isSaving)
        assertFalse(failedB.shouldRefresh)
        val anotherOldMonthly = failedB.coordinator.acceptMonthly(failedB.state, response, monthlyRequestRevision)
        assertEquals("accepted A", anotherOldMonthly.selectedQuote?.answer)
        assertEquals(response.monthlySummary, anotherOldMonthly.data?.monthlySummary)
    }

    @Test fun `monthly refresh preserves explicit edit before any text change`() {
        val editing = editCalendarAnswer(state())
        assertEquals("답변", editing.answer.draft)
        assertTrue(editing.answer.isEditing)
        val refreshed = CalendarAnswerCoordinator().acceptMonthly(editing, response, 0)
        assertEquals("답변", refreshed.answer.draft)
        assertTrue(refreshed.answer.isEditing)
    }

    @Test fun `monthly refresh preserves explicit edit after text returns to original answer`() {
        val editing = editCalendarAnswer(state())
        val changed = changeCalendarAnswer(editing, "수정 중인 답변")
        val original = changeCalendarAnswer(changed, "답변")
        val refreshed = CalendarAnswerCoordinator().acceptMonthly(original, response, 0)
        assertEquals("답변", refreshed.answer.draft)
        assertEquals("답변", refreshed.answer.recordedAnswer)
        assertTrue(refreshed.answer.isEditing)
    }
}
