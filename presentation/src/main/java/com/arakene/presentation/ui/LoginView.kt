package com.arakene.presentation.ui

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.arakene.presentation.BuildConfig
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.CommonEffect
import com.arakene.presentation.util.DialogData
import com.arakene.presentation.util.DialogDataHolder
import com.arakene.presentation.util.HandleViewEffect
import com.arakene.presentation.util.LocalDialogDataHolder
import com.arakene.presentation.util.LoginAction
import com.arakene.presentation.util.LoginEffect
import com.arakene.presentation.util.LoginErrorException
import com.arakene.presentation.util.Screens
import com.arakene.presentation.util.noEffectClickable
import com.arakene.presentation.viewmodel.LoginViewModel
import com.google.firebase.crashlytics.FirebaseCrashlytics
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
    popBackStack: () -> Unit,
    darkMode: Boolean = isSystemInDarkTheme(),
    viewModel: LoginViewModel = hiltViewModel(),
    dialogDataHolder: DialogDataHolder = LocalDialogDataHolder.current,
    isOnboarding: Boolean = true
) {

    val context = LocalContext.current

    val authService = remember { AuthorizationService(context) }

    val uriHandler = LocalUriHandler.current

    val lifeCycle = LocalLifecycleOwner.current

    var testClickCount by remember {
        mutableStateOf(0)
    }

    HandleViewEffect(
        viewModel.effect,
        lifeCycle
    ) { effect ->
        when (effect) {
            is CommonEffect.Move -> {
                navigate(effect.screen)
            }

            is CommonEffect.OpenUri -> {
                uriHandler.openUri(effect.uri)
            }

            is LoginEffect.Move -> {
                navigate(Screens.Home())
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
                                    .longVersionCode.toString(),
                                isOnboarding = isOnboarding
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
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
    ) {

        if (isOnboarding) {
            LoginOnBoardingTopSection(
                popBackStack = popBackStack
            )
        }

        Image(
            modifier = Modifier
                .padding(top = 154.dp)
                .noEffectClickable {

                    testClickCount++

                    if (testClickCount >= 5) {
                        viewModel.testLoginMethod()
                    }

                },
            painter = if (darkMode) {
                painterResource(R.drawable.icn_login_logo_dark)
            } else {
                painterResource(R.drawable.icn_login_logo)
            },
            contentDescription = null
        )


        Text(
            stringResource(R.string.login_description),
            style = FillsaTheme.typography.body2,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(top = 80.dp),
        )

        // 카카오
        LoginButton(
            icon = painterResource(R.drawable.icn_kakao),
            text = stringResource(R.string.login_kakao),
            backgroundColor = colorResource(R.color.kakao_yellow),
            modifier = Modifier.padding(top = 12.dp),
            onClick = {
                if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                    UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                        if (error == null) {
                            if (token != null) {
                                viewModel.handleContract(
                                    LoginAction.ClickKakaoLogin(
                                        refreshToken = token.refreshToken,
                                        accessToken = token.accessToken,
                                        refreshTokenExpiresIn = token.refreshTokenExpiresAt.toString(),
                                        expiresIn = token.accessTokenExpiresAt.toString(),
                                        appVersion = context.packageManager.getPackageInfo(
                                            context.packageName,
                                            0
                                        ).longVersionCode.toString(),
                                        isOnboarding = isOnboarding
                                    )
                                )
                            } else {
                                FirebaseCrashlytics.getInstance().recordException(
                                    LoginErrorException("KAKAO Login Token is Null")
                                )
                            }
                        } else {
                            error.localizedMessage?.let {
                                FirebaseCrashlytics.getInstance().recordException(
                                    LoginErrorException("KAKAO Login $it")
                                )
                            }
                        }
                    }
                } else {
                    dialogDataHolder.apply {
                        data = DialogData.Builder()
                            .title(context.getString(R.string.login_after_install_kakao))
                            .build()
                    }.run { show = true }
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
                    "https://accounts.google.com/o/oauth2/v2/auth".toUri(), // auth endpoint
                    "https://oauth2.googleapis.com/token".toUri()            // token endpoint
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
        if (!isOnboarding) {
            LoginButton(
                icon = painterResource(R.drawable.icn_pencil),
                text = stringResource(R.string.login_non_member),
                backgroundColor = colorResource(R.color.white),
                onClick = {
                    viewModel.handleContract(LoginAction.ClickNonMember)
                },
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        // 이용약관 및 개인정보 처리방침
        LoginDescriptionText(
            text = stringResource(R.string.login_agreement),
            modifier = Modifier
                .padding(top = 12.dp, bottom = 50.dp)
                .fillMaxWidth(),
            termsOfUse = {
                viewModel.handleContract(LoginAction.ClickTermsOfUse)
            },
            privacyPolicy = {
                viewModel.handleContract(LoginAction.ClickPrivacyPolicy)
            }
        )
    }

}

@Composable
private fun LoginOnBoardingTopSection(
    popBackStack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 13.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Image(
            painter = painterResource(R.drawable.icn_exit),
            contentDescription = null,
            modifier = Modifier.noEffectClickable {
                popBackStack()
            })
    }
}

@Composable
private fun LoginDescriptionText(
    text: String,
    termsOfUse: () -> Unit,
    privacyPolicy: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val underLine = FillsaTheme.typography.subtitle2

    val annotatedString by remember {
        mutableStateOf(
            buildAnnotatedString {
                val termsStart = text.indexOf("이용약관")
                val privacyStart = text.indexOf("개인정보")
                val boldStyle = SpanStyle(
                    textDecoration = TextDecoration.Underline,
                    fontSize = underLine.fontSize,
                    fontFamily = underLine.fontFamily,
                    fontStyle = underLine.fontStyle,
                    fontWeight = underLine.fontWeight
                )

                append(text)
                addStyle(
                    boldStyle,
                    start = termsStart,
                    end = termsStart + 4
                )
                addStyle(
                    boldStyle,
                    start = privacyStart,
                    end = privacyStart + 9
                )

                addLink(
                    LinkAnnotation.Clickable(
                        tag = "TermsOfUseClickTag",
                        styles = TextLinkStyles(boldStyle.copy()),
                        linkInteractionListener = {
                            termsOfUse()
                        }
                    ),
                    start = termsStart,
                    end = termsStart + 4
                )

                addLink(
                    LinkAnnotation.Clickable(
                        tag = "PrivacyPolicyClickTag",
                        styles = TextLinkStyles(boldStyle.copy()),
                        linkInteractionListener = {
                            privacyPolicy()
                        }
                    ),
                    start = privacyStart,
                    end = privacyStart + 9
                )
            }

        )
    }

    Text(
        annotatedString,
        style = FillsaTheme.typography.body3,
        modifier = modifier,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSecondary
    )


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
        navigate = {},
        popBackStack = {}
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


