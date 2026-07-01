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
import com.arakene.presentation.util.IsDarkMode
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.DialogData
import com.arakene.presentation.util.DialogDataHolder
import com.arakene.presentation.util.LocalDialogDataHolder
import com.arakene.presentation.util.Navigate
import com.arakene.presentation.util.TypographyEnum
import com.arakene.presentation.viewmodel.SplashViewModel

@Composable
fun SplashView(
    navigate: Navigate,
    darkTheme: Boolean = IsDarkMode.current,
    viewModel: SplashViewModel = hiltViewModel(),
    dialogDataHolder: DialogDataHolder = LocalDialogDataHolder.current
) {

    val ready by viewModel.ready.collectAsState()

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(if (darkTheme) R.raw.lottie_splash_dark else R.raw.lottie_splash))
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
            if (!isPermissionRequestedBefore) {
                viewModel.setAlarmUsage(it[Manifest.permission.POST_NOTIFICATIONS] == true)
            }

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
                            .titleTextStyle(TypographyEnum.Heading4)
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
                    viewModel.permissionChecked.value = true
                }
            }
                .onFailure {
                    viewModel.permissionChecked.value = true
                }
        } else {
            viewModel.permissionChecked.value = true
        }
    }

    LaunchedEffect(Unit) {
        viewModel.checkReady()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_DENIED
        ) {
            permissionLauncher.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
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
            .background(
                color = if (darkTheme) {
                    colorResource(R.color.gray_700)
                } else {
                    Color.White
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text(
                stringResource(R.string.splash_logo_text), style = FillsaTheme.typography.quote,
                color = if (darkTheme) {
                    colorResource(R.color.yellow01)
                } else {
                    colorResource(R.color.gray_700)
                }
            )

            LottieAnimation(
                composition = composition,
                progress = { lottieState.progress },
                safeMode = true,
                modifier = Modifier.size(192.dp)
            )
        }
    }


}
