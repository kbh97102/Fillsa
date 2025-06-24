package com.arakene.presentation.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

    val isLastPage by remember(pagerState.currentPage) {
        mutableStateOf(pagerState.currentPage == pagerState.pageCount - 1)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

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
                modifier = Modifier
                    .padding(start = 15.dp)
                    .noEffectClickable {
                        navigate(Screens.Home())
                    }
            )

            Text(
                stringResource(R.string.skip),
                style = FillsaTheme.typography.body3,
                color = colorResource(R.color.gray_500),
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .padding(end = 20.dp)
                    .noEffectClickable {
                        navigate(Screens.Home())
                    }
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
            OkButton(isLastPage = isLastPage, navigate = navigate, scrollTo = {
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
            })
        }


    }

}

@Composable
private fun OkButton(
    isLastPage: Boolean,
    scrollTo: suspend () -> Unit,
    navigate: Navigate,
    modifier: Modifier = Modifier
) {

    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 30.dp)
            .background(
                color = if (isLastPage) {
                    colorResource(R.color.purple01)
                } else {
                    colorResource(R.color.gray_700)
                },
                shape = MaterialTheme.shapes.small
            )
            .padding(vertical = 15.dp)
            .noEffectClickable {
                if (isLastPage) {
                    navigate(Screens.Home())
                } else {
                    scope.launch { scrollTo() }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isLastPage) {
                stringResource(R.string.start_fillsa)
            } else {
                stringResource(R.string.next)
            },
            style = FillsaTheme.typography.buttonMediumBold,
            color = Color.White
        )
    }
}

@Preview
@Composable
private fun Preview() {
    IntroduceView(
        navigate = {}
    )
}