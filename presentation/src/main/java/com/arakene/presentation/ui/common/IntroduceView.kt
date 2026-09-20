package com.arakene.presentation.ui.common

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.IsDarkMode
import com.arakene.presentation.util.Navigate
import com.arakene.presentation.util.Screens
import com.arakene.presentation.util.getBackgroundColor
import com.arakene.presentation.util.noEffectClickable
import kotlinx.coroutines.launch

@Composable
fun IntroduceView(
    navigate: Navigate,
    modifier: Modifier = Modifier,
    initialPage: Int = 0,
) {

    val pagerState = rememberPagerState(initialPage = initialPage) { 3 }

    val isLastPage by remember(pagerState.currentPage) {
        mutableStateOf(pagerState.currentPage == pagerState.pageCount - 1)
    }

    BackHandler {
        navigate(Screens.Home())
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(getBackgroundColor())
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.height(6.dp))

            // Skip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(R.drawable.icn_arrow),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 15.dp)
                        .size(32.dp)
                        .noEffectClickable {
                            navigate(Screens.Home())
                        },
                    colorFilter = ColorFilter.tint(color = FillsaTheme.colorScheme.onBackground1)
                )

                Text(
                    stringResource(R.string.skip),
                    style = FillsaTheme.typography.body3,
                    color = FillsaTheme.colorScheme.onBackground1,
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

                Spacer(
                    modifier = Modifier.height(
                        if (pagerState.currentPage == 1) 40.dp else 35.dp
                    )
                )

                // Images
                IndicatorImageSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(430.dp),
                    pagerState = pagerState
                )
            }
        }

        // Button
        OkButton(
            isLastPage = isLastPage,
            navigate = navigate,
            scrollTo = {
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp)
        )
    }

}

@Composable
private fun OkButton(
    isLastPage: Boolean,
    scrollTo: suspend () -> Unit,
    navigate: Navigate,
    modifier: Modifier = Modifier,
    darkMode: Boolean = IsDarkMode.current
) {

    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 5.dp)
            .height(50.dp)
            .background(
                color = when (darkMode) {
                    true -> if (isLastPage) {
                        colorResource(R.color.purple01)
                    } else {
                        colorResource(R.color.purple02)
                    }

                    else -> if (isLastPage) {
                        colorResource(R.color.purple01)
                    } else {
                        colorResource(R.color.gray_700)
                    }
                },
                shape = MaterialTheme.shapes.small
            )
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
            color = when (darkMode) {
                true -> if (isLastPage) Color.White else colorResource(R.color.gray_700)
                else -> Color.White
            }
        )
    }
}

@Preview(
    name = "Onboarding guide 1 dark",
    widthDp = 360,
    heightDp = 672,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    locale = "ko",
    fontScale = 1f
)
@Composable
private fun IntroducePage1DarkPreview() {
    IntroduceDarkPreview(page = 0)
}

@Preview(
    name = "Onboarding guide 2 dark",
    widthDp = 360,
    heightDp = 672,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    locale = "ko",
    fontScale = 1f
)
@Composable
private fun IntroducePage2DarkPreview() {
    IntroduceDarkPreview(page = 1)
}

@Preview(
    name = "Onboarding guide 3 dark",
    widthDp = 360,
    heightDp = 672,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    locale = "ko",
    fontScale = 1f
)
@Composable
private fun IntroducePage3DarkPreview() {
    IntroduceDarkPreview(page = 2)
}

@Composable
private fun IntroduceDarkPreview(page: Int) {
    FillsaTheme(darkTheme = true) {
        CompositionLocalProvider(IsDarkMode provides true) {
            IntroduceView(
                navigate = {},
                initialPage = page
            )
        }
    }
}
