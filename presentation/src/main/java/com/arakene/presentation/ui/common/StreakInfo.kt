package com.arakene.presentation.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.arakene.domain.responses.MemberStreakResponse
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.IsDarkMode
import com.arakene.presentation.util.StreakProvider
import com.arakene.presentation.util.noEffectClickable
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreakInfo(
    moveToCalendar: () -> Unit,
    modifier: Modifier = Modifier,
    streak: MemberStreakResponse? = StreakProvider.current,
    darkMode: Boolean = IsDarkMode.current
) {

    val streakCount by remember {
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

        val state = rememberTooltipState(
            isPersistent = true
        )
        val scope = rememberCoroutineScope()

        TooltipBox(
            positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
            state = state,
            tooltip = {
                RichTooltip(
                    shape = RoundedCornerShape(4.dp),
                    caretSize = DpSize.Unspecified,
                    colors = TooltipDefaults.richTooltipColors(
                        containerColor = if (darkMode) colorResource(R.color.primary) else colorResource(
                            R.color.gray_700
                        ),
                        contentColor = FillsaTheme.colorScheme.backgroundContainer
                    ),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 16.dp),
                    ) {
                        Column {
                            Text(
                                "연속 필사를 완료해주세요.",
                                style = FillsaTheme.typography.subtitle2,
                                color = FillsaTheme.colorScheme.backgroundContainer
                            )
                            Spacer(Modifier.height(5.dp))
                            Text(
                                "나의 필사현황 보기",
                                style = FillsaTheme.typography.body4,
                                color = FillsaTheme.colorScheme.tertiary,
                                modifier = Modifier.noEffectClickable {
                                    state.dismiss()
                                    moveToCalendar()
                                }
                            )
                        }

                        Spacer(Modifier.width(30.dp))

                        Image(
                            painterResource(R.drawable.icn_exit_white),
                            contentDescription = null,
                            modifier = Modifier.noEffectClickable{
                                state.dismiss()
                            }
                        )
                    }
                }
            },
            content = {
                Image(
                    painterResource(R.drawable.icn_today_complete), contentDescription = null,
                    modifier = Modifier.noEffectClickable {
                        scope.launch {
                            state.show()
                        }
                    })
            }
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
        moveToCalendar = {}
    )
}