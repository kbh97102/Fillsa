package com.arakene.fillsa

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.arakene.data.util.AlarmManagerHelper
import com.arakene.data.util.AlarmReceiver
import com.arakene.data.util.TokenProvider
import com.arakene.domain.usecase.common.GetAccessTokenUseCase
import com.arakene.domain.usecase.common.GetAlarmUsageUseCase
import com.arakene.presentation.BuildConfig
import com.arakene.presentation.util.logDebug
import com.google.firebase.FirebaseApp
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class FillsaApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var tokenProvider: TokenProvider

    @Inject
    lateinit var getAccessTokenUseCase: GetAccessTokenUseCase

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var getAlarmUsageUseCase: GetAlarmUsageUseCase

    @Inject
    lateinit var alarmManagerHelper: AlarmManagerHelper

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

        KakaoSdk.init(this, BuildConfig.kakao_key)

        CoroutineScope(Dispatchers.IO).launch {
            tokenProvider.setToken(getAccessTokenUseCase())
        }

        toggleAlert()
    }

    private fun toggleAlert() {
        CoroutineScope(Dispatchers.IO).launch {
            getAlarmUsageUseCase()
                .collectLatest {
                    scheduleDailyNotification(this@FillsaApplication, it)
                }
        }
    }

    private fun scheduleDailyNotification(context: Context, enable: Boolean) {


        if (enable) {
            alarmManagerHelper.setAlarm()
        } else {
            alarmManagerHelper.cancelAlarm()
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}