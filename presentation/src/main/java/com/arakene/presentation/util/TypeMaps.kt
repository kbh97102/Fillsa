package com.arakene.presentation.util

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import com.arakene.domain.responses.DailyQuoteDto
import com.arakene.domain.responses.NoticeResponse
import kotlinx.serialization.json.Json

val DailyQuoteDtoTypeMap = object : NavType<DailyQuoteDto>(isNullableAllowed = false) {
    override fun put(bundle: Bundle, key: String, value: DailyQuoteDto) {
        bundle.putString(key, serializeAsValue(value))
    }

    override fun get(bundle: Bundle, key: String): DailyQuoteDto? {
        return bundle.getString(key)?.let { parseValue(it) }
    }

    override fun parseValue(value: String): DailyQuoteDto {
        val decodedJson = Uri.decode(value)
        return Json.decodeFromString(DailyQuoteDto.serializer(), decodedJson)
    }

    override fun serializeAsValue(value: DailyQuoteDto): String {
        val json = Json.encodeToString(DailyQuoteDto.serializer(), value)
        return Uri.encode(json)  // encode JSON to prevent special-character issues
    }
}


val NoticeResponseTypeMap = object : NavType<NoticeResponse>(isNullableAllowed = false) {
    override fun put(bundle: Bundle, key: String, value: NoticeResponse) {
        bundle.putString(key, serializeAsValue(value))
    }

    override fun get(bundle: Bundle, key: String): NoticeResponse? {
        return bundle.getString(key)?.let { parseValue(it) }
    }

    override fun parseValue(value: String): NoticeResponse {
        val decodedJson = Uri.decode(value)
        return Json.decodeFromString(NoticeResponse.serializer(), decodedJson)
    }

    override fun serializeAsValue(value: NoticeResponse): String {
        val json = Json.encodeToString(NoticeResponse.serializer(), value)
        return Uri.encode(json)  // encode JSON to prevent special-character issues
    }
}