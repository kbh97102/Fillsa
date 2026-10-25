package com.arakene.presentation.ui.home

import android.content.res.Configuration
import android.view.WindowManager
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.TypingResultKind

@Composable
internal fun TypingResultDialog(
    kind: TypingResultKind,
    title: String,
    darkMode: Boolean,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = false,
            dismissOnBackPress = kind == TypingResultKind.Incomplete,
        ),
    ) {
        val window = (LocalView.current.parent as? DialogWindowProvider)?.window
        SideEffect {
            window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            window?.setDimAmount(if (darkMode) 0.485f else 0.694f)
        }
        TypingResultContent(kind, title, darkMode, onConfirm, onCancel)
    }
}

@Composable
internal fun TypingResultContent(
    kind: TypingResultKind,
    title: String,
    darkMode: Boolean,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    val shape = RoundedCornerShape(8.dp)
    val outline = colorResource(R.color.gray_500)
    val textColor = if (darkMode) Color.White else colorResource(R.color.gray_700)
    Column(
        modifier = Modifier
            .width(320.dp)
            .height(if (darkMode) 245.dp else 243.dp)
            .then(if (darkMode) Modifier else Modifier.shadow(12.dp, shape))
            .clip(shape)
            .background(if (darkMode) colorResource(R.color.gray_600) else Color.White)
            .then(if (darkMode) Modifier.border(1.dp, outline, shape) else Modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(if (darkMode) 21.dp else 20.dp))
        TypingResultCharacter(kind, darkMode)
        Box(Modifier.fillMaxWidth().height(30.dp), contentAlignment = Alignment.Center) {
            Text(
                text = title,
                style = FillsaTheme.typography.heading4,
                color = textColor,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }
        Box(Modifier.fillMaxWidth().height(73.dp), contentAlignment = Alignment.TopCenter) {
            Row(modifier = Modifier.offset(y = 12.dp)) {
                if (kind == TypingResultKind.Incomplete) {
                    TypingResultButton("취소", darkMode, primary = false, width = 142, onClick = onCancel)
                    Spacer(Modifier.width(12.dp))
                    TypingResultButton("필사하기", darkMode, primary = true, width = 142, onClick = onConfirm)
                } else {
                    TypingResultButton("확인", darkMode, primary = true, width = 296, onClick = onConfirm)
                }
            }
        }
    }
}

@Composable
private fun TypingResultButton(
    label: String,
    darkMode: Boolean,
    primary: Boolean,
    width: Int,
    onClick: () -> Unit,
) {
    val purple = colorResource(R.color.purple01)
    val ink = colorResource(R.color.gray_700)
    val background = when {
        !primary -> Color.White
        darkMode -> purple
        else -> colorResource(R.color.yellow02)
    }
    val foreground = when {
        !primary && darkMode -> purple
        !primary -> ink
        darkMode -> Color.White
        else -> ink
    }
    val shape = RoundedCornerShape(8.dp)
    Box(
        modifier = Modifier
            .width(width.dp)
            .height(49.dp)
            .clip(shape)
            .background(background)
            .then(if (primary) Modifier else Modifier.border(1.dp, foreground, shape))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, style = FillsaTheme.typography.buttonMediumBold, color = foreground)
    }
}

@Composable
private fun TypingResultCharacter(kind: TypingResultKind, darkMode: Boolean) {
    Box(Modifier.size(120.dp)) {
        when (kind) {
            TypingResultKind.Continue -> TypingResultImage(
                if (darkMode) R.drawable.typing_result_dark_continue_character_light_state_handwriting
                else R.drawable.typing_result_light_continue_character_light_state_handwriting,
                Modifier.size(120.dp),
            )
            TypingResultKind.Incomplete -> TypingResultImage(
                if (darkMode) R.drawable.typing_result_dark_incomplete_character_light_state_basic
                else R.drawable.typing_result_light_incomplete_character_light_state_basic,
                Modifier.size(120.dp),
            )
            TypingResultKind.Streak -> {
                if (darkMode) {
                    TypingResultImage(R.drawable.typing_result_dark_streak_union, Modifier.offset(28.65.dp, 29.04.dp).size(75.57.dp, 80.71.dp))
                } else {
                    TypingResultImage(R.drawable.typing_result_light_streak_body, Modifier.offset(28.65.dp, 29.04.dp).size(61.57.dp, 80.71.dp))
                    TypingResultImage(R.drawable.typing_result_light_streak_arm_wave, Modifier.offset(77.37.dp, 56.84.dp).size(26.86.dp, 30.76.dp))
                }
                Box(Modifier.offset(15.78.dp, 17.49.dp).size(11.71.dp, 10.49.dp), contentAlignment = Alignment.Center) {
                    TypingResultImage(
                        if (darkMode) R.drawable.typing_result_dark_streak_wave_lines else R.drawable.typing_result_light_streak_wave_lines,
                        Modifier.size(7.42.dp, 9.63.dp).rotate(-112f),
                    )
                }
                TypingResultImage(
                    if (darkMode) R.drawable.typing_result_dark_streak_face else R.drawable.typing_result_light_streak_face,
                    Modifier.offset(50.84.dp, 72.32.dp).size(22.11.dp, 4.42.dp),
                )
                TypingResultImage(
                    if (darkMode) R.drawable.typing_result_dark_streak_mouth_stroke else R.drawable.typing_result_light_streak_mouth_stroke,
                    Modifier.offset(58.29.dp, 80.13.dp).size(8.05.dp, 2.84.dp),
                )
                TypingResultImage(
                    if (darkMode) R.drawable.typing_result_dark_streak_vector else R.drawable.typing_result_light_streak_vector,
                    Modifier.offset(87.dp, 28.4.dp).size(22.67.dp, 25.93.dp),
                )
                Box(Modifier.offset(24.65.dp, 12.8.dp).size(26.25.dp, 27.89.dp), contentAlignment = Alignment.Center) {
                    TypingResultImage(
                        if (darkMode) R.drawable.typing_result_dark_streak_floating_quote else R.drawable.typing_result_light_streak_floating_quote,
                        Modifier.size(18.dp, 22.dp).rotate(28.15f),
                    )
                }
            }
        }
    }
}

@Composable
private fun TypingResultImage(@DrawableRes drawable: Int, modifier: Modifier) {
    Image(
        painter = painterResource(drawable),
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
        modifier = modifier,
    )
}

@Preview(widthDp = 320, heightDp = 243, locale = "ko", fontScale = 1f, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable private fun TypingContinueLightPreview() = FillsaTheme(darkTheme = false) {
    TypingResultContent(TypingResultKind.Continue, "오늘도 이어서 필사를 해주세요!", false, {}, {})
}

@Preview(widthDp = 320, heightDp = 243, locale = "ko", fontScale = 1f, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable private fun TypingStreakLightPreview() = FillsaTheme(darkTheme = false) {
    TypingResultContent(TypingResultKind.Streak, "연속 필사 완료!", false, {}, {})
}

@Preview(widthDp = 320, heightDp = 243, locale = "ko", fontScale = 1f, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable private fun TypingIncompleteLightPreview() = FillsaTheme(darkTheme = false) {
    TypingResultContent(TypingResultKind.Incomplete, "필사가 완료되지 않았어요.", false, {}, {})
}

@Preview(widthDp = 320, heightDp = 245, locale = "ko", fontScale = 1f, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable private fun TypingContinueDarkPreview() = FillsaTheme(darkTheme = true) {
    TypingResultContent(TypingResultKind.Continue, "오늘도 이어서 필사를 해주세요!", true, {}, {})
}

@Preview(widthDp = 320, heightDp = 245, locale = "ko", fontScale = 1f, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable private fun TypingStreakDarkPreview() = FillsaTheme(darkTheme = true) {
    TypingResultContent(TypingResultKind.Streak, "연속 필사 완료!", true, {}, {})
}

@Preview(widthDp = 320, heightDp = 245, locale = "ko", fontScale = 1f, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable private fun TypingIncompleteDarkPreview() = FillsaTheme(darkTheme = true) {
    TypingResultContent(TypingResultKind.Incomplete, "필사가 완료되지 않았어요.", true, {}, {})
}
