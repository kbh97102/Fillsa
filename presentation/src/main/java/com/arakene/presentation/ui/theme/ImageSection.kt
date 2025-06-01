package com.arakene.presentation.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import com.arakene.presentation.R
import com.arakene.presentation.util.logDebug
import com.arakene.presentation.util.noEffectClickable
import okhttp3.OkHttpClient

@Composable
fun ImageSection(
    imageUri: String,
    isLogged: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    LaunchedEffect(imageUri) {
        logDebug("uri $imageUri")
    }

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

    Box(
        modifier = modifier
            .aspectRatio(155 / 120f)
            .clip(MaterialTheme.shapes.medium)
            .noEffectClickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {

        if (imageUri.isEmpty()) {
            Image(
                painterResource(R.drawable.img_image_background),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            AsyncImage(
                model = imageUri,
                contentDescription = null,
                error = painterResource(R.drawable.img_image_background),
                imageLoader = imageLoader,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }


        if (!isLogged) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        colorResource(R.color.black_0c).copy(alpha = 0.6f),
                        shape = MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(painterResource(R.drawable.icn_lock), contentDescription = null)
            }
        }

    }

}


@Preview(widthDp = 145, heightDp = 120)
@Composable
private fun ImageSectionPreview() {
    ImageSection(
        isLogged = false,
        onClick = {},
        imageUri = ""
    )
}