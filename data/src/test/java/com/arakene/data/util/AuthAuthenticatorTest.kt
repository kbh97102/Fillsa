package com.arakene.data.util

import androidx.paging.PagingData
import com.arakene.domain.model.StreakInfo
import com.arakene.domain.repository.LocalRepository
import com.arakene.domain.repository.InstallationIdProvider
import com.arakene.domain.repository.TokenRepository
import com.arakene.domain.requests.LocalQuoteInfo
import com.arakene.domain.requests.DeviceData
import com.arakene.domain.requests.LoginData
import com.arakene.domain.requests.LoginRequest
import com.arakene.domain.requests.TokenRefreshRequest
import com.arakene.domain.requests.UserData
import com.arakene.data.network.FillsaNoTokenApi
import com.arakene.data.network.TokenApi
import com.arakene.data.repository.TokenRepositoryImpl
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
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import com.google.gson.JsonParser
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthAuthenticatorTest {

    @Test
    fun loginThenExpiredRequest_refreshesWithSameInstallationIdAndRetriesOnce() = runBlocking {
        val server = MockWebServer()
        server.start()
        try {
            server.enqueue(MockResponse().setResponseCode(200).setBody(
                """{"accessToken":"old-access-token","refreshToken":"old-refresh-token","memberSeq":1,"nickname":"tester","profileImageUrl":""}"""
            ))
            server.enqueue(MockResponse().setResponseCode(401).setBody(
                """{"timestamp":"","httpStatus":401,"errorCode":3003,"message":"expired"}"""
            ))
            server.enqueue(MockResponse().setResponseCode(200).setBody(
                """{"accessToken":"new-access-token","refreshToken":"new-refresh-token"}"""
            ))
            server.enqueue(MockResponse().setResponseCode(200).setBody("ok"))

            val retrofit = Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val login = retrofit.create(FillsaNoTokenApi::class.java).login(
                LoginRequest(
                    LoginData(
                        DeviceData("fid-test-123", "ANDROID", "1.0.30", "16", "Pixel"),
                        UserData("GOOGLE", "oauth-test-id", "tester", "")
                    ),
                    emptyList()
                )
            ).body()!!
            val local = FakeLocalRepository(refreshToken = "")
            local.setAccessToken(login.accessToken)
            local.setRefreshToken(login.refreshToken)

            val tokenRepository = TokenRepositoryImpl(retrofit.create(TokenApi::class.java))
            val authenticator = AuthAuthenticator(
                UpdateTokenUseCase(tokenRepository),
                GetRefreshTokenUseCase(local),
                SetRefreshTokenUseCase(local),
                SetAccessTokenUseCase(local),
                FakeInstallationIdProvider("fid-test-123")
            )
            val client = OkHttpClient.Builder().authenticator(authenticator).build()
            client.newCall(
                Request.Builder().url(server.url("/protected"))
                    .header("Authorization", "Bearer ${login.accessToken}").build()
            ).execute().use { assertEquals(200, it.code) }

            val loginRequest = server.takeRequest()
            val firstProtected = server.takeRequest()
            val refreshRequest = server.takeRequest()
            val retryProtected = server.takeRequest()
            assertEquals("/api/v1/auth/login", loginRequest.path)
            assertEquals("/protected", firstProtected.path)
            assertEquals("/api/v1/auth/refresh", refreshRequest.path)
            assertEquals("/protected", retryProtected.path)
            val loginId = JsonParser.parseString(loginRequest.body.readUtf8()).asJsonObject
                .getAsJsonObject("loginData").getAsJsonObject("deviceData").get("deviceId").asString
            val refresh = JsonParser.parseString(refreshRequest.body.readUtf8()).asJsonObject
            assertEquals("fid-test-123", loginId)
            assertEquals(loginId, refresh.get("deviceId").asString)
            assertEquals("old-refresh-token", refresh.get("refreshToken").asString)
            assertEquals("Bearer old-access-token", firstProtected.getHeader("Authorization"))
            assertEquals("Bearer new-access-token", retryProtected.getHeader("Authorization"))
            assertEquals("new-access-token", local.savedAccessToken)
            assertEquals("new-refresh-token", local.savedRefreshToken)
            assertEquals(4, server.requestCount)
        } finally {
            server.shutdown()
        }
    }

    @Test
    fun rejectedRefresh_doesNotRetryProtectedRequestAgain() {
        val server = MockWebServer()
        server.start()
        try {
            server.enqueue(MockResponse().setResponseCode(401).setBody(
                """{"timestamp":"","httpStatus":401,"errorCode":3003,"message":"expired"}"""
            ))
            server.enqueue(MockResponse().setResponseCode(403).setBody("{}"))
            val retrofit = Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val local = FakeLocalRepository(refreshToken = "expired-refresh-token")
            val authenticator = AuthAuthenticator(
                UpdateTokenUseCase(TokenRepositoryImpl(retrofit.create(TokenApi::class.java))),
                GetRefreshTokenUseCase(local),
                SetRefreshTokenUseCase(local),
                SetAccessTokenUseCase(local),
                FakeInstallationIdProvider("fid-test-123")
            )
            val client = OkHttpClient.Builder().authenticator(authenticator).build()

            client.newCall(Request.Builder().url(server.url("/protected")).build()).execute().use {
                assertEquals(401, it.code)
            }

            assertEquals("/protected", server.takeRequest().path)
            val refresh = server.takeRequest()
            assertEquals("/api/v1/auth/refresh", refresh.path)
            assertEquals("fid-test-123", JsonParser.parseString(refresh.body.readUtf8())
                .asJsonObject.get("deviceId").asString)
            assertEquals(2, server.requestCount)
            assertEquals("", local.savedAccessToken)
            assertEquals("", local.savedRefreshToken)
        } finally {
            server.shutdown()
        }
    }

    @Test
    fun authenticate_sendsInstallationIdWhenRefreshingToken() {
        val tokenRepository = FakeTokenRepository()
        val localRepository = FakeLocalRepository(refreshToken = "old-refresh-token")
        val authenticator = AuthAuthenticator(
            updateTokenUseCase = UpdateTokenUseCase(tokenRepository),
            getRefreshTokenUseCase = GetRefreshTokenUseCase(localRepository),
            setRefreshTokenUseCase = SetRefreshTokenUseCase(localRepository),
            setAccessTokenUseCase = SetAccessTokenUseCase(localRepository),
            installationIdProvider = FakeInstallationIdProvider("fid-test-123")
        )

        val retryRequest = authenticator.authenticate(route = null, response = unauthorizedResponse())

        assertEquals("fid-test-123", tokenRepository.lastRequest?.deviceId)
        assertEquals("old-refresh-token", tokenRepository.lastRequest?.refreshToken)
        assertEquals("Bearer new-access-token", retryRequest?.header("Authorization"))
        assertEquals("new-access-token", localRepository.savedAccessToken)
        assertEquals("new-refresh-token", localRepository.savedRefreshToken)
    }

    @Test
    fun authenticate_doesNotRefreshWhenInstallationIdIsBlank() {
        val tokenRepository = FakeTokenRepository()
        val localRepository = FakeLocalRepository(refreshToken = "old-refresh-token")
        val authenticator = AuthAuthenticator(
            updateTokenUseCase = UpdateTokenUseCase(tokenRepository),
            getRefreshTokenUseCase = GetRefreshTokenUseCase(localRepository),
            setRefreshTokenUseCase = SetRefreshTokenUseCase(localRepository),
            setAccessTokenUseCase = SetAccessTokenUseCase(localRepository),
            installationIdProvider = FakeInstallationIdProvider("")
        )

        val retryRequest = authenticator.authenticate(route = null, response = unauthorizedResponse())

        assertNull(retryRequest)
        assertEquals(0, tokenRepository.updateAccessTokenCallCount)
    }

    @Test
    fun authenticate_doesNotRequestRefreshToken_whenStoredRefreshTokenIsBlank() {
        val tokenRepository = FakeTokenRepository()
        val localRepository = FakeLocalRepository(refreshToken = "")
        val authenticator = AuthAuthenticator(
            updateTokenUseCase = UpdateTokenUseCase(tokenRepository),
            getRefreshTokenUseCase = GetRefreshTokenUseCase(localRepository),
            setRefreshTokenUseCase = SetRefreshTokenUseCase(localRepository),
            setAccessTokenUseCase = SetAccessTokenUseCase(localRepository),
            installationIdProvider = FakeInstallationIdProvider("fid-test-123")
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
            .body("""{"timestamp":"","httpStatus":401,"errorCode":3003,"message":"expired"}""".toResponseBody())
            .build()
    }
}

private class FakeInstallationIdProvider(private val id: String) : InstallationIdProvider {
    override suspend fun getId(): String = id
}

private class FakeTokenRepository : TokenRepository {
    var updateAccessTokenCallCount = 0
    var lastRequest: TokenRefreshRequest? = null

    override suspend fun updateAccessToken(request: TokenRefreshRequest): TokenInfo? {
        updateAccessTokenCallCount++
        lastRequest = request
        return TokenInfo(accessToken = "new-access-token", refreshToken = "new-refresh-token")
    }
}

private class FakeLocalRepository(
    refreshToken: String
) : LocalRepository {
    private var currentRefreshToken = refreshToken
    var savedAccessToken = ""
    var savedRefreshToken = ""

    override suspend fun setAccessToken(token: String) {
        savedAccessToken = token
    }

    override suspend fun getAccessToken(): String = savedAccessToken

    override suspend fun setRefreshToken(token: String) {
        savedRefreshToken = token
        currentRefreshToken = token
    }

    override suspend fun getRefreshToken(): String = currentRefreshToken

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
