package com.arakene.presentation.ui.common

import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arakene.domain.model.AdState
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.MyPageScreens
import com.arakene.presentation.util.logDebug
import com.arakene.presentation.viewmodel.AdViewModel
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.compose_util.NativeAdAttribution
import com.google.android.gms.compose_util.NativeAdHeadlineView
import com.google.android.gms.compose_util.NativeAdIconView

@Composable
fun SingleLineAdSection(
    currentRoute: String,
    modifier: Modifier = Modifier,
    refresh: Boolean = false,
) {

    val viewModel: AdViewModel = hiltViewModel()

    val adState by viewModel.adState.collectAsStateWithLifecycle()

    val backgroundColor by remember(currentRoute) {
        mutableIntStateOf(
            when {
                currentRoute.contains(MyPageScreens.Notice.routeString) || currentRoute.contains(
                    MyPageScreens.NoticeDetail().routeString
                )
                    -> R.color.yellow01

                else -> R.color.primary
            }
        )
    }

    LaunchedEffect(refresh) {
        if (refresh) {
            viewModel.refreshAds()
        }
    }

    LifecycleResumeEffect(Unit) {

        if (!refresh) {
            viewModel.refreshAds()
        }

        onPauseOrDispose {

        }
    }

    when (adState) {
        is AdState.Success -> {
            val ads = (adState as? AdState.Success)?.nativeAd ?: return

            val currentColorScheme = MaterialTheme.colorScheme
            val modifiedColorScheme = currentColorScheme.copy(
                primary = colorResource(backgroundColor)
            )

            MaterialTheme(
                colorScheme = modifiedColorScheme
            ) {
                LaunchedEffect(ads) {
                    logDebug(
                        """
                        extra ${ads.extras} 
                        adChoiceInfo ${ads.adChoicesInfo}
                        callTo Action ${ads.callToAction}
                        store ${ads.store}
                    """.trimIndent()
                    )
                }

                TestNativeAd(ads, modifier = Modifier.fillMaxWidth())
//                NativeAdView(
//                    modifier = modifier
//                        .background(color = MaterialTheme.colorScheme.primary)
//                        .padding(horizontal = 10.dp)
//                ) {
//
//                }
            }


        }

        else -> {}
    }
}


@Composable
fun TestNativeAd(nativeAd: NativeAd, modifier: Modifier = Modifier) {
    val localContext = LocalContext.current
    val nativeAdView = remember {
        com.google.android.gms.ads.nativead.NativeAdView(localContext)
            .apply { id = View.generateViewId() }
    }
    val composeView = remember {
        ComposeView(localContext).apply {
            layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )
            setContent {

            }
        }
    }

    AndroidView(
        factory = {
            nativeAdView.apply {
                layoutParams =
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                    )
                addView(
                    composeView
                )
            }
        },
        update = { view ->
            nativeAdView.setNativeAd(nativeAd)
            nativeAdView.callToActionView = composeView
            composeView.setContent { TestNativeAdContent(nativeAd) }
        },
        modifier = modifier,
    )
}

@Composable
fun TestNativeAdContent(ads: NativeAd, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Display the ad attribution.
//        NativeAdAttribution(
//            modifier = Modifier.weight(0.2f)
//        )
        // Add remaining assets such as the image and media view.

        Row(
            modifier = Modifier.weight(0.6f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {



            ads.icon?.let { icon ->
                icon.drawable?.toBitmap()?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        "ad icon",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            ads.headline?.let {
                Text(
                    modifier = Modifier,
                    text = it,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    style = FillsaTheme.typography.buttonXSmallNormal
                )
            }
        }
    }
}