package com.arakene.presentation.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.arakene.domain.responses.MemberStreakResponse
import com.arakene.presentation.R
import com.arakene.presentation.ui.common.StreakInfo
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.IsDarkMode
import com.arakene.presentation.util.Navigate
import com.arakene.presentation.util.Screens
import com.arakene.presentation.util.StreakProvider
import com.arakene.presentation.util.noEffectClickable

@Composable
fun HomeTopSection(
    navigate: Navigate,
    modifier: Modifier = Modifier,
    darkMode: Boolean = IsDarkMode.current,
    streak: MemberStreakResponse? = StreakProvider.current,
) {

    var popupPosition by remember {
        mutableStateOf(Offset.Zero)
    }

    val streakCount by remember(streak) {
        mutableIntStateOf(streak?.currentStreak ?: 0)
    }

    var displayPopUp by remember(streakCount) {
        mutableStateOf(streakCount <= 0)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            if (darkMode) {
                painterResource(R.drawable.icn_logo_dark)
            } else {
                painterResource(R.drawable.icn_logo)
            },
            contentDescription = null,
            modifier = Modifier.noEffectClickable {
                navigate(Screens.Home())
            })

        Row(verticalAlignment = Alignment.CenterVertically) {

            StreakInfo()

            if (displayPopUp) {
                val density = LocalDensity.current
                // 2) 팝업 툴팁
                Popup(
                    alignment = Alignment.TopEnd,
                    offset = with(density) {
                        IntOffset(
                            popupPosition.x.toInt() + 9.dp.toPx().toInt(), popupPosition.y.toInt()
                        )
                    },
                    onDismissRequest = {
                        displayPopUp = false
                    }
                ) {
                    BubbleTooltip(
                        onClickAction = {
                            navigate.invoke(Screens.Calendar)
                        },
                        onDismiss = {
                            displayPopUp = false
                        }
                    )
                }
            }

            Spacer(Modifier.width(10.dp))

            Image(
                painterResource(R.drawable.icn_my_page),
                contentDescription = null,
                modifier = Modifier.noEffectClickable {
                    navigate(Screens.MyPage)
                },
                colorFilter = ColorFilter.tint(FillsaTheme.colorScheme.onBackground1)
            )
        }
    }

}

@Composable
fun BubbleTooltip(
    onClickAction: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        Modifier
            .wrapContentSize()
            .pointerInput(Unit) { // 팝업 외부 클릭 가능
                detectTapGestures { onDismiss() }
            },
        horizontalAlignment = Alignment.End
    ) {
        Row(modifier = Modifier, horizontalArrangement = Arrangement.End) {
            TriangleArrow()
            Spacer(Modifier.width(20.dp))
        }
        // 말풍선 전체 UI
        Column(
            modifier = Modifier
                .background(Color.Black, shape = RoundedCornerShape(10.dp))
                .padding(16.dp)
                .align(Alignment.End)
        ) {
            Text(
                text = "연속 필사를 완료해 주세요!",
                color = Color.White,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "나의 필사현황 보기",
                color = Color(0xFFFFD966),
                fontSize = 14.sp,
                modifier = Modifier.clickable { onClickAction() }
            )
        }
    }
}

@Composable
fun TriangleArrow() {
    Canvas(
        modifier = Modifier
            .size(width = 20.dp, height = 18.dp)
    ) {
        val path = Path().apply {
            moveTo(size.width / 2f, 0f)
            lineTo(0f, size.height)
            lineTo(size.width, size.height)
            close()
        }
        drawPath(path, Color.Black)
    }
}

@Preview
@Composable
private fun Preview() {
    BubbleTooltip(onClickAction = {}) { }
}

@Preview(showBackground = true, widthDp = 400)
@Composable
private fun HomeTopSectionPreview() {
    HomeTopSection(
        navigate = {},
    )
}