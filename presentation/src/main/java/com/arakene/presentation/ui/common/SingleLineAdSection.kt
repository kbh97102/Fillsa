package com.arakene.presentation.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arakene.domain.model.AdState
import com.arakene.presentation.BuildConfig
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.logError
import com.arakene.presentation.viewmodel.AdViewModel
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.coroutines.launch

@Composable
fun SingleLineAdSection(modifier: Modifier = Modifier) {


    val viewModel: AdViewModel = hiltViewModel()

    val adState by viewModel.adState.collectAsStateWithLifecycle()

    when(adState){
        is AdState.Success -> {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                (adState as? AdState.Success)?.nativeAd?.headline?.let {
                    Text(
                        text = it,
                        color = colorResource(R.color.gray_700),
                        textAlign = TextAlign.Center,
                        style = FillsaTheme.typography.buttonXSmallNormal
                    )
                }
            }
        }
        else -> {}
    }
}