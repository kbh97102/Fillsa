package com.arakene.presentation.ui.home

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.ui.theme.defaultButtonColors
import com.arakene.presentation.util.noEffectClickable

@Composable
fun ImageDialog(
    quote: String,
    author: String,
    onDismiss: () -> Unit
) {

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

            Image(
                painterResource(R.drawable.img_image_background),
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )

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

                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = stringResource(R.string.delete),
                        style = FillsaTheme.typography.body2,
                        color = colorResource(R.color.gray_700),
                        textDecoration = TextDecoration.Underline
                    )
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
            onDismiss = {}
        )
    }
}