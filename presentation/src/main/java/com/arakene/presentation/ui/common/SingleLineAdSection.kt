//package com.arakene.presentation.ui.common
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.DisposableEffect
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.style.TextAlign
//import com.arakene.presentation.BuildConfig
//import com.arakene.presentation.ui.theme.FillsaTheme
//import com.arakene.presentation.util.logDebug
//import com.arakene.presentation.util.logError
//import com.google.android.gms.ads.AdListener
//import com.google.android.gms.ads.AdLoader
//import com.google.android.gms.ads.AdRequest
//import com.google.android.gms.ads.LoadAdError
//import com.google.android.gms.ads.nativead.NativeAd
//import kotlinx.coroutines.launch
//
//@Composable
//fun SingleLineAdSection(modifier: Modifier = Modifier) {
//
//    val context = LocalContext.current
//
//    val scope = rememberCoroutineScope()
//
//    var ad: NativeAd? by remember {
//        mutableStateOf(null)
//    }
//
//    val nativeAd = remember {
//        val loader: AdLoader = AdLoader.Builder(context,BuildConfig.ad_native_test)
//            .forNativeAd { loaded ->
//                scope.launch {
//                    ad = loaded
//                }
//            }
//            .withAdListener(object: AdListener(){
//                override fun onAdFailedToLoad(p0: LoadAdError) {
//                    super.onAdFailedToLoad(p0)
//                    logError("Error $p0")
//                }
//            })
//            .build()
//
//        loader
//    }
//
//    LaunchedEffect(Unit) {
//        scope.launch {
//            nativeAd.loadAd(
//                AdRequest.Builder().build()
//            )
//        }
//    }
//
//    DisposableEffect(Unit) {
//        onDispose {
//            ad?.destroy()
//        }
//    }
//
//    Column(modifier = modifier.fillMaxWidth()) {
//        LaunchedEffect(ad) { logDebug("AD $ad") }
//        ad?.headline?.let {
//            Text(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(Color.Black),
//                text = it,
//                color = Color.White,
//                textAlign = TextAlign.Center,
//                style = FillsaTheme.typography.buttonXSmallNormal
//            )
//        }
//    }
//}