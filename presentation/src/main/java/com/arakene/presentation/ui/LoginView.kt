package com.arakene.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme


@Composable
fun LoginView() {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 20.dp)
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
            onClick = {},
            modifier = Modifier.padding(top = 16.dp)
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


