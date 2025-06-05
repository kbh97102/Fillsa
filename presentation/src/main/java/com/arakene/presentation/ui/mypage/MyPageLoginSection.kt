package com.arakene.presentation.ui.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme

@Composable
fun MyPageLoginSection(
    isLogged: Boolean,
    modifier: Modifier = Modifier
) {

    if (isLogged) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .padding(horizontal = 20.dp, vertical = 15.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.img_mypage_non_login_default_image),
                contentDescription = null,
                modifier = Modifier.clip(CircleShape)
            )

            Text(
                "닉네임 or 연동된 계정 이름?",
                style = FillsaTheme.typography.subtitle1,
                color = colorResource(R.color.gray_700),
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

        }


    } else {

        Column(
            modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp), verticalAlignment = Alignment.CenterVertically
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

        }

    }

}