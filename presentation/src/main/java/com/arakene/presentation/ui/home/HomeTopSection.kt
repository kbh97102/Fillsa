package com.arakene.presentation.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
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
import kotlin.math.roundToInt

@Composable
fun HomeTopSection(
    navigate: Navigate,
    modifier: Modifier = Modifier,
    darkMode: Boolean = IsDarkMode.current,
    streak: MemberStreakResponse? = StreakProvider.current,
) {


    var displayPopUp by remember {
        mutableStateOf(false)
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

        val density = LocalDensity.current
        val padding = with(density) { 8.dp.roundToPx() }

        SubcomposeLayout {
            val streakInfoPlaceable = subcompose("STREAK_INFO") {
                StreakInfo(
                    displayPopUp = {
                        displayPopUp = true
                    }
                )
            }.single().measure(it)

            val popupPlaceable = subcompose("POPUP_STREAK_INFO") {
                if (displayPopUp) {
                    // 2) 팝업 툴팁
                    Popup(
                        alignment = Alignment.TopEnd,
                        onDismissRequest = {
                            displayPopUp = false
                        },
                        offset = IntOffset(
                            -(streakInfoPlaceable.width.toFloat() / 2).roundToInt(),
                            streakInfoPlaceable.height
                        )
                    ) {
                        BubbleTooltip(
                            onClickAction = {
                                displayPopUp = false
                                navigate.invoke(Screens.Calendar)
                            },
                            onDismiss = {
                                displayPopUp = false
                            }
                        )
                    }
                }
            }.firstOrNull()?.measure(it)

            val myPagePlaceable = subcompose("MY_PAGE") {
                Image(
                    painterResource(R.drawable.icn_my_page),
                    contentDescription = null,
                    modifier = Modifier.noEffectClickable {
                        navigate(Screens.MyPage)
                    },
                    colorFilter = ColorFilter.tint(FillsaTheme.colorScheme.onBackground1)
                )
            }.single().measure(it)

            val streakInfoXPos =
                it.maxWidth - myPagePlaceable.width - streakInfoPlaceable.width - padding
            val popUpXPos =
                streakInfoXPos

            layout(it.maxWidth, myPagePlaceable.height) {
                myPagePlaceable.let { myPage ->
                    myPage.placeRelative(x = it.maxWidth - myPage.width, y = 0)
                }
                streakInfoPlaceable.placeRelative(
                    x = streakInfoXPos,
                    y = 0
                )
                popupPlaceable?.placeRelative(
                    x = popUpXPos, y = 0, zIndex = 1f
                )
            }
        }
    }

}

@Composable
fun BubbleTooltip(
    onClickAction: () -> Unit,
    onDismiss: () -> Unit,
    darkMode: Boolean = IsDarkMode.current
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
                .background(
                    if (darkMode) {
                        colorResource(R.color.primary)
                    } else {
                        colorResource(R.color.black)
                    }, shape = RoundedCornerShape(10.dp)
                )
                .padding(16.dp)
                .align(Alignment.End)
        ) {
            Text(
                text = "연속 필사를 완료해 주세요!",
                color = if (darkMode) {
                    colorResource(R.color.gray_700)
                } else {
                    Color.White
                },
                style = FillsaTheme.typography.subtitle2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "나의 필사현황 보기",
                style = FillsaTheme.typography.body4,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.noEffectClickable { onClickAction() },
                color = if (darkMode) {
                    FillsaTheme.colorScheme.onTertiary2
                } else {
                    FillsaTheme.colorScheme.tertiary
                }
            )
        }
    }
}

@Composable
fun TriangleArrow(
    darkMode: Boolean = IsDarkMode.current
) {
    val primary = colorResource(R.color.primary)
    val black = colorResource(R.color.black)

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
        drawPath(
            path, if (darkMode) {
                primary
            } else {
                black
            }
        )
    }
}

@Preview
@Composable
private fun Preview() {
    BubbleTooltip(onClickAction = {}, onDismiss = {})
}

@Preview(showBackground = true, widthDp = 400)
@Composable
private fun HomeTopSectionPreview() {
    HomeTopSection(
        navigate = {},
    )
}