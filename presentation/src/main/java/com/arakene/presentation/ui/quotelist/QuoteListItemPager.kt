package com.arakene.presentation.ui.quotelist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arakene.presentation.ui.theme.FillsaTheme

@Composable
fun QuoteListItemPager(
    quote: String,
    memo: String,
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {


    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .padding(top = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (memo.isEmpty()) {
            PagerItem(quote)
        } else {

            var maxHeight by remember {
                mutableIntStateOf(0)
            }

            val density = LocalDensity.current

            SubcomposeLayout {
                val quoteCompose = subcompose("QuoteCompose") {
                    PagerItem(quote)
                }.first().measure(it)

                val memoCompose = subcompose("MemoCompose") {
                    PagerItem(memo)
                }.first().measure(it)

                maxHeight = maxOf(quoteCompose.height, memoCompose.height)

                layout(0, 0) {

                }
            }


            HorizontalPager(
                pagerState,
                modifier = Modifier.height(with(density) { maxHeight.toDp() })
            ) {
                when (it) {
                    0 -> {
                        PagerItem(quote)
                    }

                    else -> {
                        PagerItem(memo)
                    }
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
        modifier = modifier.fillMaxSize(), textAlign = TextAlign.Center, overflow = TextOverflow.Ellipsis
    )
}