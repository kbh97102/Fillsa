package com.arakene.presentation.util


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
}


sealed interface HomeAction: Action {
    data object ClickNext: HomeAction
    data object ClickBefore: HomeAction
    data object Refresh: HomeAction
    data object ClickLike: HomeAction
    data object ClickQuote: HomeAction
    data class ClickImage(val isLogged: Boolean, val quote: String, val author: String) : HomeAction
}