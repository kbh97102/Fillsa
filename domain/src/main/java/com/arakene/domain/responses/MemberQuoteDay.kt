package com.arakene.domain.responses

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class MemberQuoteDay(
    @SerializedName("date")
    val date: String,

    @SerializedName("dayOfWeek")
    val dayOfWeek: String,

    @SerializedName("state")
    val state: String,

    @SerializedName("dailyQuoteSeq")
    val dailyQuoteSeq: Int?,

    @SerializedName("korQuote")
    val korQuote: String?,

    @SerializedName("engQuote")
    val engQuote: String?,

    @SerializedName("korAuthor")
    val korAuthor: String?,

    @SerializedName("engAuthor")
    val engAuthor: String?,

    @SerializedName("authorUrl")
    val authorUrl: String?,

    @SerializedName("questionKo")
    val questionKo: String?,

    @SerializedName("questionEn")
    val questionEn: String?,

    @SerializedName("answer")
    val answer: String?,

    @SerializedName("answeredAt")
    val answeredAt: String?,

    @SerializedName("likeYn")
    val likeYn: String,

    @SerializedName("imagePath")
    val imagePath: String?,

    @SerializedName("completed")
    val completed: Boolean
)

@Keep
data class MemberWeeklyQuoteResponse(
    @SerializedName("startDate")
    val startDate: String,

    @SerializedName("endDate")
    val endDate: String,

    @SerializedName("days")
    val days: List<MemberQuoteDay>
)
