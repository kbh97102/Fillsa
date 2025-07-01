package com.arakene.presentation.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import com.arakene.presentation.ui.theme.FillsaTheme
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.coroutines.launch

@Composable
fun SingleLineAdSection(modifier: Modifier = Modifier) {

    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    var ad: NativeAd? by remember {
        mutableStateOf(null)
    }

    val nativeAd = remember {
        val loader: AdLoader = AdLoader.Builder(context, "ca-app-pub-3940256099942544/2247696110")
            .forNativeAd { loaded ->
                scope.launch {
                    ad = loaded
                }
            }
            .build()

        loader
    }

    LaunchedEffect(Unit) {
        nativeAd.loadAd(
            AdRequest.Builder().build()
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            ad?.destroy()
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        ad?.headline?.let {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black),
                text = it,
                color = Color.White,
                textAlign = TextAlign.Center,
                style = FillsaTheme.typography.buttonXSmallNormal
            )
        }
    }
}