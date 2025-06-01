package com.arakene.presentation.ui

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.arakene.presentation.BuildConfig
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.LoginAction
import com.arakene.presentation.util.LoginEffect
import com.arakene.presentation.util.Screens
import com.arakene.presentation.viewmodel.LoginViewModel
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues


@Composable
fun LoginView(
    navigate: (Screens) -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val authService = remember { AuthorizationService(context) }

    val effects by viewModel.effect.collectAsState(null)

    // TODO: 추후에 다른 함수로 추출
    LaunchedEffect(effects) {

        effects ?: return@LaunchedEffect

        when (effects) {
            is LoginEffect.Move -> {
                val data = effects as LoginEffect.Move
                navigate(data.screen)
            }
        }

    }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val intent = result.data ?: return@rememberLauncherForActivityResult
            val resp = AuthorizationResponse.fromIntent(intent)
            val ex = AuthorizationException.fromIntent(intent)

            if (resp != null) {
                val tokenRequest = resp.createTokenExchangeRequest()
                authService.performTokenRequest(tokenRequest) { response, exception ->
                    if (response != null) {
                        viewModel.handleContract(
                            LoginAction.ClickGoogleLogin(
                                refreshToken = response.refreshToken,
                                accessToken = response.accessToken,
                                idToken = response.idToken,
                                accessTokenExpirationTime = response.accessTokenExpirationTime,
                                appVersion = context.packageManager.getPackageInfo(
                                    context.packageName,
                                    0
                                )
                                    .longVersionCode.toString()
                            )
                        )
                    } else {
                        Log.e("Auth", "Token Exchange Error", exception)
                    }
                }
            } else {
                Log.e("Auth", "Auth failed", ex)
            }
        }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 20.dp)
    ) {

        Image(
            modifier = Modifier.padding(top = 154.dp),
            painter = painterResource(R.drawable.icn_login_logo),
            contentDescription = null
        )


        Text(
            stringResource(R.string.login_description),
            style = FillsaTheme.typography.body2,
            color = colorResource(R.color.gray_700),
            modifier = Modifier.padding(top = 80.dp),
        )

        // 카카오
        LoginButton(
            icon = painterResource(R.drawable.icn_kakao),
            text = stringResource(R.string.login_kakao),
            backgroundColor = colorResource(R.color.kakao_yellow),
            modifier = Modifier.padding(top = 12.dp),
            onClick = {

                Utility.getKeyHash(context).also {
                    Log.e(">>>>", it)
                }

                if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                    UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                        if (error != null) {
                            Log.e(">>>>", "로그인 실패 $error")
                        } else if (token != null) {
                            Log.e(">>>>", "로그인 성공 $token")

                            Log.e(">>>>", "kakao id token ${token.idToken}")

                            viewModel.handleContract(
                                LoginAction.ClickKakaoLogin(
                                    refreshToken = token.refreshToken,
                                    accessToken = token.accessToken,
                                    refreshTokenExpiresIn = token.refreshTokenExpiresAt.toString(),
                                    expiresIn = token.accessTokenExpiresAt.toString(),
                                    appVersion = context.packageManager.getPackageInfo(
                                        context.packageName,
                                        0
                                    ).longVersionCode.toString()
                                )
                            )
                        }
                    }
                } else {
                    Log.e(">>>>", "Fail")
                }

            }
        )

        // 구글
        LoginButton(
            icon = painterResource(R.drawable.icn_google),
            text = stringResource(R.string.login_google),
            backgroundColor = colorResource(R.color.google_gray),
            modifier = Modifier.padding(top = 16.dp),
            onClick = {
                val serviceConfig = AuthorizationServiceConfiguration(
                    Uri.parse("https://accounts.google.com/o/oauth2/v2/auth"), // auth endpoint
                    Uri.parse("https://oauth2.googleapis.com/token")            // token endpoint
                )

                val authRequest = AuthorizationRequest.Builder(
                    serviceConfig,
                    BuildConfig.google_key,   // Google Cloud에서 생성한 OAuth 2.0 클라이언트 ID
                    ResponseTypeValues.CODE,
                    "com.arakene.fillsa:/oauth2redirect".toUri() // 앱에 등록한 redirect URI
                ).setScopes("openid", "email", "profile")
                    .build()


                val authIntent = authService.getAuthorizationRequestIntent(authRequest)
                launcher.launch(authIntent)
            },
        )

        // 비회원
        LoginButton(
            icon = painterResource(R.drawable.icn_pencil),
            text = stringResource(R.string.login_non_member),
            backgroundColor = colorResource(R.color.white),
            onClick = {
                navigate(Screens.Home)
            },
            modifier = Modifier.padding(top = 16.dp)
        )

        // 이용약관 및 개인정보 처리방침
        Row(
            modifier = Modifier.padding(top = 12.dp, bottom = 40.dp)
        ) {
            Text(
                stringResource(R.string.terms_of_use),
                style = FillsaTheme.typography.body2,
                color = colorResource(R.color.gray_500),
                textDecoration = TextDecoration.Underline
            )

            Text(
                stringResource(R.string.privacy_policy),
                style = FillsaTheme.typography.body2,
                color = colorResource(R.color.gray_500),
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.padding(start = 20.dp)
            )
        }

    }

}

@Composable
private fun LoginButton(
    icon: Painter,
    text: String,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier
            .background(color = backgroundColor, shape = MaterialTheme.shapes.small)
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clickable {
                onClick()
            }, verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(painter = icon, contentDescription = null)

        Text(
            text = text,
            modifier = Modifier.padding(start = 8.dp),
            color = colorResource(R.color.login_black),
            style = FillsaTheme.typography.subtitle2,
        )
    }

}

@Preview(showBackground = true, widthDp = 400)
@Composable
private fun LoginPreview() {
    LoginView(
        navigate = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun LoginButtonPreview() {
    LoginButton(
        icon = painterResource(R.drawable.icn_kakao),
        text = "카카오 꼐정으로 시작하기",
        backgroundColor = Color.Yellow,
        onClick = {}
    )
}


