package com.arakene.presentation.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme

@Composable
fun IntroduceView(modifier: Modifier = Modifier) {

    val pagerState = rememberPagerState { 3 }

    Column {

        // Skip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(painter = painterResource(R.drawable.icn_arrow), contentDescription = null)

            Text(
                stringResource(R.string.skip),
                style = FillsaTheme.typography.body3,
                color = colorResource(R.color.gray_500),
                textDecoration = TextDecoration.Underline
            )
        }

        // Indicator
        IntroduceIndicatorSection(
            currentPage = pagerState.currentPage
        )

        // Images
        IndicatorImageSection(pagerState)

        // Button


    }

}

@Preview
@Composable
private fun Preview() {
    IntroduceView()
}