package com.arakene.presentation.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.arakene.presentation.R
import com.arakene.presentation.ui.common.CustomAsyncImage
import com.arakene.presentation.util.logDebug
import com.arakene.presentation.util.noEffectClickable

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
            CustomAsyncImage(
                imagePath = imageUri,
                modifier = Modifier.fillMaxSize(),
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