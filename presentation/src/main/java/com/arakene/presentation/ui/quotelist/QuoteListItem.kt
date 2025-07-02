package com.arakene.presentation.ui.quotelist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arakene.domain.responses.MemberQuotesResponse
import com.arakene.domain.util.YN
import com.arakene.presentation.R
import com.arakene.presentation.ui.common.CustomAsyncImage
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.DayOfWeek

@Composable
fun QuoteListItem(
    data: MemberQuotesResponse,
    modifier: Modifier = Modifier
) {

    val quote by remember(data) {
        mutableStateOf(
            if (data.korQuote.isNullOrEmpty()) {
                data.engQuote ?: ""
            } else {
                data.korQuote ?: ""
            }
        )
    }

    val pagerState = rememberPagerState {
        2
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .clip(MaterialTheme.shapes.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        QuoteListItemHeader(data)

        Box(modifier = Modifier.weight(1f)) {

            CustomAsyncImage(
                imagePath = data.imagePath ?: "",
                modifier = Modifier.fillMaxSize(),
                useDim = true
            )

            Column(
                modifier = Modifier.fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                QuoteListItemPager(
                    modifier = Modifier.weight(1f),
                    quote = quote,
                    memo = data.memo ?: "",
                    pagerState = pagerState
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.wrapContentHeight()
                ) {
                    if (data.memoYN == YN.Y) {
                        QuoteListItemIndicator(
                            pagerState = pagerState
                        )
                    }

                    QuoteListItemBottomSection(
                        hasMemo = data.memoYN == YN.Y,
                        isLike = data.likeYN == YN.Y,
                        modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
                    )
                }
            }
        }
    }

}

@Composable
private fun QuoteListItemIndicator(
    pagerState: PagerState
) {
    Row(
        modifier = Modifier.padding(top = 12.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(2) {
            val color =
                if (pagerState.currentPage == it)
                    colorResource(R.color.yellow02)
                else
                    colorResource(R.color.gray_200)
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .background(color, CircleShape)
                    .size(6.dp)
            )
        }
    }
}

@Composable
private fun QuoteListItemHeader(data: MemberQuotesResponse) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.tertiary,
            )
            .padding(vertical = 12.dp, horizontal = 27.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            data.quoteDate.replace("-", "."),
            style = FillsaTheme.typography.buttonXSmallBold,
            color = colorResource(
                R.color.gray_700
            ),
            maxLines = 1
        )

        Text(
            DayOfWeek.valueOf(data.quoteDayOfWeek).kor,
            style = FillsaTheme.typography.buttonXSmallNormal,
            color = colorResource(R.color.gray_700),
            modifier = Modifier.padding(start = 10.dp),
            maxLines = 1
        )
    }
}

@Composable
@Preview
private fun QuoteListItemPreview() {
    FillsaTheme {
        QuoteListItem(
            data = MemberQuotesResponse()
        )
    }
}