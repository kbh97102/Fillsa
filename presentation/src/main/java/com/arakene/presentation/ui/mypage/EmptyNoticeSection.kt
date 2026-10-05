package com.arakene.presentation.ui.mypage

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme

@Composable
fun EmptyNoticeSection(modifier: Modifier = Modifier) {

    Column(
        modifier = modifier.fillMaxSize()
            .background(FillsaTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {


        Column(
            modifier = Modifier.offset(y = (-58).dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(painter = painterResource(R.drawable.icn_empty_notice), contentDescription = null)
            Text(
                stringResource(R.string.empty_notice),
                style = FillsaTheme.typography.heading4,
                color = FillsaTheme.colorScheme.onBackground1,
                modifier = Modifier.padding(top = 24.dp)
            )
        }
    }

}

@Preview(name = "Notice empty light", widthDp = 360, heightDp = 585, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
private fun EmptyNoticeLightPreview() {
    FillsaTheme(darkTheme = false) { EmptyNoticeSection() }
}

@Preview(name = "Notice empty dark", widthDp = 360, heightDp = 585, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
private fun EmptyNoticeDarkPreview() {
    FillsaTheme(darkTheme = true) { EmptyNoticeSection() }
}
