package com.arakene.presentation.ui.quotelist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arakene.domain.responses.MemberQuotesResponse
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme

@Composable
fun QuoteListItem(
    data: MemberQuotesResponse,
    modifier: Modifier = Modifier
) {

    Column(
        modifier.clip(MaterialTheme.shapes.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

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
                "2025.03.25",
                style = FillsaTheme.typography.buttonXSmallBold,
                color = colorResource(
                    R.color.gray_700
                )
            )

            Text(
                "(수)",
                style = FillsaTheme.typography.buttonXSmallNormal,
                color = colorResource(R.color.gray_700),
                modifier = Modifier.padding(start = 10.dp)
            )
        }

        Box {
            Image(
                painter = painterResource(R.drawable.icn_quote_list_item_background),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                QuoteListItemPager(
                    quote = "상황을 가장 잘 활용하는 사람이 가장 좋은 상황을 맞는다.\n" +
                            "필사 문장이 들어가용",
                    memo = "메모ㅔ몸메모메모메ㅗ메ㅗㅔ모ㅔ모ㅔ모ㅔ모ㅔ모ㅔ모ㅔ모ㅔㅗ모"
                )

                QuoteListItemBottomSection(
                    modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
                )
            }
        }
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