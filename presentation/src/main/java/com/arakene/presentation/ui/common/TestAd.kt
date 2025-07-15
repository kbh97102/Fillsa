package com.arakene.presentation.ui.common

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

@Composable
fun TestAdSection(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val TAG = ">>>>AD"

    // [START create_ad_view]
    val adView = remember { AdView(context) }

    // 테스트 광고 ID임 // TODO: 차후에 릴리즈 빌드할 때는 admobs에 있는 설정으로 올리기
    adView.adUnitId = "ca-app-pub-3940256099942544/9214589741"

    // [START set_ad_size]
    // Set the adaptive banner ad size with a given width.
    val adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
        LocalContext.current,
        (context.resources.displayMetrics.widthPixels / context.resources.displayMetrics.density).toInt()
    )
    adView.setAdSize(adSize)
    // [END set_ad_size]

    // [START banner_screen]
    // Place the ad view at the bottom of the screen.
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 50.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        Box(modifier = modifier.fillMaxWidth()) { BannerAd(adView, modifier) }
    }
    // [END banner_screen]
    // [END create_ad_view]

    // [START add_listener]
    // [Optional] Set an AdListener to receive callbacks for various ad events.
    adView.adListener =
        object : AdListener() {
            override fun onAdLoaded() {
                Log.d(TAG, "Banner ad was loaded.")
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                Log.e(TAG, "Banner ad failed to load: ${error.message}")
            }

            override fun onAdImpression() {
                Log.d(TAG, "Banner ad recorded an impression.")
            }

            override fun onAdClicked() {
                Log.d(TAG, "Banner ad was clicked.")
            }
        }
    // [END add_listener]

    // Prevent loading the AdView if the app is in preview mode.
    if (!LocalInspectionMode.current) {
        // [START load_ad]
        // Create an AdRequest and load the ad.
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        // [END load_ad]
    }

    // [START dispose_ad]
    DisposableEffect(Unit) {
        // Destroy the AdView to prevent memory leaks when the screen is disposed.
        onDispose { adView.destroy() }
    }
    // [END dispose_ad]
}