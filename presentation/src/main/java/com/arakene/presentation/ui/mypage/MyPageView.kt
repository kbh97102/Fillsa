package com.arakene.presentation.ui.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme

@Composable
fun MyPageView(modifier: Modifier = Modifier) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 20.dp)
    ) {
        // LOGO
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp), horizontalArrangement = Arrangement.Center
        ) {
            Image(painterResource(R.drawable.icn_logo), contentDescription = null)
        }

        // Login or UserName
        MyPageLoginSection(
            modifier = Modifier.padding(top = 10.dp),
            isLogged = true
        )

        // Notice
        MyPageItem(
            image = painterResource(R.drawable.icn_info),
            text = stringResource(R.string.notice),
            onClick = {},
            modifier = Modifier.padding(top = 20.dp)
        )

        // Alert
        MyPageItem(
            image = painterResource(R.drawable.icn_bell),
            text = stringResource(R.string.alert),
            onClick = {},
            modifier = Modifier.padding(top = 20.dp)
        )

        // Theme
        MyPageItem(
            image = painterResource(R.drawable.icn_theme),
            text = stringResource(R.string.theme),
            onClick = {},
            modifier = Modifier.padding(top = 20.dp)
        )

        // version
        VersionSection(modifier = modifier.padding(top = 20.dp), isLogged = true, logout = {})

        // logout
    }

}

@Preview
@Composable
private fun MyPageViewPreview() {
    FillsaTheme {
        MyPageView()
    }
}