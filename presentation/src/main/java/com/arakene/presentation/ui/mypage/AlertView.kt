package com.arakene.presentation.ui.mypage

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.arakene.presentation.R
import com.arakene.presentation.ui.common.HeaderSection
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.CommonAction
import com.arakene.presentation.util.CommonEffect
import com.arakene.presentation.util.DialogData
import com.arakene.presentation.util.DialogDataHolder
import com.arakene.presentation.util.HandleViewEffect
import com.arakene.presentation.util.LocalDialogDataHolder
import com.arakene.presentation.util.action.MyPageAction
import com.arakene.presentation.util.Navigate
import com.arakene.presentation.util.noEffectClickable
import com.arakene.presentation.viewmodel.MyPageViewModel

@Composable
fun AlertView(
    navigate: Navigate,
    modifier: Modifier = Modifier,
    popBackStack: () -> Unit,
    viewModel: MyPageViewModel = hiltViewModel(),
    dialogDataHolder: DialogDataHolder = LocalDialogDataHolder.current
) {

    val context = LocalContext.current

    val selected by viewModel.getAlarmUsage.collectAsState(false)

    val isLogged by viewModel.isLogged.collectAsState(false)

    HandleViewEffect(
        viewModel.effect,
        LocalLifecycleOwner.current
    ) {
        when (it) {
            is CommonEffect.PopBackStack -> {
                popBackStack()
            }

            is CommonEffect.Move -> {
                navigate(it.screen)
            }
        }
    }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager?.canScheduleExactAlarms() == true) {
                    // ✅ 권한 허용됨 - 알람 설정 가능
                    viewModel.checkAlarmState()
                }
            }
        }

    LaunchedEffect(Unit) {
        runCatching {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
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

                        }
                        .build()
                }.show = true
            } else {
                viewModel.checkAlarmState()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        HeaderSection(
            modifier = Modifier.padding(horizontal = 20.dp),
            text = stringResource(R.string.alert),
            onBackPress = {
                viewModel.handleContract(CommonAction.PopBackStack)
            }
        )

        AlertSwitchSection(
            selected = selected,
            setSelected = {
                viewModel.handleContract(MyPageAction.ClickAlarmUsage(usage = it))
            }
        )

        if (isLogged) {
            Row(
                modifier = Modifier
                    .padding(top = 50.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondary)
                    .padding(horizontal = 20.dp, vertical = 19.dp)
                    .noEffectClickable {
                        dialogDataHolder.data = DialogData.Builder()
                            .title(context.getString(R.string.resign_title))
                            .titleTextSize(20.sp)
                            .bodyTextSize(16.sp)
                            .body(context.getString(R.string.resign_body))
                            .reversed(true)
                            .okText(context.getString(R.string.cancel))
                            .cancelText(context.getString(R.string.resign))
                            .cancelOnClick {
                                viewModel.handleContract(MyPageAction.Resign)
                            }
                            .build()

                        dialogDataHolder.show = true
                    },
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    stringResource(R.string.resign),
                    style = FillsaTheme.typography.body2,
                    color = colorResource(R.color.gray_700)
                )

                Image(painter = painterResource(R.drawable.icn_sign_out), contentDescription = null)

            }
        }

    }

}