package com.arakene.presentation.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.IsDarkMode
import com.arakene.presentation.util.noEffectClickable

@Composable
fun CalendarCountSection(
    likeCount: Int,
    typingCount: Int,
    countOnClick: () -> Unit,
    modifier: Modifier = Modifier,
    darkMode: Boolean = IsDarkMode.current,
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
            CalendarFigmaAsset(
                fileName = "calendar_record_heart.svg",
                modifier = Modifier.size(16.dp),
                darkMode = darkMode,
            )

            Text(
                likeCount.toString(),
                style = FillsaTheme.typography.body3,
                color = FillsaTheme.colorScheme.onBackground1,
                modifier = Modifier.padding(start = 4.dp)
            )

            CalendarFigmaAsset(
                fileName = "calendar_record_fire.svg",
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(16.dp),
                darkMode = darkMode,
            )

            Text(
                typingCount.toString(),
                style = FillsaTheme.typography.body3,
                color = FillsaTheme.colorScheme.onBackground1,
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
