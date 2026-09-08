package com.arakene.data.network

import com.arakene.domain.requests.AnswerRequest
import com.arakene.domain.requests.TypingQuoteRequest
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FillsaApiContractTest {

    private lateinit var server: MockWebServer
    private lateinit var api: FillsaApi
    private val gson = Gson()

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FillsaApi::class.java)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun weeklyQuotes_omitsEndDateOnFirstLoad() = runBlocking {
        enqueueJson(weeklyResponseJson)

        api.getWeeklyQuotes()

        val request = server.takeRequest()
        assertEquals("GET", request.method)
        assertEquals("/api/v2/member-quotes/weekly", request.requestUrl?.encodedPath)
        assertFalse(request.requestUrl!!.queryParameterNames.contains("endDate"))
    }

    @Test
    fun weeklyQuotes_sendsEndDateWhenPaging() = runBlocking {
        enqueueJson(weeklyResponseJson)

        api.getWeeklyQuotes(endDate = "2026-09-13")

        val request = server.takeRequest()
        assertEquals("/api/v2/member-quotes/weekly?endDate=2026-09-13", request.path)
    }

    @Test
    fun memberDailyQuote_sendsV2RouteAndQuoteDate() = runBlocking {
        enqueueJson(memberQuoteDayJson)

        api.getMemberQuoteDay(quoteDate = "2026-09-08")

        val request = server.takeRequest()
        assertEquals("GET", request.method)
        assertEquals("/api/v2/member-quotes/daily?quoteDate=2026-09-08", request.path)
    }

    @Test
    fun postAnswer_substitutesDailyQuoteSeqAndSendsExactJsonBody() = runBlocking {
        enqueueJson(
            """
                {
                  "memberQuoteSeq": 99,
                  "answer": "기록한 답변",
                  "answeredAt": "2026-09-08T14:05:00"
                }
            """.trimIndent()
        )

        api.postAnswer(
            dailyQuoteSeq = 42,
            body = AnswerRequest(answer = "기록한 답변")
        )

        val request = server.takeRequest()
        assertEquals("POST", request.method)
        assertEquals("/api/v2/member-quotes/42/answer", request.path)
        assertEquals("application/json; charset=UTF-8", request.getHeader("Content-Type"))
        assertEquals(
            gson.fromJson("""{"answer":"기록한 답변"}""", JsonObject::class.java),
            gson.fromJson(request.body.readUtf8(), JsonObject::class.java)
        )
    }

    @Test
    fun postTyping_usesV2Route() = runBlocking {
        enqueueJson("1")

        api.postTyping(
            dailyQuoteSeq = 42,
            body = TypingQuoteRequest(
                typingKorQuote = "한국어",
                typingEngQuote = "English"
            )
        )

        assertEquals("/api/v2/member-quotes/42/typing", server.takeRequest().path)
    }

    @Test
    fun getTyping_remainsOnV1Route() = runBlocking {
        enqueueJson("{}")

        api.getTyping(dailyQuoteSeq = 42)

        assertEquals("/api/v1/member-quotes/42/typing", server.takeRequest().path)
    }

    private fun enqueueJson(body: String) {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(body)
        )
    }

    private val memberQuoteDayJson = """
        {
          "date": "2026-09-08",
          "dayOfWeek": "TUESDAY",
          "state": "today",
          "dailyQuoteSeq": 42,
          "korQuote": "오늘의 문장",
          "engQuote": "Quote of the day",
          "korAuthor": "작가",
          "engAuthor": "Author",
          "authorUrl": null,
          "questionKo": "질문",
          "questionEn": "Question",
          "answer": null,
          "answeredAt": null,
          "likeYn": "N",
          "imagePath": null,
          "completed": false
        }
    """.trimIndent()

    private val weeklyResponseJson = """
        {
          "startDate": "2026-09-07",
          "endDate": "2026-09-13",
          "days": [$memberQuoteDayJson]
        }
    """.trimIndent()
}
