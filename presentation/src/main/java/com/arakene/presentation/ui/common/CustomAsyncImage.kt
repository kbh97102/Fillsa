package com.arakene.presentation.ui.common

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
    colorFilter: ColorFilter? = null
) {

    AsyncImage(
        model = imagePath,
        contentDescription = null,
        error = error,
        modifier = modifier,
        contentScale = contentScale,
        colorFilter = colorFilter
    )

}