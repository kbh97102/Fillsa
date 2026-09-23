package com.arakene.presentation.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.arakene.presentation.R


@Composable
fun IndicatorImageSection(
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {

    HorizontalPager(
        state = pagerState,
        modifier = modifier
    ) { page ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier.size(
                    width = if (page == 2) 280.dp else 288.dp,
                    height = 430.dp
                ),
                painter =
                    when (page) {
                        0 -> painterResource(R.drawable.onboarding_guide_visual_1)
                        1 -> painterResource(R.drawable.onboarding_guide_visual_2)
                        else -> painterResource(R.drawable.onboarding_guide_visual_3)
                    },
                contentDescription = null,
                contentScale = ContentScale.Fit
            )
        }
    }
}
