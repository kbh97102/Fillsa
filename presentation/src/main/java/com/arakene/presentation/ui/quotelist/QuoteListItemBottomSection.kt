package com.arakene.presentation.ui.quotelist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme

@Composable
fun QuoteListItemBottomSection(
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {

        Row(
            modifier = Modifier
                .background(
                    color = Color.White.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(100.dp)
                )
                .padding(horizontal = 14.dp, vertical = 1.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(painterResource(R.drawable.icn_memo), contentDescription = null)

            Text(
                stringResource(R.string.memo),
                style = FillsaTheme.typography.body4,
                color = colorResource(R.color.gray_700),
                modifier = Modifier.padding(start = 4.dp)
            )

        }

        Row(
            modifier = Modifier
                .background(
                    color = Color.White.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(100.dp)
                )
                .padding(horizontal = 14.dp, vertical = 1.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painterResource(R.drawable.icn_filled_heart),
                contentDescription = null,
                modifier = Modifier.size(12.dp)
            )

            Text(
                stringResource(R.string.like),
                style = FillsaTheme.typography.body4,
                color = colorResource(R.color.gray_700),
                modifier = Modifier.padding(start = 4.dp)
            )

        }

    }


}

@Composable
@Preview
private fun QuoteListItemBottomSectionPreview() {
    QuoteListItemBottomSection()
}