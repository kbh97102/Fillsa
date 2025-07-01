package com.arakene.presentation.ui.quotelist

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme

@Composable
fun QuoteListEmptySection(
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Image(painter = painterResource(R.drawable.icn_list_empty), contentDescription = null)

        Text(
            stringResource(R.string.list_empty),
            color = colorResource(R.color.purple01),
            style = FillsaTheme.typography.subtitle1,
            modifier = Modifier.padding(top = 20.dp)
        )
        Text(
            stringResource(R.string.list_empty_description),
            color = colorResource(R.color.gray_700),
            style = FillsaTheme.typography.body2,
            modifier = Modifier.padding(top = 8.dp)
        )


    }

}

@Preview
@Composable
private fun QuoteListEmptySectionPreview() {
    FillsaTheme {
        QuoteListEmptySection()
    }
}