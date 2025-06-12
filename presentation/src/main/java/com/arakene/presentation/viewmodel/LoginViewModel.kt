package com.arakene.presentation.viewmodel

import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.arakene.domain.requests.DailySyncData
import com.arakene.domain.requests.DeviceData
import com.arakene.domain.requests.LikeRequest
import com.arakene.domain.requests.LoginData
import com.arakene.domain.requests.LoginRequest
import com.arakene.domain.requests.MemoRequest
import com.arakene.domain.requests.TypingQuoteRequest
import com.arakene.domain.requests.UserData
import com.arakene.domain.usecase.LoginUseCase
import com.arakene.domain.usecase.common.SetAccessTokenUseCase
import com.arakene.domain.usecase.common.SetRefreshTokenUseCase
import com.arakene.domain.usecase.common.SetUserNameUseCase
import com.arakene.domain.usecase.db.GetLocalQuoteListUseCase
import com.arakene.domain.usecase.home.SetImageUriUseCase
import com.arakene.domain.util.ApiResult
import com.arakene.presentation.util.Action
import com.arakene.presentation.util.BaseViewModel
import com.arakene.presentation.util.CommonEffect
import com.arakene.presentation.util.LoginAction
import com.arakene.presentation.util.LoginEffect
import com.google.firebase.installations.FirebaseInstallations
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val setRefreshTokenUseCase: SetRefreshTokenUseCase,
    private val setAccessTokenUseCase: SetAccessTokenUseCase,
    private val setImageUriUseCase: SetImageUriUseCase,
    private val setUserNameUseCase: SetUserNameUseCase,
    private val getLocalQuoteListUseCase: GetLocalQuoteListUseCase
) : BaseViewModel() {

    override fun handleAction(action: Action) {

        when (val loginAction = action as LoginAction) {

            is LoginAction.ClickGoogleLogin -> {
                loginGoogle(loginAction)
            }

            is LoginAction.ClickKakaoLogin -> {
                loginKakao(
                    accessToken = loginAction.accessToken,
                    refreshToken = loginAction.refreshToken,
                    expiresIn = loginAction.expiresIn,
                    refreshTokenExpiresIn = loginAction.refreshTokenExpiresIn,
                    appVersion = loginAction.appVersion
                )
            }

            is LoginAction.ClickNonMember -> {
                clickNonMember()
            }

            is LoginAction.ClickPrivacyPolicy -> {
                emitEffect(CommonEffect.OpenUri("https://home.fillsa.store/3p4kj92yn5qwkm57q1x8"))
            }

            is LoginAction.ClickTermsOfUse -> {
                emitEffect(CommonEffect.OpenUri("https://home.fillsa.store/7vgjr4m1n5gkk2dwpy86"))
            }

            else -> {}
        }
    }

    private fun clickNonMember() = viewModelScope.launch {
        setRefreshTokenUseCase("")
        setAccessTokenUseCase("")

        emitEffect(LoginEffect.Move)
    }

    private fun loginGoogle(loginAction: LoginAction.ClickGoogleLogin) {
        viewModelScope.launch {
            val (id, nickName, profileImageUrl) = getLoginUserData(loginAction.idToken)

            setUserNameUseCase(nickName)
            setImageUriUseCase(profileImageUrl)

            login(
                accessToken = loginAction.accessToken,
                refreshToken = loginAction.refreshToken,
                expiresIn = loginAction.accessTokenExpirationTime?.let {
                    formatToIso8601(it)
                } ?: "",
                refreshTokenExpiresIn = sixMonthsFromNowInIso8601(),
                id = id,
                nickName = nickName,
                profileImageUrl = profileImageUrl,
                provider = "GOOGLE",
                appVersion = loginAction.appVersion
            )
        }
    }

    private fun loginKakao(
        accessToken: String?,
        refreshToken: String?,
        refreshTokenExpiresIn: String?,
        expiresIn: String?,
        appVersion: String
    ) {
        viewModelScope.launch {
            val (id, nickName, imageUrl) = getKakaoUserData()

            setUserNameUseCase(nickName)
            setImageUriUseCase(imageUrl)

            login(
                accessToken = accessToken,
                refreshToken = refreshToken,
                refreshTokenExpiresIn = refreshTokenExpiresIn?.let {
                    convertToIso8601(it)
                },
                expiresIn = expiresIn?.let {
                    convertToIso8601(it)
                },
                id = id,
                nickName = nickName,
                profileImageUrl = imageUrl,
                provider = "KAKAO",
                appVersion = appVersion
            )

        }
    }

    private fun getLoginUserData(idToken: String?): Triple<String, String, String> {
        return try {

            if (idToken.isNullOrEmpty()) {
                return Triple("", "", "")
            }

            val parts = idToken.split(".")

            val payload = parts[1]
            val decoded =
                Base64.decode(payload, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
            val decodedString = String(decoded, charset("UTF-8"))

            val userData = JSONObject(decodedString).also {
                Log.e(">>>>", "json $it")
            }

            // Google
            val id = userData.optString("sub")
            val name = userData.optString("name")
            val image = userData.optString("picture")

            Triple(id, name, image)
                .also { Log.e(">>>>", "Google Info $it") }
        } catch (e: Exception) {
            Triple("", "", "")
        }
    }

    private suspend fun getKakaoUserData(): Triple<String, String, String> =
        suspendCoroutine { cont ->
            UserApiClient.instance.me { user, error ->
                if (error != null || user == null) {
                    cont.resume(Triple("", "", ""))
                } else {
                    Log.e(">>>>", "me? $user")

                    val id = user.id.toString()
                    val nickname = user.kakaoAccount?.profile?.nickname ?: ""
                    val imageUrl = user.kakaoAccount?.profile?.profileImageUrl ?: ""
                    cont.resume(Triple(id, nickname, imageUrl).also {
                        Log.e(">>>>", "KAKAO Info ${it}")
                    })
                }
            }
        }

    private fun login(
        accessToken: String?,
        refreshToken: String?,
        id: String?,
        nickName: String?,
        profileImageUrl: String?,
        refreshTokenExpiresIn: String? = null,
        expiresIn: String? = null,
        provider: String,
        appVersion: String
    ) = viewModelScope.launch {

        val fid = suspendCancellableCoroutine<String> { cont ->
            FirebaseInstallations.getInstance().id
                .addOnSuccessListener { fid ->
                    cont.resume(fid)
                }.addOnFailureListener {
                    it.printStackTrace()
                    cont.resume("")
                }
        }

        val syncData = getLocalQuoteListUseCase()
            .map {
                DailySyncData(
                    dailyQuoteSeq = it.dailyQuoteSeq,
                    typingQuoteRequest = TypingQuoteRequest(
                        typingKorQuote = it.korTyping,
                        typingEngQuote = it.engTyping
                    ),
                    memoRequest = MemoRequest(
                        memo = it.memo
                    ),
                    likeRequest = LikeRequest(
                        likeYn = it.likeYn
                    )
                )
            }


        val request = LoginRequest(
            LoginData(
                deviceData = DeviceData(
                    deviceId = fid,
                    osType = "ANDROID",
                    appVersion = appVersion,
                    osVersion = Build.VERSION.RELEASE,
                    deviceModel = Build.MODEL
                ),
                userData = UserData(
                    oAuthProvider = provider,
                    oAuthId = id ?: "",
                    nickname = nickName ?: "",
                    profileImageUrl = profileImageUrl ?: ""
                )
            ),
            syncData = syncData
        )

        when (val result = loginUseCase(request)) {
            is ApiResult.Success -> {

                setAccessTokenUseCase(result.data.accessToken)
                setRefreshTokenUseCase(result.data.refreshToken)

                Log.d(">>>>", "Success ${result.data}")
                emitEffect(LoginEffect.Move)
            }

            is ApiResult.Fail -> {
                Log.d(">>>>", "Fail ${result.error}")
            }

            is ApiResult.Error -> {
                Log.d(">>>>", "Error ${result.error}")
            }
        }
    }

    private fun convertToIso8601(dateString: String): String {
        // 1. 기존 문자열 파싱
        val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
        val date = inputFormat.parse(dateString)

        // 2. Date → ZonedDateTime (UTC로 변환)
        val zonedDateTime = date.toInstant().atZone(ZoneId.of("UTC"))

        // 3. ZonedDateTime → ISO 8601 문자열
        val isoFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        return zonedDateTime.format(isoFormat)
    }

    private fun formatToIso8601(expirationTimeMillis: Long): String {
        val instant = Instant.ofEpochMilli(expirationTimeMillis)
        return DateTimeFormatter.ISO_INSTANT.format(instant)
    }

    private fun sixMonthsFromNowInIso8601(): String {
        val nowUtc = ZonedDateTime.now(ZoneOffset.UTC)        // 현재 UTC 시간
        val sixMonthsLater = nowUtc.plusMonths(6)             // 6개월 후
        return sixMonthsLater.format(DateTimeFormatter.ISO_INSTANT)
    }
}