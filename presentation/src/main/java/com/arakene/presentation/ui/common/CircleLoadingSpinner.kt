package com.arakene.presentation.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.svg.SvgDecoder
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme

@Composable
fun CircleLoadingSpinner(
    isLoading: Boolean
) {
    if (isLoading) {
        val infiniteTransition = rememberInfiniteTransition(label = "global-loading")
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1_000, easing = LinearEasing),
            ),
            label = "global-loading-rotation",
        )
        val context = LocalContext.current
        val model = remember(context) {
            ImageRequest.Builder(context)
                .data("file:///android_asset/figma/common/progress_indicator_01.svg")
                .decoderFactory(SvgDecoder.Factory())
                .build()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF212121).copy(alpha = 0.8f))
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            awaitPointerEvent().changes.forEach { it.consume() }
                        }
                    }
                }
                .semantics {
                    contentDescription = "로딩 중"
                    progressBarRangeInfo = ProgressBarRangeInfo.Indeterminate
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center,
            ) {
                AsyncImage(
                    model = model,
                    contentDescription = null,
                    placeholder = painterResource(R.drawable.progress_indicator_01),
                    error = painterResource(R.drawable.progress_indicator_01),
                    modifier = Modifier
                        .size(112.dp)
                        .graphicsLayer { rotationZ = rotation },
                )
            }
        }
    }
}

@Preview(widthDp = 360, heightDp = 720, locale = "ko", fontScale = 1f)
@Composable
private fun GlobalLoadingSpinnerPreview() {
    FillsaTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFEFCC)),
        ) {
            CircleLoadingSpinner(isLoading = true)
        }
    }
}
