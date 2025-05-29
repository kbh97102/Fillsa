package com.arakene.presentation.util


sealed interface LoginAction : Action {

    data class ClickGoogleLogin(
        val accessToken: String?,
        val refreshToken: String?,
        val idToken: String?,
        val accessTokenExpirationTime: Long?
    ) : LoginAction

    data class ClickKakaoLogin(
        val accessToken: String?,
        val refreshToken: String?,
        val refreshTokenExpiresIn: String? = null,
        val expiresIn: String? = null
    ) : LoginAction

}