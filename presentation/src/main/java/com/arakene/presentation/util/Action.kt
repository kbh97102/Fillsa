package com.arakene.presentation.util

import com.arakene.domain.responses.MemberQuotesResponse
import java.io.File


sealed interface LoginAction : Action {

    data class ClickGoogleLogin(
        val accessToken: String?,
        val refreshToken: String?,
        val idToken: String?,
        val accessTokenExpirationTime: Long?,
        val appVersion: String
    ) : LoginAction

    data class ClickKakaoLogin(
        val accessToken: String?,
        val refreshToken: String?,
        val refreshTokenExpiresIn: String? = null,
        val expiresIn: String? = null,
        val appVersion: String,
    ) : LoginAction

    data object ClickNonMember : LoginAction
}


sealed interface HomeAction : Action {
    data object ClickNext : HomeAction
    data object ClickBefore : HomeAction
    data object Refresh : HomeAction
    data object ClickLike : HomeAction
    data object ClickQuote : HomeAction
    data class ClickImage(val isLogged: Boolean, val quote: String, val author: String) : HomeAction
    data class ClickShare(val quote: String, val author: String) : HomeAction
    data class ClickChangeImage(val file: File?, val uri: String) : HomeAction
    data object ClickDeleteImage : HomeAction
}

sealed interface TypingAction : Action {
    // TODO: 공통으로?
    data class ClickShare(val quote: String, val author: String) : TypingAction
    data class ClickLike(val like: Boolean, val dailyQuoteSeq: Int) : TypingAction
}

sealed interface QuoteListAction : Action {
    data class ClickItem(
        val memberQuotesResponse: MemberQuotesResponse
    ): QuoteListAction
}