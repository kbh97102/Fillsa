package com.arakene.presentation.ui.quotelist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import com.arakene.domain.responses.MemberQuotesResponse
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.logDebug
import okhttp3.OkHttpClient

@Composable
fun QuoteListItem(
    imagePath: String,
    data: MemberQuotesResponse,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current

    val quote by remember(data) {
        mutableStateOf(
            if (data.korQuote.isNullOrEmpty()) {
                data.engQuote ?: ""
            } else {
                data.korQuote ?: ""
            }
        )
    }

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

    Column(
        modifier.clip(MaterialTheme.shapes.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.tertiary,
                )
                .padding(vertical = 12.dp, horizontal = 27.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                "2025.03.25",
                style = FillsaTheme.typography.buttonXSmallBold,
                color = colorResource(
                    R.color.gray_700
                )
            )

            Text(
                "(수)",
                style = FillsaTheme.typography.buttonXSmallNormal,
                color = colorResource(R.color.gray_700),
                modifier = Modifier.padding(start = 10.dp)
            )
        }

        Box {
            AsyncImage(
                model = imagePath,
                contentDescription = null,
                error = painterResource(R.drawable.img_image_background),
                imageLoader = imageLoader,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                QuoteListItemPager(
                    quote = quote,
                    memo = data.memo ?: ""
                )

                QuoteListItemBottomSection(
                    modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
                )
            }
        }
    }

}

@Composable
@Preview
private fun QuoteListItemPreview() {
    FillsaTheme {
        QuoteListItem(
            imagePath = "",
            data = MemberQuotesResponse()
        )
    }
}