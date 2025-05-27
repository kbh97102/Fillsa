package com.arakene.presentation.ui

import android.util.Log
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
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.launch


@Composable
fun LoginView() {

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    fun handleSignIn(result: GetCredentialResponse) {
        // Handle the successfully returned credential.
        when (val credential = result.credential) {

            // Passkey credential
            is PublicKeyCredential -> {
                // Share responseJson such as a GetCredentialResponse on your server to
                // validate and authenticate
                credential.authenticationResponseJson.also {
                    Log.e(">>>>", "passkey $it")
                }
            }

            // Password credential
            is PasswordCredential -> {
                // Send ID and password to your server to validate and authenticate.
                val username = credential.id
                val password = credential.password
                Log.e(">>>>", "name $username pw $password")
            }

            // GoogleIdToken credential
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // Use googleIdTokenCredential and extract the ID to validate and
                        // authenticate on your server.
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)
                        // You can use the members of googleIdTokenCredential directly for UX
                        // purposes, but don't use them to store or control access to user
                        // data. For that you first need to validate the token:
                        // pass googleIdTokenCredential.getIdToken() to the backend server.
//                        GoogleIdTokenVerifier verifier = ... // see validation instructions
//                        GoogleIdToken idToken = verifier.verify(idTokenString);
                        // To get a stable account identifier (e.g. for storing user data),
                        // use the subject ID:
//                        idToken.getPayload().getSubject()
                        Log.e(
                            ">>>>",
                            "idToken ${googleIdTokenCredential.idToken} id ${googleIdTokenCredential.id}"
                        )
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(">>>>", "Received an invalid google id token response", e)
                    }
                } else {
                    // Catch any unrecognized custom credential type here.
                    Log.e(">>>>", "Unexpected type of credential")
                }
            }

            else -> {
                // Catch any unrecognized credential type here.
                Log.e(">>>>", "Unexpected type of credential")
            }
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
            onClick = {},
            modifier = Modifier.padding(top = 12.dp)
        )

        // 구글
        LoginButton(
            icon = painterResource(R.drawable.icn_google),
            text = stringResource(R.string.login_google),
            backgroundColor = colorResource(R.color.google_gray),
            modifier = Modifier.padding(top = 16.dp),
            onClick = {

                val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId("")
                    .setAutoSelectEnabled(false)
                    .build()

                val request: GetCredentialRequest = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                scope.launch {
                    try {
                        val result = CredentialManager.create(context).getCredential(
                            request = request,
                            context = context,
                        )
                        handleSignIn(result)
                    } catch (e: Exception) {
//                        handleFailure(e)
                        e.printStackTrace()
                    }
                }

            },
        )

        // 비회원
        LoginButton(
            icon = painterResource(R.drawable.icn_pencil),
            text = stringResource(R.string.login_non_member),
            backgroundColor = colorResource(R.color.white),
            onClick = {},
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
    LoginView()
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


