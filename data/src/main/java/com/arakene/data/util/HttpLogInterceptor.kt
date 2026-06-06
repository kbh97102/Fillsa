package com.arakene.data.util

import android.util.Log
import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer

class HttpLogInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val startedAt = System.currentTimeMillis()

        logLines(
            listOf(
                "---------- REQUEST ----------",
                "${request.method} ${request.url}",
                "header",
            ) + request.headers.toLogLines() + listOf(
                "message",
            ) + request.body.toMessage().toLogLines() + listOf(
                "-----------------------------",
            )
        )

        val response = chain.proceed(request)
        val tookMs = System.currentTimeMillis() - startedAt

        logLines(
            listOf(
                "---------- RESPONSE ----------",
                "${request.method} ${request.url}",
                "code ${response.code} (${tookMs}ms)",
                "header",
            ) + response.headers.toLogLines() + listOf(
                "message",
            ) + response.peekBody(MAX_RESPONSE_LOG_SIZE).string().toLogLines() + listOf(
                "------------------------------",
            )
        )

        return response
    }

    private fun RequestBody?.toMessage(): String {
        if (this == null) return ""

        return runCatching {
            val buffer = Buffer()
            writeTo(buffer)
            val charset = contentType()?.charset(Charsets.UTF_8) ?: Charsets.UTF_8
            buffer.readString(charset)
        }.getOrDefault("")
    }

    private fun okhttp3.Headers.toLogLines(): List<String> {
        return toString().trim().toLogLines()
    }

    private fun String.toLogLines(): List<String> {
        return if (isBlank()) {
            listOf("")
        } else {
            lines()
        }
    }

    private fun logLines(lines: List<String>) {
        lines.forEach { Log.d(TAG, it) }
    }

    companion object {
        private const val TAG = "HTTP"
        private const val MAX_RESPONSE_LOG_SIZE = 1024L * 1024L
    }
}
