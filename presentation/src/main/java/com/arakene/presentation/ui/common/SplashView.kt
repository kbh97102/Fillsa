package com.arakene.presentation.ui.common

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.arakene.presentation.viewmodel.SplashViewModel

@Composable
fun SplashView(
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = hiltViewModel()
) {

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && it.containsKey(Manifest.permission.POST_NOTIFICATIONS)) {
            viewModel.setAlarmUsage(it[Manifest.permission.POST_NOTIFICATIONS] == true)

            viewModel.setAlarm()
        } else {
            viewModel.cancelAlarm()
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                    arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.POST_NOTIFICATIONS,
                        Manifest.permission.SCHEDULE_EXACT_ALARM
                    )
                }

                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.SCHEDULE_EXACT_ALARM
                    )
                }

                else -> {
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                }
            }
        )
    }

//    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading))
//    val progress by animateLottieCompositionAsState(composition)
//    LottieAnimation(
//        composition = composition,
//        progress = { progress },
//    )


}