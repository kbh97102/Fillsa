package com.arakene.presentation.ui.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.util.DebugLogger
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.ui.theme.defaultButtonColors
import com.arakene.presentation.util.logDebug
import com.arakene.presentation.util.noEffectClickable
import okhttp3.OkHttpClient

@Composable
fun ImageDialog(
    quote: String,
    author: String,
    backgroundImageUrl: String,
    onDismiss: () -> Unit,
    uploadImage: (Uri) -> Unit,
    deleteOnClick: () -> Unit
) {

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                logDebug("gallery uri $it")
                uploadImage(it)
            }
        }
    )

    val context = LocalContext.current

    val imageLoader = remember(backgroundImageUrl) {
        ImageLoader.Builder(context)
            .logger(DebugLogger())
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
                .clip(MaterialTheme.shapes.medium)
        ) {

            if (backgroundImageUrl.isEmpty()) {
                Image(
                    painterResource(R.drawable.img_image_background),
                    contentDescription = null,
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                AsyncImage(
                    model = backgroundImageUrl,
                    contentDescription = null,
                    error = painterResource(R.drawable.img_image_background),
                    imageLoader = imageLoader,
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 20.dp)
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painterResource(R.drawable.icn_close_black), contentDescription = null,
                        modifier = Modifier.noEffectClickable {
                            onDismiss()
                        })

                    if (backgroundImageUrl.isNotEmpty()) {
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

                Text(
                    quote,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 90.dp),
                    textAlign = TextAlign.Center,
                    style = FillsaTheme.typography.body2,
                    color = colorResource(R.color.gray_700)
                )

                Text(
                    author,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    textAlign = TextAlign.Center,
                    style = FillsaTheme.typography.body2,
                    color = colorResource(R.color.gray_700)
                )

                Row(
                    modifier = Modifier.padding(top = 86.dp),
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