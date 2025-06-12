package com.arakene.data.network

object ApiEndPoint {

    const val LOGIN = "/api/v1/auth/login"

    const val GET_DAILY_QUOTE_NON_MEMBER = "/api/v1/quotes/daily"

    const val GET_DAILY_QUOTE = "/api/v1/member-quotes/daily"

    const val POST_LIKE = "/api/v1/member-quotes/{dailyQuoteSeq}/like"

    const val POST_UPLOAD_IMAGE = "/api/v1/member-quotes/{dailyQuoteSeq}/images"

    const val DELETE_UPLOAD_IMAGE = "/api/v1/member-quotes/{dailyQuoteSeq}/images"

    const val GET_QUOTE_LIST = "/api/v1/member-quotes"

    const val POST_SAVE_MEMO = "/api/v1/member-quotes/{memberQuoteSeq}/memo"

    const val GET_QUOTE_MONTHLY = "/api/v1/member-quotes/monthly"

    const val GET_NOTICE = "/api/v1/notices"

    const val DELETE_RESIGN = "/api/v1/auth/auth/withdraw"

    const val UPDATE_ACCESS_TOKEN = "/api/v1/auth/refresh"

    const val POST_TYPING = "/api/v1/member-quotes/{dailyQuoteSeq}/typing"

    const val GET_TYPING = "/api/v1/member-quotes/{dailyQuoteSeq}/typing"
}