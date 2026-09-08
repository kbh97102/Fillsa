package com.arakene.data.network

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

class FillsaApiContractTest {

    @Test
    fun weeklyQuotes_usesV2RouteAndOptionalEndDateQuery() {
        val method = methodNamed("getWeeklyQuotes")

        assertEquals("/api/v2/member-quotes/weekly", method.requiredGet().value)
        assertTrue(method.parameterAnnotations.flatten().filterIsInstance<Query>().any {
            it.value == "endDate"
        })
    }

    @Test
    fun memberDailyQuote_usesV2RouteAndQuoteDateQuery() {
        val method = methodNamed("getMemberQuoteDay")

        assertEquals("/api/v2/member-quotes/daily", method.requiredGet().value)
        assertTrue(method.parameterAnnotations.flatten().filterIsInstance<Query>().any {
            it.value == "quoteDate"
        })
    }

    @Test
    fun postAnswer_usesDailyQuotePathAndRequestBody() {
        val method = methodNamed("postAnswer")

        assertEquals(
            "/api/v2/member-quotes/{dailyQuoteSeq}/answer",
            method.requiredPost().value
        )
        assertTrue(method.parameterAnnotations.flatten().filterIsInstance<Path>().any {
            it.value == "dailyQuoteSeq"
        })
        assertNotNull(method.parameterAnnotations.flatten().filterIsInstance<Body>().singleOrNull())
    }

    @Test
    fun postTyping_usesV2WhileGetTypingRemainsV1() {
        assertEquals(
            "/api/v2/member-quotes/{dailyQuoteSeq}/typing",
            methodNamed("postTyping").requiredPost().value
        )
        assertEquals(
            "/api/v1/member-quotes/{dailyQuoteSeq}/typing",
            methodNamed("getTyping").requiredGet().value
        )
    }

    private fun methodNamed(name: String) = FillsaApi::class.java.declaredMethods.single {
        it.name == name
    }

    private fun java.lang.reflect.Method.requiredGet() =
        requireNotNull(getAnnotation(GET::class.java))

    private fun java.lang.reflect.Method.requiredPost() =
        requireNotNull(getAnnotation(POST::class.java))
}
