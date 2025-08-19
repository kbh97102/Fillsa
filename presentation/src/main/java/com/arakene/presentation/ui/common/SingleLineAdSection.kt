package com.arakene.presentation.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arakene.domain.model.AdState
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.logDebug
import com.arakene.presentation.viewmodel.AdViewModel
import com.google.android.gms.compose_util.NativeAdAttribution
import com.google.android.gms.compose_util.NativeAdHeadlineView
import com.google.android.gms.compose_util.NativeAdIconView
import com.google.android.gms.compose_util.NativeAdView

@Composable
fun SingleLineAdSection(
    modifier: Modifier = Modifier,
    refresh: Boolean = false,
) {
    LocalContext.current

    val viewModel: AdViewModel = hiltViewModel()

    val adState by viewModel.adState.collectAsStateWithLifecycle()

    LaunchedEffect(refresh) {
        if (refresh) {
            logDebug("Effect refresh ad")
            viewModel.testMethod()
        }
    }

    LifecycleResumeEffect(Unit) {

        if (!refresh) {
            logDebug("Resume refresh ad")
            viewModel.testMethod()
        }

        onPauseOrDispose {

        }
    }

    when (adState) {
        is AdState.Success -> {
            val ads = (adState as? AdState.Success)?.nativeAd ?: return

            NativeAdView(
                modifier = Modifier
                    .background(color = colorResource(R.color.primary))
                    .padding(horizontal = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Display the ad attribution.
                    NativeAdAttribution(
                        modifier = Modifier.weight(0.2f)
                    )
                    // Add remaining assets such as the image and media view.

                    Row(
                        modifier = Modifier.weight(0.6f),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) { ads.icon?.let { icon ->
                        NativeAdIconView(Modifier.padding(5.dp)) {
                            icon.drawable?.toBitmap()?.let { bitmap ->
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    "Icon",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        ads.headline?.let {
                            NativeAdHeadlineView {
                                Text(
                                    modifier = Modifier,
                                    text = it,
                                    color = Color.Black,
                                    textAlign = TextAlign.Center,
                                    style = FillsaTheme.typography.buttonXSmallNormal
                                )
                            }
                        } }
                    }

                    Box(modifier = Modifier.weight(0.2f))
                }
            }
        }

        else -> {}
    }
}