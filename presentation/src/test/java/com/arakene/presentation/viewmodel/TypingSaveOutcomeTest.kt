package com.arakene.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.arakene.domain.repository.TypingRepository
import com.arakene.domain.requests.TypingQuoteRequest
import com.arakene.domain.requests.LocalQuoteInfo
import com.arakene.domain.responses.DailyQuoteDto
import com.arakene.domain.responses.MemberTypingQuoteResponse
import com.arakene.domain.responses.TypingSaveResponse
import com.arakene.domain.usecase.common.GetLoginStatusUseCase
import com.arakene.domain.usecase.common.GetMemberStreaksUseCase
import com.arakene.domain.usecase.db.AddLocalQuoteUseCase
import com.arakene.domain.usecase.common.DeleteLocalQuoteUseCase
import com.arakene.domain.usecase.db.GetLocalQuoteUseCase
import com.arakene.domain.usecase.db.InsertStreakInfoUseCase
import com.arakene.domain.usecase.db.UpdateLocalQuoteLikeUseCase
import com.arakene.domain.usecase.home.GetTypingUseCase
import com.arakene.domain.usecase.home.PostLikeUseCase
import com.arakene.domain.usecase.home.PostTypingUseCase
import com.arakene.domain.util.ApiResult
import com.arakene.domain.util.CommonError
import com.arakene.domain.util.YN
import com.arakene.presentation.util.CommonEffect
import com.arakene.presentation.util.LocaleType
import com.arakene.presentation.util.Screens
import com.arakene.presentation.util.TypingResultKind
import com.arakene.presentation.util.action.TypingAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

@OptIn(ExperimentalCoroutinesApi::class)
class TypingSaveOutcomeTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())
    private var viewModel: TypingViewModel? = null

    @After fun tearDown() { viewModel?.viewModelScope?.cancel() }

    @Test
    fun `failed member save never displays a result popup`() = runTest {
        val repository = FakeTypingRepository(ApiResult.Fail(CommonError.NetworkError))
        val vm = createViewModel(repository)
        val dialog = async(Dispatchers.Default) { withTimeoutOrNull(500) { vm.effect.filterIsInstance<CommonEffect.ShowDialog>().first() } }

        vm.handleContract(saveAction())

        assertNull(dialog.await())
    }

    @Test
    fun `completed save popup confirmation returns to Home`() = runTest {
        val vm = createViewModel(FakeTypingRepository(ApiResult.Success(savedTyping)))
        val dialog = async(Dispatchers.Default) { withTimeout(3_000) { vm.effect.filterIsInstance<CommonEffect.ShowDialog>().first() } }

        vm.handleContract(saveAction())
        val shown = dialog.await()
        val move = async(Dispatchers.Default) { withTimeout(3_000) { vm.effect.filterIsInstance<CommonEffect.Move>().first() } }

        assertNotNull(shown.dialogData.onClick)
        assertEquals(TypingResultKind.Streak, shown.dialogData.typingResultKind)
        shown.dialogData.onClick!!.invoke()
        assertEquals(Screens.Home(), move.await().screen)
    }

    @Test
    fun `guest completed save uses the continue writing popup`() = runTest {
        val vm = createViewModel(FakeTypingRepository(ApiResult.Success(savedTyping)), loggedIn = false)
        val dialog = async(Dispatchers.Default) { withTimeout(3_000) { vm.effect.filterIsInstance<CommonEffect.ShowDialog>().first() } }

        vm.handleContract(saveAction())

        val shown = dialog.await().dialogData
        assertEquals("오늘도 이어서 필사를 해주세요!", shown.title)
        assertEquals(TypingResultKind.Continue, shown.typingResultKind)
    }

    @Test
    fun `incomplete save offers typing without opening another typing route`() = runTest {
        val vm = createViewModel(FakeTypingRepository(ApiResult.Success(savedTyping)))
        val dialog = async(Dispatchers.Default) { withTimeout(3_000) { vm.effect.filterIsInstance<CommonEffect.ShowDialog>().first() } }

        vm.handleContract(saveAction().copy(korTyping = "오늘의"))

        val shown = dialog.await().dialogData
        assertEquals("필사가 완료되지 않았어요.", shown.title)
        assertEquals(TypingResultKind.Incomplete, shown.typingResultKind)
        assertEquals("필사하기", shown.okText)
        assertNull(shown.onClick)
    }

    @Test
    fun `guest empty save shows incomplete popup after deleting an old draft`() = runTest {
        val local = CountingLocalRepository(loggedIn = false).apply {
            localQuotes = listOf(LocalQuoteInfo(
                memo = "", korAuthor = "", korQuote = "오늘의 문장", engQuote = "",
                engAuthor = "", korTyping = "오늘의 문장", engTyping = "", dailyQuoteSeq = 42,
                likeYn = YN.N.type, date = "2026-10-11", dayOfWeek = "SUNDAY",
            ))
        }
        val vm = createViewModel(FakeTypingRepository(ApiResult.Success(savedTyping)), local = local)
        val dialog = async(Dispatchers.Default) { withTimeout(3_000) { vm.effect.filterIsInstance<CommonEffect.ShowDialog>().first() } }

        vm.handleContract(saveAction().copy(korTyping = ""))

        assertEquals(TypingResultKind.Incomplete, dialog.await().dialogData.typingResultKind)
        assertEquals(emptyList<LocalQuoteInfo>(), local.localQuotes)
    }

    @Test
    fun `repeated save taps issue one member request`() = runTest {
        val repository = FakeTypingRepository(ApiResult.Success(savedTyping), delayMillis = 150)
        val vm = createViewModel(repository)
        val dialog = async(Dispatchers.Default) { withTimeout(3_000) { vm.effect.filterIsInstance<CommonEffect.ShowDialog>().first() } }

        vm.handleContract(saveAction())
        vm.handleContract(saveAction())

        dialog.await()
        assertEquals(1, repository.saveCount.get())
    }

    private fun createViewModel(
        typing: TypingRepository,
        loggedIn: Boolean = true,
        local: CountingLocalRepository = CountingLocalRepository(loggedIn = loggedIn),
    ): TypingViewModel {
        return TypingViewModel(
            postLikeUseCase = PostLikeUseCase(CountingHomeRepository()),
            addLocalQuoteUseCase = AddLocalQuoteUseCase(local),
            getLoginStateUseCase = GetLoginStatusUseCase(local),
            updateLocalQuoteLikeUseCase = UpdateLocalQuoteLikeUseCase(local),
            getTypingUseCase = GetTypingUseCase(typing),
            postTypingUseCase = PostTypingUseCase(typing),
            getLocalQuoteUseCase = GetLocalQuoteUseCase(local),
            deleteLocalQuoteUseCase = DeleteLocalQuoteUseCase(local),
            getMemberStreakResponse = GetMemberStreaksUseCase(CountingCommonRepository(), local),
            setTodayTypingComplete = InsertStreakInfoUseCase(local),
        ).also { viewModel = it }
    }

    private fun saveAction() = TypingAction.Save(
        korTyping = "오늘의 문장",
        engTyping = "",
        dailyQuote = DailyQuoteDto().copy(dailyQuoteSeq = 42, korQuote = "오늘의 문장").apply {
            quoteDate = "2026-10-11"
        },
        localeType = LocaleType.KOR,
        isLike = false,
    )

    private val savedTyping = TypingSaveResponse(
        memberQuoteSeq = 7350633,
        completedChanged = false,
        todayCompletedChanged = false,
    )

    private class FakeTypingRepository(
        private val saveResult: ApiResult<TypingSaveResponse>,
        private val delayMillis: Long = 0,
    ) : TypingRepository {
        val saveCount = AtomicInteger(0)

        override suspend fun getTyping(dailyQuoteSeq: Int): ApiResult<MemberTypingQuoteResponse> =
            ApiResult.Fail(CommonError.DefaultError)

        override suspend fun postTyping(
            dailyQuoteSeq: Int,
            request: TypingQuoteRequest,
        ): ApiResult<TypingSaveResponse> {
            saveCount.incrementAndGet()
            if (delayMillis > 0) delay(delayMillis)
            return saveResult
        }
    }
}
