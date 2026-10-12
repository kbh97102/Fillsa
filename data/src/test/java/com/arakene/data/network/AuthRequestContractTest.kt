package com.arakene.data.network

import com.arakene.domain.requests.DeviceData
import com.arakene.domain.requests.DailySyncData
import com.arakene.domain.requests.LikeRequest
import com.arakene.domain.requests.LoginData
import com.arakene.domain.requests.LoginRequest
import com.arakene.domain.requests.MemoRequest
import com.arakene.domain.requests.TokenRefreshRequest
import com.arakene.domain.requests.TypingQuoteRequest
import com.arakene.domain.requests.UserData
import com.google.gson.JsonParser
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthRequestContractTest {
    private lateinit var server: MockWebServer
    private lateinit var retrofit: Retrofit

    @Before fun setUp() {
        server = MockWebServer()
        server.start()
        retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @After fun tearDown() = server.shutdown()

    @Test fun login_sendsInstallationAndDeviceMetadata() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(200).setBody("{}"))
        retrofit.create(FillsaNoTokenApi::class.java).login(
            LoginRequest(
                loginData = LoginData(
                    deviceData = DeviceData("fid-test-123", "ANDROID", "1.0.30", "16", "Pixel"),
                    userData = UserData("GOOGLE", "oauth-test-id", "tester", "")
                ),
                syncData = listOf(
                    DailySyncData(42, TypingQuoteRequest("한글", "English"), MemoRequest("memo"), LikeRequest("Y"))
                )
            )
        )

        val request = server.takeRequest()
        val body = JsonParser.parseString(request.body.readUtf8()).asJsonObject
        val device = body.getAsJsonObject("loginData").getAsJsonObject("deviceData")
        assertEquals("/api/v1/auth/login", request.path)
        assertEquals("fid-test-123", device.get("deviceId").asString)
        assertEquals("ANDROID", device.get("osType").asString)
        assertEquals("1.0.30", device.get("appVersion").asString)
        assertEquals("16", device.get("osVersion").asString)
        assertEquals("Pixel", device.get("deviceModel").asString)
        val user = body.getAsJsonObject("loginData").getAsJsonObject("userData")
        assertEquals("GOOGLE", user.get("oAuthProvider").asString)
        assertEquals("oauth-test-id", user.get("oAuthId").asString)
        assertEquals("tester", user.get("nickname").asString)
        assertEquals("", user.get("profileImageUrl").asString)
        val sync = body.getAsJsonArray("syncData")[0].asJsonObject
        assertEquals(42, sync.get("dailyQuoteSeq").asInt)
        assertEquals("한글", sync.getAsJsonObject("typingQuoteRequest").get("typingKorQuote").asString)
        assertEquals("English", sync.getAsJsonObject("typingQuoteRequest").get("typingEngQuote").asString)
        assertEquals("memo", sync.getAsJsonObject("memoRequest").get("memo").asString)
        assertEquals("Y", sync.getAsJsonObject("likeRequest").get("likeYn").asString)
    }

    @Test fun refresh_sendsInstallationAndRefreshToken() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(200).setBody("{}"))
        retrofit.create(TokenApi::class.java)
            .updateToken(TokenRefreshRequest("fid-test-123", "refresh-test-token"))

        val request = server.takeRequest()
        val body = JsonParser.parseString(request.body.readUtf8()).asJsonObject
        assertEquals("/api/v1/auth/refresh", request.path)
        assertEquals("fid-test-123", body.get("deviceId").asString)
        assertEquals("refresh-test-token", body.get("refreshToken").asString)
    }
}
