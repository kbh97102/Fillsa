package com.arakene.domain

import com.arakene.domain.responses.AnswerResponse
import com.arakene.domain.responses.MemberMonthlyQuoteResponse
import com.arakene.domain.responses.MemberQuoteDay
import com.arakene.domain.responses.MemberWeeklyQuoteResponse
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MemberQuoteContractTest {

    private val gson = Gson()

    @Test
    fun weeklyResponse_decodesEveryDocumentedDayField() {
        val json = """
            {
              "startDate": "2026-09-07",
              "endDate": "2026-09-13",
              "days": [
                {
                  "date": "2026-09-08",
                  "dayOfWeek": "TUESDAY",
                  "state": "today",
                  "dailyQuoteSeq": 42,
                  "korQuote": "오늘의 문장",
                  "engQuote": "Quote of the day",
                  "korAuthor": "작가",
                  "engAuthor": "Author",
                  "authorUrl": "https://example.com/author",
                  "questionKo": "오늘 무엇을 배웠나요?",
                  "questionEn": "What did you learn today?",
                  "answer": "테스트를 먼저 작성했다.",
                  "answeredAt": "2026-09-08T14:00:00",
                  "likeYn": "Y",
                  "imagePath": "https://example.com/image.jpg",
                  "completed": true
                }
              ]
            }
        """.trimIndent()

        val response = gson.fromJson(json, MemberWeeklyQuoteResponse::class.java)
        val day: MemberQuoteDay = response.days.single()

        assertEquals("2026-09-07", response.startDate)
        assertEquals("2026-09-13", response.endDate)
        assertEquals("2026-09-08", day.date)
        assertEquals("TUESDAY", day.dayOfWeek)
        assertEquals("today", day.state)
        assertEquals(42, day.dailyQuoteSeq)
        assertEquals("오늘의 문장", day.korQuote)
        assertEquals("Quote of the day", day.engQuote)
        assertEquals("작가", day.korAuthor)
        assertEquals("Author", day.engAuthor)
        assertEquals("https://example.com/author", day.authorUrl)
        assertEquals("오늘 무엇을 배웠나요?", day.questionKo)
        assertEquals("What did you learn today?", day.questionEn)
        assertEquals("테스트를 먼저 작성했다.", day.answer)
        assertEquals("2026-09-08T14:00:00", day.answeredAt)
        assertEquals("Y", day.likeYn)
        assertEquals("https://example.com/image.jpg", day.imagePath)
        assertTrue(day.completed)
    }

    @Test
    fun dailyResponse_acceptsNullQuoteContentForEmptyDay() {
        val json = """
            {
              "date": "2026-09-09",
              "dayOfWeek": "WEDNESDAY",
              "state": "none",
              "dailyQuoteSeq": null,
              "korQuote": null,
              "engQuote": null,
              "korAuthor": null,
              "engAuthor": null,
              "authorUrl": null,
              "questionKo": null,
              "questionEn": null,
              "answer": null,
              "answeredAt": null,
              "likeYn": "N",
              "imagePath": null,
              "completed": false
            }
        """.trimIndent()

        val day = gson.fromJson(json, MemberQuoteDay::class.java)

        assertEquals("none", day.state)
        assertNull(day.dailyQuoteSeq)
        assertNull(day.korQuote)
        assertNull(day.answer)
        assertEquals("N", day.likeYn)
        assertFalse(day.completed)
    }

    @Test
    fun answerResponse_decodesServerNormalizedAnswer() {
        val json = """
            {
              "memberQuoteSeq": 99,
              "answer": "공백이 제거된 답변",
              "answeredAt": "2026-09-08T14:05:00"
            }
        """.trimIndent()

        val response = gson.fromJson(json, AnswerResponse::class.java)

        assertEquals(99, response.memberQuoteSeq)
        assertEquals("공백이 제거된 답변", response.answer)
        assertEquals("2026-09-08T14:05:00", response.answeredAt)
    }

    @Test
    fun monthlyResponse_decodesRenewedOptionalFields() {
        val json = """
            {
              "memberQuotes": [
                {
                  "dailyQuoteSeq": 7,
                  "quoteDate": "2026-09-08",
                  "quote": "한국어 문장",
                  "author": "작가",
                  "completed": true,
                  "likeYn": "N",
                  "todayCompleted": false,
                  "engQuote": "English quote",
                  "engAuthor": "Author",
                  "authorUrl": "https://example.com/author",
                  "questionKo": "질문",
                  "questionEn": "Question",
                  "answer": "답변",
                  "answeredAt": "2026-09-08T14:10:00",
                  "imagePath": "https://example.com/image.jpg"
                }
              ],
              "monthlySummary": {
                "typingCount": 1,
                "likeCount": 0,
                "streakCount": 2
              }
            }
        """.trimIndent()

        val response = gson.fromJson(json, MemberMonthlyQuoteResponse::class.java)
        val quote = response.memberQuotes.single()

        assertEquals("English quote", quote.engQuote)
        assertEquals("Author", quote.engAuthor)
        assertEquals("https://example.com/author", quote.authorUrl)
        assertEquals("질문", quote.questionKo)
        assertEquals("Question", quote.questionEn)
        assertEquals("답변", quote.answer)
        assertEquals("2026-09-08T14:10:00", quote.answeredAt)
        assertEquals("https://example.com/image.jpg", quote.imagePath)
    }
}
