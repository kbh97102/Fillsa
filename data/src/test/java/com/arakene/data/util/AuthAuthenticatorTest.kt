package com.arakene.data.util

import androidx.paging.PagingData
import com.arakene.domain.model.StreakInfo
import com.arakene.domain.repository.LocalRepository
import com.arakene.domain.repository.TokenRepository
import com.arakene.domain.requests.LocalQuoteInfo
import com.arakene.domain.requests.TokenRefreshRequest
import com.arakene.domain.responses.DailyQuotaNoToken
import com.arakene.domain.responses.DailyQuoteDto
import com.arakene.domain.responses.TokenInfo
import com.arakene.domain.usecase.common.GetRefreshTokenUseCase
import com.arakene.domain.usecase.common.SetAccessTokenUseCase
import com.arakene.domain.usecase.common.SetRefreshTokenUseCase
import com.arakene.domain.usecase.common.UpdateTokenUseCase
import com.arakene.domain.util.DarkModeType
import com.arakene.domain.util.YN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AuthAuthenticatorTest {

    @Test
    fun authenticate_doesNotRequestRefreshToken_whenStoredRefreshTokenIsBlank() {
        val tokenRepository = FakeTokenRepository()
        val localRepository = FakeLocalRepository(refreshToken = "")
        val authenticator = AuthAuthenticator(
            updateTokenUseCase = UpdateTokenUseCase(tokenRepository),
            getRefreshTokenUseCase = GetRefreshTokenUseCase(localRepository),
            setRefreshTokenUseCase = SetRefreshTokenUseCase(localRepository),
            setAccessTokenUseCase = SetAccessTokenUseCase(localRepository)
        )

        val retryRequest = authenticator.authenticate(
            route = null,
            response = unauthorizedResponse()
        )

        assertNull(retryRequest)
        assertEquals(0, tokenRepository.updateAccessTokenCallCount)
        assertEquals("", localRepository.savedAccessToken)
        assertEquals("", localRepository.savedRefreshToken)
    }

    private fun unauthorizedResponse(): Response {
        val request = Request.Builder()
            .url("https://api.fillsa.com/api/v1/member-quotes/daily")
            .build()

        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(401)
            .message("Unauthorized")
            .body("".toResponseBody())
            .build()
    }
}

private class FakeTokenRepository : TokenRepository {
    var updateAccessTokenCallCount = 0

    override suspend fun updateAccessToken(request: TokenRefreshRequest): TokenInfo? {
        updateAccessTokenCallCount++
        return TokenInfo(accessToken = "new-access-token", refreshToken = "new-refresh-token")
    }
}

private class FakeLocalRepository(
    private val refreshToken: String
) : LocalRepository {
    var savedAccessToken = ""
    var savedRefreshToken = ""

    override suspend fun setAccessToken(token: String) {
        savedAccessToken = token
    }

    override suspend fun getAccessToken(): String = savedAccessToken

    override suspend fun setRefreshToken(token: String) {
        savedRefreshToken = token
    }

    override suspend fun getRefreshToken(): String = refreshToken

    override fun getLocalQuoteForWidget(): Flow<DailyQuoteDto?> = flowOf(null)
    override suspend fun setLocalQuoteForWidget(data: DailyQuotaNoToken) = Unit
    override suspend fun setImageUri(uri: String) = Unit
    override suspend fun setShareDescriptionVisible(boolean: Boolean) = Unit
    override suspend fun getShareDescriptionVisible(): Boolean = false
    override fun getImageUri(): Flow<String> = flowOf("")
    override fun getLoginStatus(): Flow<Boolean> = flowOf(false)
    override suspend fun isFirstOpen(): Flow<Boolean> = flowOf(false)
    override suspend fun setFirstOpen(value: Boolean) = Unit
    override suspend fun setAlarm(value: Boolean) = Unit
    override suspend fun setName(value: String) = Unit
    override fun getAlarm(): Flow<Boolean> = flowOf(false)
    override fun getName(): Flow<String> = flowOf("")
    override fun isAlarmPermissionRequestedBefore(): Flow<Boolean> = flowOf(false)
    override suspend fun setAlarmPermissionRequestedBefore(requested: Boolean) = Unit
    override suspend fun getLocalQuotes(): List<LocalQuoteInfo> = emptyList()
    override suspend fun addLocalQuote(quote: LocalQuoteInfo) = Unit
    override suspend fun deleteQuote(quote: LocalQuoteInfo) = Unit
    override suspend fun updateQuote(quote: LocalQuoteInfo) = Unit
    override fun getLocalQuotesPaging(
        likeYN: YN,
        startDate: String,
        endDate: String
    ): Flow<PagingData<LocalQuoteInfo>> = flowOf(PagingData.empty())

    override suspend fun updateLocalQuoteMemo(memo: String, seq: Int) = Unit
    override suspend fun updateLocalQuoteLike(likeYN: YN, seq: Int): Int = 0
    override suspend fun getQuoteLocal(seq: Int): LocalQuoteInfo? = null
    override suspend fun emitTokenExpired(errorCode: String) = Unit
    override fun getTokenExpired(): Flow<String> = flowOf("")
    override suspend fun findLocalQuoteById(seq: Int): LocalQuoteInfo? = null
    override suspend fun clear() = Unit
    override suspend fun deleteQuote(seq: Int) = Unit
    override suspend fun setDarkModeType(darkMode: DarkModeType) = Unit
    override fun getDarkModeType(): Flow<DarkModeType> = flowOf(DarkModeType.SYSTEM)
    override suspend fun setTodayStreakInfo() = Unit
    override suspend fun getYesterdayStreakInfo(): StreakInfo? = null
    override suspend fun getAllStreakInfos(): List<StreakInfo> = emptyList()
    override suspend fun getStreakDateCount(): Int = 0
    override suspend fun checkYesterdayStreak() = Unit
    override suspend fun getTodayLocalStreakInfo(): StreakInfo? = null
    override suspend fun checkPopupIsHidden(seq: Int): Boolean = false
    override suspend fun addHiddenPopup(seq: Int) = Unit
    override suspend fun clearAllHiddenPopUp() = Unit
}
