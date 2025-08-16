package com.arakene.presentation.util

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun String.toLocalDate(): LocalDate {
    // 날짜 파싱을 위한 포맷터
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    return try {
        // 문자열을 LocalDate로 파싱
        LocalDate.parse(this, formatter)
    } catch (e: DateTimeParseException) {
        LocalDate.now()
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