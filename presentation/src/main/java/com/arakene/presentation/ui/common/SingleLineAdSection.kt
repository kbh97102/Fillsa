package com.arakene.presentation.ui.common

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arakene.domain.model.AdState
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.MyPageScreens
import com.arakene.presentation.viewmodel.AdViewModel
import com.google.android.gms.compose_util.NativeAdHeadlineView
import com.google.android.gms.compose_util.NativeAdIconView
import com.google.android.gms.compose_util.NativeAdView

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
                NativeAdView(
                    modifier = modifier
                        .background(color = MaterialTheme.colorScheme.primary)
                        .padding(horizontal = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        AdAttributeIcon()

                        Spacer(Modifier.width(6.dp))

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ads.icon?.let { icon ->
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
                                            style = FillsaTheme.typography.buttonXSmallNormal.copy(
                                                fontSize = 10.sp
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }


        }

        else -> {}
    }
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