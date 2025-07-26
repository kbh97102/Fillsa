package com.arakene.presentation.ui.calendar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.noEffectClickable

@Composable
fun CalendarCountSection(
    likeCount: Int,
    typingCount: Int,
    countOnClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.noEffectClickable {
                countOnClick()
            }
        ) {
            Image(painterResource(R.drawable.icn_note_calendar_purple), contentDescription = null)

            Text(
                typingCount.toString(),
                style = FillsaTheme.typography.body3,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(start = 4.dp)
            )

            Image(
                painterResource(R.drawable.icn_fill_heart),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 20.dp)
                    .size(16.dp)
            )

            Text(
                likeCount.toString(),
                style = FillsaTheme.typography.body3,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun CalendarCountSectionPreview() {
    CalendarCountSection(
        typingCount = 3,
        likeCount = 5,
        countOnClick = {}
    )
}