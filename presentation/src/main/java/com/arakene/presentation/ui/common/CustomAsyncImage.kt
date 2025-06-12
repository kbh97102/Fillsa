package com.arakene.presentation.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import com.arakene.presentation.R
import okhttp3.OkHttpClient

@Composable
fun CustomAsyncImage(
    imagePath: String,
    modifier: Modifier = Modifier,
    error: Painter? = painterResource(R.drawable.img_image_background),
    contentScale: ContentScale = ContentScale.Crop,
) {

    val context = LocalContext.current

    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                add(
                    OkHttpNetworkFetcherFactory(
                        callFactory = {
                            OkHttpClient()
                        }
                    ))
            }
            .build()
    }

    AsyncImage(
        model = imagePath,
        contentDescription = null,
        error = error,
        imageLoader = imageLoader,
        modifier = modifier,
        contentScale = contentScale
    )

}