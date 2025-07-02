package com.arakene.presentation.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.arakene.presentation.R


@Composable
fun IndicatorImageSection(
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {

    HorizontalPager(
        state = pagerState,
        modifier = modifier
    ) {

        Image(
            modifier = Modifier.fillMaxSize(),
            painter =
                when (it) {
                    0 -> painterResource(R.drawable.img_guide_1)
                    1 -> painterResource(R.drawable.img_guide_2)
                    else -> painterResource(R.drawable.img_guide_3)
                },
            contentDescription = null
        )


    }

}