package com.arakene.presentation.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.Navigate
import com.arakene.presentation.util.Screens
import com.arakene.presentation.util.noEffectClickable
import kotlinx.coroutines.launch

@Composable
fun IntroduceView(
    navigate: Navigate,
    modifier: Modifier = Modifier
) {

    val pagerState = rememberPagerState { 3 }

    val scope = rememberCoroutineScope()

    Column(modifier = modifier) {

        // Skip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(R.drawable.icn_arrow),
                contentDescription = null,
                modifier = Modifier.padding(start = 15.dp)
            )

            Text(
                stringResource(R.string.skip),
                style = FillsaTheme.typography.body3,
                color = colorResource(R.color.gray_500),
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.padding(end = 20.dp)
            )
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            // Indicator
            IntroduceIndicatorSection(
                currentPage = pagerState.currentPage
            )

            // Images
            IndicatorImageSection(
                modifier = Modifier
                    .weight(1f),
                pagerState = pagerState
            )

            // Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp)
                    .padding(bottom = 30.dp)
                    .noEffectClickable {
                        if (pagerState.currentPage < pagerState.pageCount - 1) {
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        } else {
                            navigate(Screens.Home())
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    stringResource(R.string.next),
                    style = FillsaTheme.typography.buttonMediumBold,
                    color = Color.White
                )
            }
        }


    }

}

@Preview
@Composable
private fun Preview() {
    IntroduceView(
        navigate = {}
    )
}