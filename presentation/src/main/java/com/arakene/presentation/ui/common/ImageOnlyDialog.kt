package com.arakene.presentation.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme

@Composable
fun ImageOnlyDialog(
    imageUri: String,
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {

    if (visible) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {

            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CustomAsyncImage(
                    imagePath = imageUri,
                    contentScale = ContentScale.Fit
                )

                HorizontalDivider(color = FillsaTheme.colorScheme.outlineVariant)

                TextButton(
                    onClick = onDismiss,
                    contentPadding = PaddingValues(vertical = 10.dp, horizontal = 20.dp),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        stringResource(R.string.close),
                        style = FillsaTheme.typography.subtitle2,
                        color = colorResource(R.color.gray_700)
                    )
                }
            }

        }
    }

}


@Preview(showBackground = true)
@Composable
private fun ImageOnlyDialogPreview() {
    ImageOnlyDialog(
        imageUri = "",
        visible = true,
        onDismiss = {}
    )
}