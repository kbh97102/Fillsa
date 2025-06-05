package com.arakene.presentation.ui.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.dropShadow

@Composable
fun MyPageLoginSection(
    isLogged: Boolean,
    modifier: Modifier = Modifier
) {

    if (isLogged) {

        Row(
            modifier = modifier
                .dropShadow(
                    shape = MaterialTheme.shapes.medium,
                    blur = 16.dp,
                    spread = (-3).dp,
                    color = colorResource(R.color.gray_cb).copy(alpha = 0.7f)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White, shape = MaterialTheme.shapes.medium)
                    .padding(horizontal = 20.dp, vertical = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(R.drawable.img_mypage_non_login_default_image),
                    contentDescription = null,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(50.dp),
                )

                Text(
                    "닉네임 or 연동된 계정 이름?",
                    style = FillsaTheme.typography.subtitle1,
                    color = colorResource(R.color.gray_700),
                    maxLines = 1,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

        }


    } else {

        Column(
            modifier = modifier
                .dropShadow(
                    shape = MaterialTheme.shapes.medium,
                    blur = 16.dp,
                    spread = (-3).dp,
                    color = colorResource(R.color.gray_cb).copy(alpha = 0.7f)
                ), horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                Modifier
                    .fillMaxWidth()
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                    )
                    .padding(vertical = 18.dp), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(painter = painterResource(R.drawable.icn_book), contentDescription = null)

                Text(
                    stringResource(R.string.use_after_login),
                    style = FillsaTheme.typography.subtitle1,
                    color = colorResource(R.color.gray_700),
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
            }

            // 로그인 버튼
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        colorResource(R.color.purple_5e),
                        shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                    )
                    .padding(vertical = 15.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.login),
                    style = FillsaTheme.typography.buttonMediumBold,
                    color = Color.White
                )
            }
        }

    }

}


@Preview(showBackground = true, name = "로그인 X")
@Composable
private fun MyPageLoginSectionPreview() {
    FillsaTheme {
        MyPageLoginSection(
            isLogged = false
        )
    }
}


@Preview(showBackground = true, name = "로그인함")
@Composable
private fun MyPageLoginSectionPreview2() {
    FillsaTheme {
        MyPageLoginSection(
            isLogged = true
        )
    }
}