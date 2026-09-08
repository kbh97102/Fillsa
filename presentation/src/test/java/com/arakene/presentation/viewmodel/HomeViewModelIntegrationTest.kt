package com.arakene.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.arakene.domain.responses.AnswerResponse
import com.arakene.domain.responses.MemberQuoteDay
import com.arakene.domain.responses.MemberWeeklyQuoteResponse
import com.arakene.domain.usecase.TestErrorCodeUseCase
import com.arakene.domain.usecase.common.GetAccessTokenUseCase
import com.arakene.domain.usecase.common.GetLoginStatusUseCase
import com.arakene.domain.usecase.common.GetStreakCountUseCase
import com.arakene.domain.usecase.db.AddLocalQuoteUseCase
import com.arakene.domain.usecase.db.FindLocalQuoteByIdUseCase
import com.arakene.domain.usecase.db.GetAllStreakInfoUseCase
import com.arakene.domain.usecase.db.GetLocalQuoteListUseCase
import com.arakene.domain.usecase.db.UpdateLocalQuoteLikeUseCase
import com.arakene.domain.usecase.home.DeleteUploadImageUseCase
import com.arakene.domain.usecase.home.GetDailyQuoteNoTokenUseCase
import com.arakene.domain.usecase.home.GetDailyQuoteUseCase
import com.arakene.domain.usecase.home.GetMemberQuoteDayUseCase
import com.arakene.domain.usecase.home.GetMemberWeeklyQuotesUseCase
import com.arakene.domain.usecase.home.PostLikeUseCase
import com.arakene.domain.usecase.home.PostUploadImageUseCase
import com.arakene.domain.usecase.home.SaveQuoteAnswerUseCase
import com.arakene.domain.util.ApiResult
import com.arakene.domain.util.CommonError
import com.arakene.presentation.util.action.HomeAction
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelIntegrationTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()
    private var viewModel: HomeViewModel? = null

    @After fun tearDown() { viewModel?.viewModelScope?.cancel() }

    @Test
    fun `member initial load and in-window month boundary selection use one weekly request`() = runTest {
        val home = CountingHomeRepository().apply { weeklyResult = ApiResult.Success(weekly()) }
        val local = CountingLocalRepository(loggedIn = true)
        val vm = createViewModel(home, local)

        vm.initialRefresh(null)
        advanceUntilIdle()
        assertEquals(listOf<String?>(null), home.weeklyEndDates)
        assertEquals(1, home.networkCallCount)

        vm.handleContract(HomeAction.SelectWeekDay(LocalDate.parse("2026-09-01")))
        runCurrent()

        assertEquals(listOf<String?>(null), home.weeklyEndDates)
        assertEquals(1, home.networkCallCount)
        assertEquals(LocalDate.parse("2026-09-01"), vm.date.value)
        assertTrue(LocalDate.parse("2026-08-30") in vm.completedDates)
    }

    @Test
    fun `Calendar target first anchors weekly then requests aligned target window`() = runTest {
        val home = CountingHomeRepository().apply {
            weeklyResult = ApiResult.Success(weekly())
        }
        val vm = createViewModel(home, CountingLocalRepository(loggedIn = true))

        vm.initialRefresh(LocalDate.parse("2026-08-20"))
        advanceUntilIdle()

        assertEquals(listOf(null, "2026-08-20"), home.weeklyEndDates)
    }

    @Test
    fun `logout clears member window and guest refresh calls no member endpoint`() = runTest {
        val home = CountingHomeRepository().apply { weeklyResult = ApiResult.Success(weekly()) }
        val local = CountingLocalRepository(loggedIn = true)
        val vm = createViewModel(home, local)
        vm.initialRefresh(null)
        advanceUntilIdle()
        assertTrue(vm.memberQuoteWindow != null)
        val eventsBeforeLogout = home.events.toList()
        assertEquals(listOf("weekly:omitted"), eventsBeforeLogout)

        local.loginStatus.emit(false)
        advanceUntilIdle()

        assertNull(vm.memberQuoteWindow)
        assertEquals(1, home.weeklyEndDates.size)
        assertEquals(2, home.networkCallCount)
        assertEquals(eventsBeforeLogout, home.events.dropLast(1))
        assertTrue(home.events.last().startsWith("guest-daily:"))
    }

    @Test
    fun `account identity change discards cached window and anchors a fresh request`() = runTest {
        val home = CountingHomeRepository().apply { weeklyResult = ApiResult.Success(weekly()) }
        val local = CountingLocalRepository(loggedIn = true)
        val vm = createViewModel(home, local)
        vm.initialRefresh(null)
        advanceUntilIdle()

        local.accessToken = "account-b"
        local.loginStatus.emit(true)
        advanceUntilIdle()

        assertEquals(listOf(null, null), home.weeklyEndDates)
        assertEquals(LocalDate.parse("2026-09-03"), vm.memberQuoteWindow?.selectedDate)
    }

    @Test
    fun `member answer invokes POST then silent best-effort daily without changing completion`() = runTest {
        val home = CountingHomeRepository().apply {
            weeklyResult = ApiResult.Success(weekly())
            answerResult = ApiResult.Success(AnswerResponse(999, "서버 답변", "2026-09-08 09:00:00"))
            dailyResult = ApiResult.Fail(CommonError.NetworkError)
        }
        val vm = createViewModel(home, CountingLocalRepository(loggedIn = true))
        val errors = mutableListOf<CommonError>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.error.toList(errors) }
        vm.initialRefresh(null)
        advanceUntilIdle()

        vm.handleContract(HomeAction.ChangeAnswer("내 답변"))
        runCurrent()
        vm.handleContract(HomeAction.RecordAnswer)
        advanceUntilIdle()

        assertEquals(3, home.networkCallCount)
        assertEquals(
            listOf("weekly:omitted", "answer:107:내 답변", "member-daily:2026-09-03"),
            home.events,
        )
        assertEquals("서버 답변", vm.answerUiState.recordedAnswer)
        assertFalse(vm.memberQuoteWindow!!.selectedDay!!.completed)
        assertEquals("today", vm.memberQuoteWindow!!.selectedDay!!.state)
        assertTrue(errors.isEmpty())
    }

    private fun createViewModel(home: CountingHomeRepository, local: CountingLocalRepository): HomeViewModel =
        HomeViewModel(
            GetDailyQuoteNoTokenUseCase(home), GetDailyQuoteUseCase(home), GetMemberWeeklyQuotesUseCase(home),
            GetMemberQuoteDayUseCase(home), SaveQuoteAnswerUseCase(home), GetLoginStatusUseCase(local),
            PostLikeUseCase(home), PostUploadImageUseCase(home), DeleteUploadImageUseCase(home),
            UpdateLocalQuoteLikeUseCase(local), GetLocalQuoteListUseCase(local), FindLocalQuoteByIdUseCase(local),
            AddLocalQuoteUseCase(local), TestErrorCodeUseCase(home),
            GetStreakCountUseCase(local, CountingCommonRepository()), GetAllStreakInfoUseCase(local),
            GetAccessTokenUseCase(local),
        ).also { viewModel = it }

    private fun weekly() = MemberWeeklyQuoteResponse(
        startDate = "2026-08-28", endDate = "2026-09-03",
        days = listOf(
            day("2026-08-28", "past", 101), day("2026-08-29", "past", 102),
            day("2026-08-30", "done", 103, completed = true), day("2026-08-31", "past", 104),
            day("2026-09-01", "past", 105), day("2026-09-02", "past", 106),
            day("2026-09-03", "today", 107),
        ),
    )

    private fun day(date: String, state: String, seq: Int, completed: Boolean = false) = MemberQuoteDay(
        date, "TUESDAY", state, seq, "명언", "Quote", "작가", "Author", null,
        "질문", "Question", null, null, "N", null, completed,
    )
}
