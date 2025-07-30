package com.arakene.presentation.ui.home

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.LocalSnackbarHost
import com.arakene.presentation.util.copyToClipboard
import com.arakene.presentation.util.noEffectClickable
import com.arakene.presentation.util.saveBitmapToCache
import com.arakene.presentation.util.saveBitmapToGallery
import kotlinx.coroutines.launch

@Composable
fun ShareView(
    quote: String,
    author: String,
    popBackStack: () -> Unit,
    snackbarHostState: SnackbarHostState = LocalSnackbarHost.current
) {
    val imageList = remember {
        listOf(
            R.drawable.img_share_background_1,
            R.drawable.img_share_background_3,
            R.drawable.img_share_background_4,
            R.drawable.img_share_background_5,
            R.drawable.img_share_background_7,
            R.drawable.img_share_background_8,
            R.drawable.img_share_background_10,
        )
    }

    val state = rememberPagerState { imageList.size }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val clipBoard = LocalClipboard.current

    val graphicLayer = rememberGraphicsLayer()


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
                    popBackStack()
                })

        }

        Column(
            Modifier
                .weight(1f)
                .background(colorResource(R.color.white)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("배경을 선택해주세요.", style = FillsaTheme.typography.heading4, color = Color.White)
            Text("필사한 문장이 이미지로 저장됩니다.", style = FillsaTheme.typography.body2, color = Color.White)

            HorizontalPager(
                state = state,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 30.dp),
                beyondViewportPageCount = 1,
                pageSpacing = 20.dp,
                contentPadding = PaddingValues(horizontal = 60.dp)
            ) { page ->

                ShareItem(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(
                            RoundedCornerShape(30.dp)
                        ),
                    graphicLayer = graphicLayer.takeIf { page == state.currentPage },
                    author = author,
                    quote = quote,
                    backgroundUri = imageList[page]
                )
            }

            ShareBottomSection(
                shareOnClick = {
                    // TODO: 카톡 공유
                    scope.launch {
                        val uri = saveBitmapToCache(
                            context,
                            graphicLayer.toImageBitmap().asAndroidBitmap()
                        )

                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "image/png"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }

                        context.startActivity(
                            Intent.createChooser(intent, "이미지 공유")
                        )

                    }

                },
                copyOnClick = {
                    copyToClipboard(context, scope, clipBoard, snackbarHostState, quote, author)
                },
                saveOnClick = {
                    // TODO: 이미지 저장

                    scope.launch {
                        val bitmap = graphicLayer.toImageBitmap()
                        val uri = saveBitmapToGallery(
                            context = context,
                            bitmap = bitmap.asAndroidBitmap()
                        )

                        // TODO: 문구 찾아보기
                        if (uri != null) {
                            snackbarHostState.showSnackbar("저장 성공")
                        } else {
                            snackbarHostState.showSnackbar("저장 실패")
                        }
                    }

                },
                modifier = Modifier
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
            image = painterResource(R.drawable.icn_save_black),
            text = stringResource(R.string.download),
            onClick = {
                saveOnClick()
            },
            textColor = colorResource(R.color.gray_700)
        )

        ShareButton(
            image = painterResource(R.drawable.icn_copy_black),
            text = stringResource(R.string.copy),
            onClick = {
                copyOnClick()
            },
            textColor = colorResource(R.color.gray_700)
        )

        ShareButton(
            image = painterResource(R.drawable.icn_kakao_circle),
            text = stringResource(R.string.kakao_talk),
            onClick = {
                shareOnClick()
            },
            textColor = colorResource(R.color.gray_700)
        )
    }
}

@Composable
private fun ShareButton(
    image: Painter,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = colorResource(R.color.white)
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
            color = textColor,
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
        author = "존 우든",
        popBackStack = {}
    )
}