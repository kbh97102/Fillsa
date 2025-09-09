package com.arakene.presentation.ui.common

import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
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
import com.arakene.presentation.viewmodel.AdViewModel
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.compose_util.LocalNativeAdView
import com.google.android.gms.compose_util.NativeAdChoicesView
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

                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .background(colorResource(backgroundColor)),
                    horizontalArrangement = Arrangement.Center
                ) {
                    SingleLineAdContent(
                        ads, modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                    )
                }
            }


        }

        else -> {}
    }
}

@Composable
fun SingleLineAdContent(nativeAd: NativeAd, modifier: Modifier = Modifier) {
    CustomNativeAdView(modifier = modifier.fillMaxWidth(), nativeAd = nativeAd) {

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AdAttributeIcon()


            nativeAd.icon?.let { icon ->
                Spacer(Modifier.width(6.dp))
                NativeAdIconView {
                    icon.drawable?.toBitmap()?.let { bitmap ->
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            "ad icon",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            nativeAd.headline?.let {
                Spacer(Modifier.width(6.dp))
                NativeAdHeadlineView {
                    Text(
                        modifier = Modifier,
                        text = it,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        style = FillsaTheme.typography.buttonXSmallNormal
                    )
                }
            }

            NativeAdChoicesView()
        }
    }
}


@Composable
fun CustomNativeAdView(
    modifier: Modifier = Modifier,
    nativeAd: NativeAd,
    content: @Composable () -> Unit
) {
    val localContext = LocalContext.current
    val nativeAdView = remember {
        val adView = NativeAdView(localContext).apply { id = View.generateViewId() }
        adView.setNativeAd(nativeAd)
        adView
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
                    ComposeView(context).apply {
                        layoutParams =
                            ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                            )
                        setContent {
                            // Set `nativeAdView` as the current LocalNativeAdView so that
                            // `content` can access the `NativeAdView` via `LocalNativeAdView.current`.
                            // This would allow ad attributes (such as `NativeHeadline`) to attribute
                            // its contained View subclass via setter functions (e.g. nativeAdView.headlineView =
                            // view)
                            CompositionLocalProvider(LocalNativeAdView provides nativeAdView) { content.invoke() }
                        }
                    }
                )
            }
        },
        modifier = modifier,
    )
}

@Composable
private fun AdAttributeIcon(modifier: Modifier = Modifier) {
    Box(
        modifier
            .background(colorResource(R.color.gray_700), shape = RoundedCornerShape(100))
            .padding(horizontal = 6.dp)
    ) {
        Text("AD", style = FillsaTheme.typography.body4, color = Color.White)
    }
}