package com.arakene.presentation.ui.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.arakene.presentation.R
import com.arakene.presentation.ui.common.CustomAsyncImage
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.ui.theme.gangwoneduall
import com.arakene.presentation.ui.theme.defaultButtonColors
import com.arakene.presentation.util.logDebug
import com.arakene.presentation.util.IsDarkMode
import com.arakene.presentation.util.noEffectClickable

internal enum class ImageDialogTextLayout {
    Intrinsic,
    BoundedScrollable,
}

internal fun imageDialogTextLayout(darkMode: Boolean): ImageDialogTextLayout =
    if (darkMode) ImageDialogTextLayout.BoundedScrollable else ImageDialogTextLayout.Intrinsic

@Composable
fun ImageDialog(
    quote: String,
    author: String,
    backgroundImageUrl: String,
    onDismiss: () -> Unit,
    uploadImage: (Uri) -> Unit,
    deleteOnClick: () -> Unit,
    showDeleteAction: Boolean = backgroundImageUrl.isNotEmpty(),
    darkMode: Boolean = IsDarkMode.current,
) {

    LaunchedEffect(backgroundImageUrl) {
        logDebug("uri $backgroundImageUrl")
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                uploadImage(it)
            }
        }
    )

    val maxDialogHeight = (LocalConfiguration.current.screenHeightDp - 48).dp
    val textScrollState = rememberScrollState()
    val textLayout = imageDialogTextLayout(darkMode)

    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = {
            onDismiss()
        }
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .then(
                    if (darkMode) {
                        Modifier.heightIn(min = 373.dp, max = maxDialogHeight)
                    } else {
                        Modifier.wrapContentHeight()
                    }
                )
                .shadow(if (darkMode) 23.dp else 0.dp, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
        ) {

            if (backgroundImageUrl.isEmpty()) {
                Image(
                    painterResource(R.drawable.img_share_background_1),
                    contentDescription = null,
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                CustomAsyncImage(
                    imagePath = backgroundImageUrl,
                    modifier = Modifier.matchParentSize(),
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (darkMode) {
                            Modifier.heightIn(min = 373.dp, max = maxDialogHeight)
                        } else {
                            Modifier
                        }
                    )
                    .padding(horizontal = 12.dp, vertical = 20.dp),
                verticalArrangement = if (darkMode) Arrangement.SpaceBetween else Arrangement.Top,
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painterResource(R.drawable.icn_close_black), contentDescription = null,
                        modifier = Modifier.noEffectClickable {
                            onDismiss()
                        })

                    if (showDeleteAction) {
                        Text(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .noEffectClickable {
                                    deleteOnClick()
                                },
                            text = stringResource(R.string.delete),
                            style = FillsaTheme.typography.body2,
                            color = colorResource(R.color.gray_700),
                            textDecoration = TextDecoration.Underline
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (textLayout == ImageDialogTextLayout.BoundedScrollable) {
                                Modifier.weight(1f, fill = false)
                            } else {
                                Modifier.padding(top = 90.dp)
                            }
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (textLayout == ImageDialogTextLayout.BoundedScrollable) {
                                    Modifier.verticalScroll(textScrollState)
                                } else {
                                    Modifier
                                }
                            ),
                    ) {
                        Text(
                            quote,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = if (darkMode) {
                                FillsaTheme.typography.quote.copy(fontFamily = gangwoneduall)
                            } else {
                                FillsaTheme.typography.body2
                            },
                            color = colorResource(R.color.gray_700)
                        )

                        Text(
                            author,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            textAlign = TextAlign.Center,
                            style = if (darkMode) {
                                FillsaTheme.typography.quote.copy(fontFamily = gangwoneduall)
                            } else {
                                FillsaTheme.typography.body2
                            },
                            color = colorResource(R.color.gray_700),
                            textDecoration = if (darkMode) TextDecoration.Underline else TextDecoration.None,
                        )
                    }
                }

                Row(
                    modifier = Modifier.padding(top = if (darkMode) 0.dp else 86.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        shape = MaterialTheme.shapes.small,
                        colors = MaterialTheme.colorScheme.defaultButtonColors,
                        onClick = {},
                        contentPadding = PaddingValues(horizontal = 17.dp, vertical = 15.dp)
                    ) {
                        Text(
                            stringResource(R.string.change_image),
                            style = FillsaTheme.typography.buttonMediumBold,
                            color = colorResource(R.color.gray_700),
                            modifier = Modifier.noEffectClickable {
                                galleryLauncher.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            }
                        )
                    }

                    Button(
                        shape = MaterialTheme.shapes.small,
                        colors = MaterialTheme.colorScheme.defaultButtonColors,
                        onClick = onDismiss,
                        contentPadding = PaddingValues(vertical = 15.dp),
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 10.dp)
                    ) {
                        Text(
                            stringResource(R.string.ok),
                            style = FillsaTheme.typography.buttonMediumBold,
                            color = colorResource(R.color.gray_700),
                        )
                    }
                }

            }

        }

    }

}


@Composable
@Preview
private fun ImageDialogPreview() {
    FillsaTheme {
        ImageDialog(
            quote = "123123123123",
            author = "asdfasdf",
            onDismiss = {},
            uploadImage = {},
            backgroundImageUrl = "",
            deleteOnClick = {}
        )
    }
}
