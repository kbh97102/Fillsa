package com.arakene.presentation.ui.quotelist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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
fun MemoInsertSection(
    memo: String,
    modifier: Modifier = Modifier
) {

    Column(modifier = modifier) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(painterResource(R.drawable.icn_memo), contentDescription = null)
            Text(
                stringResource(R.string.memo),
                style = FillsaTheme.typography.body2,
                color = colorResource(R.color.gray_700),
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .aspectRatio(320f / 458f)
                .background(color = Color.White, shape = MaterialTheme.shapes.medium)
                .border(
                    1.dp,
                    color = MaterialTheme.colorScheme.tertiary,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(10.dp)
        ) {
            if (memo.isEmpty()) {
                Text(
                    stringResource(R.string.insert_memo),
                    style = FillsaTheme.typography.body3,
                    color = colorResource(R.color.gray_ba)
                )
            } else {
                Text(
                    memo,
                    style = FillsaTheme.typography.body3,
                    color = colorResource(R.color.gray_700)
                )
            }
        }

    }

}

@Preview(showBackground = true)
@Composable
private fun MemoInsertSectionPreview() {
    FillsaTheme {
        MemoInsertSection(
            memo = "memo"
        )
    }
}