package com.arakene.presentation.ui.quotelist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arakene.presentation.R
import com.arakene.presentation.ui.common.AuthorText
import com.arakene.presentation.ui.theme.FillsaTheme

@Composable
fun MemoQuoteSection(
    author: String,
    quote: String,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Color.White, shape = MaterialTheme.shapes.medium)
            .border(
                1.dp,
                color = MaterialTheme.colorScheme.tertiary,
                shape = MaterialTheme.shapes.medium
            )
            .padding(horizontal = 10.dp, vertical = 20.dp)
    ) {
        Text(
            quote,
            style = FillsaTheme.typography.body2,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = colorResource(R.color.gray_700)
        )

        AuthorText(
            author = author,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
        )
    }

}


@Composable
@Preview
private fun MemoQuoteSectionPreview() {
    MemoQuoteSection(
        author = "존 우든",
        quote = "상황을 가장 잘 활용하는 사람이 가장 좋은 상황을 맞는다."
    )
}