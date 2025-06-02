package com.arakene.presentation.ui.quotelist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arakene.presentation.ui.theme.FillsaTheme

@Composable
fun QuoteListItemPager(
    quote: String,
    memo: String,
    modifier: Modifier = Modifier
) {

    if (memo.isEmpty()) {
        PagerItem(quote)
    } else {

        val pagerState = rememberPagerState {
            2
        }

        Column {
            HorizontalPager(pagerState) {
                when (it) {
                    0 -> {
                        PagerItem(quote)
                    }

                    else -> {
                        PagerItem(memo)
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                repeat(2){
                    val color = if (pagerState.currentPage == it) Color.DarkGray else Color.LightGray
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .background(color, CircleShape)
                            .size(10.dp)
                    )
                }
            }
        }

    }


}


@Composable
private fun PagerItem(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text, style = FillsaTheme.typography.subtitle2, color = Color.White,
        modifier = modifier.fillMaxWidth(), textAlign = TextAlign.Center
    )
}