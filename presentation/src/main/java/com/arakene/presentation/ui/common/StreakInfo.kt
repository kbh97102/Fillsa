package com.arakene.presentation.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arakene.domain.responses.MemberStreakResponse
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.IsDarkMode
import com.arakene.presentation.util.StreakProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreakInfo(
    streak: MemberStreakResponse? = StreakProvider.current,
) {

    val streakCount by remember(streak) {
        mutableIntStateOf(streak?.currentStreak ?: 0)
    }

    if (streakCount > 0) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painterResource(R.drawable.icn_today_complete),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Text(
                "${streakCount}일",
                style = FillsaTheme.typography.subtitle1,
                color = FillsaTheme.colorScheme.onBackground1
            )

        }
    } else {
        Image(
            painterResource(R.drawable.icn_empty_daily_count), contentDescription = null,
        )
    }

}

@Preview(showBackground = true)
@Composable
private fun StreakInfoPreview() {
    StreakInfo(
        streak = MemberStreakResponse(
            currentStreak = 0,
            isTodayWritten = false
        ),
    )
}