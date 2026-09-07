package com.arakene.presentation.ui.calendar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.svg.SvgDecoder

@Composable
internal fun CalendarFigmaAsset(
    fileName: String,
    modifier: Modifier = Modifier,
    darkMode: Boolean = false,
    assetSet: String = "calendar",
    tint: Color? = null,
) {
    val context = LocalContext.current
    AsyncImage(
        model = remember(fileName, darkMode, assetSet) {
            ImageRequest.Builder(context)
                .data("file:///android_asset/figma/${if (darkMode) "$assetSet-night" else assetSet}/$fileName")
                .decoderFactory(SvgDecoder.Factory())
                .build()
        },
        contentDescription = null,
        contentScale = ContentScale.Fit,
        colorFilter = tint?.let(ColorFilter::tint),
        modifier = modifier,
    )
}
