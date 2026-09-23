package com.arakene.presentation.viewmodel

import androidx.paging.PagingData
import com.arakene.domain.model.StreakInfo
import com.arakene.domain.repository.CalendarRepository
import com.arakene.domain.repository.CommonRepository
import com.arakene.domain.repository.HomeRepository
import com.arakene.domain.repository.LocalRepository
import com.arakene.domain.requests.AnswerRequest
import com.arakene.domain.requests.LikeRequest
import com.arakene.domain.requests.LocalQuoteInfo
import com.arakene.domain.responses.AnswerResponse
import com.arakene.domain.responses.DailyQuotaNoToken
import com.arakene.domain.responses.DailyQuoteDto
import com.arakene.domain.responses.MemberMonthlyQuoteResponse
import com.arakene.domain.responses.MemberQuoteDay
import com.arakene.domain.responses.MemberQuoteImageResponse
import com.arakene.domain.responses.MemberStreakResponse
import com.arakene.domain.responses.MemberWeeklyQuoteResponse
import com.arakene.domain.responses.MonthlyQuoteResponse
import com.arakene.domain.responses.NoticeResponse
import com.arakene.domain.responses.PopupResponse
import com.arakene.domain.util.ApiResult
import com.arakene.domain.util.CommonError
import com.arakene.domain.util.DarkModeType
import com.arakene.domain.util.YN
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.Dispatchers
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val dispatcher: TestDispatcher = StandardTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) = Dispatchers.setMain(dispatcher)
    override fun finished(description: Description) = Dispatchers.resetMain()
}

internal class CountingHomeRepository : HomeRepository {
    val events = mutableListOf<String>()
    var networkCallCount: Int = 0
        private set
    val weeklyEndDates = mutableListOf<String?>()
    var weeklyResult: ApiResult<MemberWeeklyQuoteResponse> = ApiResult.Fail(CommonError.DefaultError)
    var weeklyHandler: (suspend (String?) -> ApiResult<MemberWeeklyQuoteResponse>)? = null
    var dailyResult: ApiResult<MemberQuoteDay> = ApiResult.Fail(CommonError.DefaultError)
    var answerResult: ApiResult<AnswerResponse> = ApiResult.Fail(CommonError.DefaultError)
    var answerHandler: (suspend (Int, AnswerRequest) -> ApiResult<AnswerResponse>)? = null
    var likeHandler: (suspend (LikeRequest, Int) -> ApiResult<Int>)? = null
    var guestDailyResult: ApiResult<DailyQuotaNoToken> = ApiResult.Success(
        DailyQuotaNoToken(1, "guest", "guest", "author", "author", null),
    )

    private fun record(event: String) {
        networkCallCount += 1
        events += event
    }

    override suspend fun testErrorCode(code: Int): ApiResult<Unit> {
        record("test-error:$code")
        return ApiResult.Success(Unit)
    }
    override suspend fun getDailyQuoteNoToken(quoteDate: String): ApiResult<DailyQuotaNoToken> {
        record("guest-daily:$quoteDate")
        return guestDailyResult
    }
    override suspend fun getDailyQuote(quoteDate: String): ApiResult<DailyQuoteDto> {
        record("legacy-daily:$quoteDate")
        return ApiResult.Success(DailyQuoteDto())
    }
    override suspend fun getWeeklyQuotes(endDate: String?): ApiResult<MemberWeeklyQuoteResponse> {
        weeklyEndDates += endDate
        record("weekly:${endDate ?: "omitted"}")
        return weeklyHandler?.invoke(endDate) ?: weeklyResult
    }
    override suspend fun getMemberQuoteDay(quoteDate: String): ApiResult<MemberQuoteDay> {
        record("member-daily:$quoteDate")
        return dailyResult
    }
    override suspend fun postAnswer(dailyQuoteSeq: Int, request: AnswerRequest): ApiResult<AnswerResponse> {
        record("answer:$dailyQuoteSeq:${request.answer}")
        return answerHandler?.invoke(dailyQuoteSeq, request) ?: answerResult
    }
    override suspend fun postLike(likeRequest: LikeRequest, dailyQuoteSeq: Int): ApiResult<Int> {
        record("like:$dailyQuoteSeq:${likeRequest.likeYn}")
        return likeHandler?.invoke(likeRequest, dailyQuoteSeq) ?: ApiResult.Success(1)
    }
    override suspend fun postUploadImage(imageFile: File, dailyQuoteSeq: Int): ApiResult<MemberQuoteImageResponse> {
        record("upload-image:$dailyQuoteSeq:${imageFile.name}")
        return ApiResult.Success(MemberQuoteImageResponse(dailyQuoteSeq, "image"))
    }
    override suspend fun deleteUploadImage(dailyQuoteSeq: Int): ApiResult<Int> {
        record("delete-image:$dailyQuoteSeq")
        return ApiResult.Success(1)
    }
}

internal class CountingCalendarRepository : CalendarRepository {
    val events = mutableListOf<String>()
    var memberResult: ApiResult<MemberMonthlyQuoteResponse> = ApiResult.Fail(CommonError.DefaultError)
    var guestResult: ApiResult<List<MonthlyQuoteResponse>> = ApiResult.Success(emptyList())

    override suspend fun getQuotesMonthly(yearMonth: String): ApiResult<MemberMonthlyQuoteResponse> {
        events += "member-monthly:$yearMonth"
        return memberResult
    }
    override suspend fun getQuotesMonthlyNonMember(yearMonth: String): ApiResult<List<MonthlyQuoteResponse>> {
        events += "guest-monthly:$yearMonth"
        return guestResult
    }
}

internal class CountingLocalRepository(
    loggedIn: Boolean,
    var accessToken: String = if (loggedIn) "account-a" else "",
) : LocalRepository {
    val loginStatus = MutableSharedFlow<Boolean>(replay = 1).apply { tryEmit(loggedIn) }
    var localQuotes: List<LocalQuoteInfo> = emptyList()
    var streakInfos: List<StreakInfo> = emptyList()

    override fun getLocalQuoteForWidget(): Flow<DailyQuoteDto?> = flowOf(null)
    override suspend fun setLocalQuoteForWidget(data: DailyQuotaNoToken) = Unit
    override suspend fun setAccessToken(token: String) { accessToken = token }
    override suspend fun getAccessToken(): String = accessToken
    override suspend fun setRefreshToken(token: String) = Unit
    override suspend fun getRefreshToken(): String = ""
    override suspend fun setImageUri(uri: String) = Unit
    override suspend fun setShareDescriptionVisible(boolean: Boolean) = Unit
    override suspend fun getShareDescriptionVisible(): Boolean = false
    override fun getImageUri(): Flow<String> = flowOf("")
    override fun getLoginStatus(): Flow<Boolean> = loginStatus
    override suspend fun isFirstOpen(): Flow<Boolean> = flowOf(false)
    override suspend fun setFirstOpen(value: Boolean) = Unit
    override suspend fun setAlarm(value: Boolean) = Unit
    override suspend fun setName(value: String) = Unit
    override fun getAlarm(): Flow<Boolean> = flowOf(false)
    override fun getName(): Flow<String> = flowOf("")
    override fun isAlarmPermissionRequestedBefore(): Flow<Boolean> = flowOf(false)
    override suspend fun setAlarmPermissionRequestedBefore(requested: Boolean) = Unit
    override suspend fun getLocalQuotes(): List<LocalQuoteInfo> = localQuotes
    override suspend fun addLocalQuote(quote: LocalQuoteInfo) { localQuotes = localQuotes + quote }
    override suspend fun deleteQuote(quote: LocalQuoteInfo) { localQuotes = localQuotes - quote }
    override suspend fun updateQuote(quote: LocalQuoteInfo) = Unit
    override fun getLocalQuotesPaging(likeYN: YN, startDate: String, endDate: String): Flow<PagingData<LocalQuoteInfo>> =
        flowOf(PagingData.empty())
    override suspend fun updateLocalQuoteMemo(memo: String, seq: Int) = Unit
    override suspend fun updateLocalQuoteLike(likeYN: YN, seq: Int): Int = 1
    override suspend fun getQuoteLocal(seq: Int): LocalQuoteInfo? = localQuotes.firstOrNull { it.dailyQuoteSeq == seq }
    override suspend fun emitTokenExpired(errorCode: String) = Unit
    override fun getTokenExpired(): Flow<String> = flowOf("")
    override suspend fun findLocalQuoteById(seq: Int): LocalQuoteInfo? = getQuoteLocal(seq)
    override suspend fun clear() { localQuotes = emptyList() }
    override suspend fun deleteQuote(seq: Int) { localQuotes = localQuotes.filterNot { it.dailyQuoteSeq == seq } }
    override suspend fun setDarkModeType(darkMode: DarkModeType) = Unit
    override fun getDarkModeType(): Flow<DarkModeType> = flowOf(DarkModeType.SYSTEM)
    override suspend fun setTodayStreakInfo() = Unit
    override suspend fun getYesterdayStreakInfo(): StreakInfo? = null
    override suspend fun getAllStreakInfos(): List<StreakInfo> = streakInfos
    override suspend fun getStreakDateCount(): Int = streakInfos.size
    override suspend fun checkYesterdayStreak() = Unit
    override suspend fun getTodayLocalStreakInfo(): StreakInfo? = streakInfos.lastOrNull()
    override suspend fun checkPopupIsHidden(seq: Int): Boolean = false
    override suspend fun addHiddenPopup(seq: Int) = Unit
    override suspend fun clearAllHiddenPopUp() = Unit
}

internal class CountingCommonRepository : CommonRepository {
    override fun getNotice(): Flow<PagingData<NoticeResponse>> = flowOf(PagingData.empty())
    override suspend fun getMemberStreaks(): ApiResult<MemberStreakResponse> =
        ApiResult.Success(MemberStreakResponse(currentStreak = 7, isTodayWritten = false))
    override suspend fun getPopUpGeneral(): ApiResult<PopupResponse> = ApiResult.Fail(CommonError.DefaultError)
    override suspend fun getPopUpVersionUpdate(): ApiResult<PopupResponse> = ApiResult.Fail(CommonError.DefaultError)
    override suspend fun deleteResign(): ApiResult<Int> = ApiResult.Success(1)
}
