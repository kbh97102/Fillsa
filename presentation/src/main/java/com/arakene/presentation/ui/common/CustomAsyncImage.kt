package com.arakene.presentation.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImage
import com.arakene.presentation.R

@Composable
fun CustomAsyncImage(
    imagePath: String,
    modifier: Modifier = Modifier,
    error: Painter? = painterResource(R.drawable.img_image_background),
    contentScale: ContentScale = ContentScale.Crop,
    useDim: Boolean = false
) {

    Box {
        AsyncImage(
            model = imagePath,
            contentDescription = null,
            error = error,
            modifier = modifier,
            contentScale = contentScale,
        )

        if (useDim) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(colorResource(R.color.gray_700).copy(alpha = 0.3f))
            )
        }
    }

}