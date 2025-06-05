package com.arakene.domain.responses

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class MemberMonthlyQuoteResponse(
    @SerializedName("memberQuotes")
    val memberQuotes: List<MemberQuotesData>,

    @SerializedName("monthlySummary")
    val monthlySummary: MonthlySummaryData
)
