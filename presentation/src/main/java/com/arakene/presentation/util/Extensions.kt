package com.arakene.presentation.util

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import com.arakene.presentation.ui.theme.FillsaTypo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

suspend fun SnackbarHostState.showCustomSnackbar(
    message: String,
    actionLabel: String? = null,
    withDismissAction: Boolean = false,
    duration: SnackbarDuration = SnackbarDuration.Short,
    displayIcon: Boolean = true
) {
    this.showSnackbar(
        CustomSnackbarVisuals(
            message = message,
            actionLabel = actionLabel,
            withDismissAction = withDismissAction,
            duration = duration,
            displayIcon = displayIcon
        )
    )
}


fun String.toLocalDate(defaultValue: LocalDate = LocalDate.now()): LocalDate {
    // 날짜 파싱을 위한 포맷터
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    return try {
        // 문자열을 LocalDate로 파싱
        LocalDate.parse(this, formatter)
    } catch (e: DateTimeParseException) {
        defaultValue
    }
}

fun <T> Flow<T>.throttleFirst(windowDuration: Long): Flow<T> = channelFlow {
    var lastEmissionTime = 0L
    collect {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastEmissionTime >= windowDuration) {
            lastEmissionTime = currentTime
            send(it)
        }
    }
}

@Composable
fun DoubleBackPressHandler(
    onExit: () -> Unit = {},
    exitMessage: String = "한 번 더 뒤로가기를 누르면 앱이 종료됩니다.",
    timeWindow: Long = 2000L
) {
    var backPressedTime by remember { mutableLongStateOf(0) }
    val context = LocalContext.current

    BackHandler {
        val currentTime = System.currentTimeMillis()

        if (currentTime - backPressedTime < timeWindow) {
            onExit()
        } else {
            backPressedTime = currentTime
            Toast.makeText(context, exitMessage, Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun FillsaTypo.getStyle(style: TypographyEnum): TextStyle {
    return when (style) {
        TypographyEnum.Heading1 -> heading1
        TypographyEnum.Heading2 -> heading2
        TypographyEnum.Heading3 -> heading3
        TypographyEnum.Heading4 -> heading4
        TypographyEnum.Subtitle1 -> subtitle1
        TypographyEnum.Subtitle2 -> subtitle2
        TypographyEnum.Body1 -> body1
        TypographyEnum.Body2 -> body2
        TypographyEnum.Body3 -> body3
        TypographyEnum.Body4 -> body4
        TypographyEnum.ButtonLargeBold -> buttonLargeBold
        TypographyEnum.ButtonLargeNormal -> buttonLargeNormal
        TypographyEnum.ButtonMediumBold -> buttonMediumBold
        TypographyEnum.ButtonMediumNormal -> buttonMediumNormal
        TypographyEnum.ButtonSmallBold -> buttonSmallBold
        TypographyEnum.ButtonSmallNormal -> buttonSmallNormal
        TypographyEnum.ButtonXSmallBold -> buttonXSmallBold
        TypographyEnum.ButtonXSmallNormal -> buttonXSmallNormal
        TypographyEnum.Quote -> quote
    }
}