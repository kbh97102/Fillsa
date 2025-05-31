package com.arakene.presentation.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.LocalSnackbarHost
import com.arakene.presentation.util.copyToClipboard
import com.arakene.presentation.util.noEffectClickable

@Composable
fun ShareView(
    quote: String,
    author: String,
    snackbarHostState: SnackbarHostState = LocalSnackbarHost.current
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val clipBoard = LocalClipboard.current

    Column(modifier = Modifier.fillMaxSize()) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 15.dp, vertical = 9.dp)
        ) {

            Image(
                painter = painterResource(R.drawable.icn_arrow),
                contentDescription = null,
                modifier = Modifier.noEffectClickable {

                })

        }

        Box(modifier = Modifier.weight(1f)) {

            Image(
                painter = painterResource(R.drawable.img_image_background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.align(Alignment.Center)) {
                Text(
                    text = quote,
                    color = colorResource(R.color.gray_700),
                    style = FillsaTheme.typography.body1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )

                Text(
                    author,
                    color = colorResource(R.color.gray_700),
                    style = FillsaTheme.typography.body1,
                    textDecoration = TextDecoration.Underline,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                )
            }

            ShareBottomSection(
                shareOnClick = {
                    // TODO: 카톡 공유
                },
                copyOnClick = {
                    copyToClipboard(context, scope, clipBoard, snackbarHostState, quote, author)
                },
                saveOnClick = {
                    // TODO: 이미지 저장
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 50.dp)
            )

        }
    }

}

@Composable
private fun ShareBottomSection(
    saveOnClick: () -> Unit,
    copyOnClick: () -> Unit,
    shareOnClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(50.dp)
    ) {
        ShareButton(
            image = painterResource(R.drawable.icn_download_circle),
            text = stringResource(R.string.download),
            onClick = {}
        )

        ShareButton(
            image = painterResource(R.drawable.icn_copy_circle),
            text = stringResource(R.string.copy),
            onClick = {}
        )

        ShareButton(
            image = painterResource(R.drawable.icn_kakao_circle),
            text = stringResource(R.string.kakao_talk),
            onClick = {}
        )
    }
}

@Composable
private fun ShareButton(
    image: Painter,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .noEffectClickable { onClick() }, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(color = Color.White, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {

            Image(painter = image, contentDescription = null)
        }

        Text(
            text,
            color = Color.White,
            style = FillsaTheme.typography.body3,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}


@Composable
@Preview
private fun ShareBottomSectionPreview() {
    ShareBottomSection(
        shareOnClick = {},
        copyOnClick = {},
        saveOnClick = {}
    )
}

@Composable
@Preview
private fun ShareViewPreview() {
    ShareView(
        quote = "상황을 가장 잘 활용하는 사람이 가장 좋은 상황을 맞는다.",
        author = "존 우든"
    )
}