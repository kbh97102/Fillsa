package com.arakene.presentation.ui.common

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.arakene.presentation.R
import com.arakene.presentation.util.DialogData
import com.arakene.presentation.util.DialogDataHolder
import com.arakene.presentation.util.LocalDialogDataHolder
import com.arakene.presentation.util.Navigate
import com.arakene.presentation.viewmodel.SplashViewModel

@Composable
fun SplashView(
    navigate: Navigate,
    viewModel: SplashViewModel = hiltViewModel(),
    dialogDataHolder: DialogDataHolder = LocalDialogDataHolder.current
) {

    val ready by viewModel.ready.collectAsState()

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lottie_splash))
    val lottieState = animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    val isPermissionRequestedBefore by viewModel.isPermissionRequestedBefore.collectAsState(false)

    val context = LocalContext.current

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager?.canScheduleExactAlarms() == true) {
                    // ✅ 권한 허용됨 - 알람 설정 가능
                    viewModel.setAlarm()
                    viewModel.permissionChecked.value = true
                } else {
                    // ❌ 권한 아직 없음 - 사용자 거절함
                    viewModel.permissionChecked.value = true
                }
            }
        }


    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && it.containsKey(Manifest.permission.POST_NOTIFICATIONS)) {
            viewModel.setAlarmUsage(it[Manifest.permission.POST_NOTIFICATIONS] == true)


            runCatching {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                if (!alarmManager.canScheduleExactAlarms()) {

                    if (isPermissionRequestedBefore) {
                        viewModel.permissionChecked.value = true
                        return@runCatching
                    }

                    viewModel.setPermissionRequested()

                    dialogDataHolder.apply {
                        data = DialogData.Builder()
                            .title(context.getString(R.string.alarm_permission_title))
                            .body(context.getString(R.string.alarm_permisstion_body))
                            .drawableId(R.drawable.icn_bell_fill)
                            .titleTextSize(20.sp)
                            .onClick {
                                // 시스템 설정으로 이동 유도
                                val intent =
                                    Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                                        data = "package:${context.packageName}".toUri()
                                    }
                                launcher.launch(intent)
                            }
                            .cancelOnClick {
                                viewModel.permissionChecked.value = true
                            }
                            .build()
                    }.show = true
                } else {
                    viewModel.setAlarm()
                    viewModel.permissionChecked.value = true
                }
            }
                .onFailure {
                    viewModel.permissionChecked.value = true
                }
        } else {
            viewModel.cancelAlarm()
            viewModel.permissionChecked.value = true
        }
    }

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
            viewModel.permissionChecked.value = true
        }
    }

    LaunchedEffect(ready) {
        if (ready) {
            navigate(viewModel.destination)
        }
    }

    LaunchedEffect(lottieState.iteration) {
        if (lottieState.iteration == 2) {
            viewModel.hasPlayedOnce.value = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { lottieState.progress },
            safeMode = true,
            modifier = Modifier.size(192.dp)
        )
    }


}