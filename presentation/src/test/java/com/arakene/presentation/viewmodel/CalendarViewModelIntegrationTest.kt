package com.arakene.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.arakene.domain.responses.AnswerResponse
import com.arakene.domain.responses.MemberMonthlyQuoteResponse
import com.arakene.domain.responses.MemberQuoteDay
import com.arakene.domain.responses.MemberQuotesData
import com.arakene.domain.responses.MonthlySummaryData
import com.arakene.domain.usecase.calendar.GetMonthlyQuotesNonMemberUseCase
import com.arakene.domain.usecase.calendar.GetQuotesMonthlyUseCase
import com.arakene.domain.usecase.common.GetAccessTokenUseCase
import com.arakene.domain.usecase.common.GetLoginStatusUseCase
import com.arakene.domain.usecase.db.GetLocalQuoteListUseCase
import com.arakene.domain.usecase.db.GetTodayLocalStreakInfoUseCase
import com.arakene.domain.usecase.home.GetMemberQuoteDayUseCase
import com.arakene.domain.usecase.home.SaveQuoteAnswerUseCase
import com.arakene.domain.util.ApiResult
import com.arakene.domain.util.CommonError
import com.arakene.presentation.util.CommonEffect
import com.arakene.presentation.util.Screens
import com.arakene.presentation.util.action.CalendarAction
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModelIntegrationTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()
    private var viewModel: CalendarViewModel? = null

    @After fun tearDown() { viewModel?.viewModelScope?.cancel() }

    @Test
    fun `member month loads once and SelectDay is cache-only`() = runTest {
        val home = CountingHomeRepository()
        val calendar = CountingCalendarRepository().apply { memberResult = ApiResult.Success(monthly()) }
        val vm = createViewModel(home, calendar, CountingLocalRepository(loggedIn = true))
        runCurrent()
        vm.handleContract(CommonEffect.Refresh)
        advanceUntilIdle()
        assertEquals(1, calendar.events.count { it.startsWith("member-monthly:") })
        assertEquals(0, home.networkCallCount)

        vm.handleContract(CalendarAction.SelectDay(day("2026-09-07")))
        runCurrent()

        assertEquals(1, calendar.events.size)
        assertEquals(0, home.networkCallCount)
        assertEquals(emptyList<String>(), home.events)
        assertEquals("2026-09-07", vm.selectedDay.value.date.toString())
    }

    @Test
    fun `member answer invokes POST then silent daily and preserves monthly summary completion like image`() = runTest {
        val initial = monthly()
        val home = CountingHomeRepository().apply {
            answerResult = ApiResult.Success(AnswerResponse(77, "서버 답변", "saved-at"))
            dailyResult = ApiResult.Fail(CommonError.NetworkError)
        }
        val calendar = CountingCalendarRepository().apply { memberResult = ApiResult.Success(initial) }
        val vm = createViewModel(home, calendar, CountingLocalRepository(loggedIn = true))
        val errors = mutableListOf<CommonError>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.error.toList(errors) }
        runCurrent()
        vm.handleContract(CommonEffect.Refresh)
        advanceUntilIdle()
        vm.handleContract(CalendarAction.SelectDay(day("2026-09-08")))
        runCurrent()
        vm.handleContract(CalendarAction.ChangeAnswer("내 답변"))
        vm.handleContract(CalendarAction.RecordAnswer)
        advanceUntilIdle()

        assertEquals(2, home.networkCallCount)
        assertEquals(listOf("answer:42:내 답변", "member-daily:2026-09-08"), home.events)
        val after = vm.data.value!!
        val quote = after.memberQuotes.single { it.quoteDate == "2026-09-08" }
        assertEquals(initial.monthlySummary, after.monthlySummary)
        assertTrue(quote.completed)
        assertEquals("Y", quote.likeYnString)
        assertEquals("https://example.com/image.png", quote.imagePath)
        assertEquals("서버 답변", quote.answer)
        assertTrue(errors.isEmpty())
    }

    @Test
    fun `guest month uses only guest API and SelectDay calls no member API`() = runTest {
        val home = CountingHomeRepository()
        val calendar = CountingCalendarRepository()
        val vm = createViewModel(home, calendar, CountingLocalRepository(loggedIn = false))
        runCurrent()
        vm.handleContract(CommonEffect.Refresh)
        advanceUntilIdle()
        vm.handleContract(CalendarAction.SelectDay(day("2026-09-08")))
        runCurrent()

        assertEquals(1, calendar.events.count { it.startsWith("guest-monthly:") })
        assertFalse(calendar.events.any { it.startsWith("member-monthly:") })
        assertEquals(0, home.networkCallCount)
        assertEquals(emptyList<String>(), home.events)
    }

    @Test
    fun `selected Calendar day is emitted as the exact Home target date`() = runTest {
        val home = CountingHomeRepository()
        val calendar = CountingCalendarRepository().apply { memberResult = ApiResult.Success(monthly()) }
        val vm = createViewModel(home, calendar, CountingLocalRepository(loggedIn = true))
        runCurrent()
        vm.handleContract(CommonEffect.Refresh)
        advanceUntilIdle()
        vm.handleContract(CalendarAction.SelectDay(day("2026-09-07")))
        runCurrent()
        Thread.sleep(260)

        val effect = async { vm.effect.first() }
        vm.handleContract(CalendarAction.ClickBottomQuote)
        runCurrent()
        val destination = (effect.await() as CommonEffect.Move).screen as Screens.Home

        assertEquals(2026, destination.targetYear)
        assertEquals(9, destination.targetMonth)
        assertEquals(7, destination.targetDay)
    }

    private fun createViewModel(
        home: CountingHomeRepository,
        calendar: CountingCalendarRepository,
        local: CountingLocalRepository,
    ) = CalendarViewModel(
        GetQuotesMonthlyUseCase(calendar), GetLocalQuoteListUseCase(local), GetLoginStatusUseCase(local),
        GetMonthlyQuotesNonMemberUseCase(calendar), GetTodayLocalStreakInfoUseCase(local),
        SaveQuoteAnswerUseCase(home), GetMemberQuoteDayUseCase(home), GetAccessTokenUseCase(local),
    ).also { viewModel = it }

    private fun day(date: String) = CalendarDay(LocalDate.parse(date), DayPosition.MonthDate)

    private fun monthly() = MemberMonthlyQuoteResponse(
        memberQuotes = listOf(
            MemberQuotesData(41, "2026-09-07", "전날", "작가", false, "N", false),
            MemberQuotesData(
                dailyQuoteSeq = 42, quoteDate = "2026-09-08", quote = "오늘", author = "작가",
                completed = true, likeYnString = "Y", todayCompleted = true,
                questionKo = "질문", questionEn = "Question", answer = null, answeredAt = null,
                imagePath = "https://example.com/image.png",
            ),
        ),
        monthlySummary = MonthlySummaryData(typingCount = 11, likeCount = 7, streakCount = 5),
    )
}
