package com.arakene.presentation.ui.common

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.arakene.presentation.R
import com.arakene.presentation.util.Navigate
import com.arakene.presentation.util.Screens
import com.arakene.presentation.util.logDebug
import com.arakene.presentation.viewmodel.SplashViewModel

@Composable
fun SplashView(
    navigate: Navigate,
    viewModel: SplashViewModel = hiltViewModel()
) {

    val ready by viewModel.ready.collectAsState()

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lottie_splash))
    val lottieState = animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        viewModel.permissionChecked.value = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && it.containsKey(Manifest.permission.POST_NOTIFICATIONS)) {
            viewModel.setAlarmUsage(it[Manifest.permission.POST_NOTIFICATIONS] == true)

            viewModel.setAlarm()
        } else {
            viewModel.cancelAlarm()
        }
    }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.checkReady()
        val permissions = when {
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

        val notGranted = permissions.any {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_DENIED
        }

        if (notGranted) {
            permissionLauncher.launch(permissions)
        } else {
            logDebug("permission granted")
            viewModel.permissionChecked.value = true
        }
    }

    LaunchedEffect(ready) {
        if (ready) {
            navigate(Screens.Home())
        }
    }

    LaunchedEffect(lottieState.iteration) {
        if (lottieState.iteration == 2) {
            viewModel.hasPlayedOnce.value = true
        }
    }

    LottieAnimation(
        composition = composition,
        progress = { lottieState.progress },
    )

}