package com.arakene.presentation.ui.quotelist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arakene.domain.responses.DailyQuoteDto
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme

@Composable
fun QuoteListItem(
    data: DailyQuoteDto,
    modifier: Modifier = Modifier
) {

    Column(modifier) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.tertiary,
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
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

        QuoteListItemPager(
            quote = "상황을 가장 잘 활용하는 사람이 가장 좋은 상황을 맞는다.\n" +
                    "필사 문장이 들어가용",
            memo = "메모ㅔ몸메모메모메ㅗ메ㅗㅔ모ㅔ모ㅔ모ㅔ모ㅔ모ㅔ모ㅔ모ㅔㅗ모"
        )

//        Box {
//            Image(
//                painter = painterResource(R.drawable.icn_quote_list_item_background),
//                contentDescription = null,
//                contentScale = ContentScale.Crop,
//                modifier = Modifier.fillMaxSize()
//            )
//        }

//        HorizontalPager() { }
//
//        Indicator
//
//        Row {  }
    }

}

@Composable
@Preview
private fun QuoteListItemPreview() {
    FillsaTheme {
        QuoteListItem(
            data = DailyQuoteDto()
        )
    }
}