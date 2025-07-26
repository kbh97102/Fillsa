package com.arakene.presentation.ui.quotelist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DateSelectSection(
    startDate: LocalDate,
    endDate: LocalDate,
    modifier: Modifier = Modifier,
) {

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")

    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.secondary,
                shape = MaterialTheme.shapes.small
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {

        Image(painterResource(R.drawable.icn_calendar), contentDescription = null)

        Text(
            "${dateFormatter.format(startDate)} - ${dateFormatter.format(endDate)}",
            style = FillsaTheme.typography.body2,
            color = colorResource(R.color.gray_700),
            modifier = Modifier.padding(start = 10.dp)
        )

        Spacer(Modifier.weight(1f))

        Image(painterResource(R.drawable.icn_arrow_down_black), contentDescription = null)

    }

}


@Composable
@Preview
private fun DateSelectSectionPreview() {
    FillsaTheme {
        DateSelectSection(
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(10)
        )
    }
}