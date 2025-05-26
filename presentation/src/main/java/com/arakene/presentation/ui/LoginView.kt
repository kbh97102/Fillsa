package com.arakene.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme


@Composable
fun LoginView() {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(R.drawable.icn_login_logo),
            contentDescription = null
        )


        Text(stringResource(R.string.login_description), style = FillsaTheme.typography.body2)

        // 카카오
        Button(
            onClick = {}
        ) { }

        // 구글
        Button(
            onClick = {}
        ) { }

        // 비회원
        Button(
            onClick = {}
        ) { }

        // 이용약관 및 개인정보 처리방침
        Row {
            Button(
                onClick = {}
            ) { }

            Button(
                onClick = {}
            ) { }
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
            .background(color = backgroundColor), verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painter = icon, contentDescription = null)

        Text(text = text, modifier = Modifier)
    }

}

@Preview(showBackground = true)
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


