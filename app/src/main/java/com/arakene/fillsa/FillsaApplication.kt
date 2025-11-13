package com.arakene.fillsa

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.arakene.data.util.TokenProvider
import com.arakene.domain.usecase.common.GetAccessTokenUseCase
import com.arakene.domain.usecase.common.GetAlarmUsageUseCase
import com.arakene.presentation.BuildConfig
import com.arakene.presentation.util.AlarmManagerHelper
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class FillsaApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var tokenProvider: TokenProvider

    @Inject
    lateinit var getAccessTokenUseCase: GetAccessTokenUseCase

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        MobileAds.initialize(this)

        Firebase.crashlytics.isCrashlyticsCollectionEnabled = true

        FirebaseApp.initializeApp(this)

        KakaoSdk.init(this, BuildConfig.kakao_key)

        CoroutineScope(Dispatchers.IO).launch {
            tokenProvider.setToken(getAccessTokenUseCase())
        }
    }

    fun scheduleMidnightWorker(context: Context) {
        val now = LocalDateTime.now()
        val midnight = now.toLocalDate().plusDays(1).atStartOfDay()
        val initialDelay = Duration.between(now, midnight)

        val workRequest = PeriodicWorkRequestBuilder<MidnightWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelay)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "midnight_work",
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
    }


    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}