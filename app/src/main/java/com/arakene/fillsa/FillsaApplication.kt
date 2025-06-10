package com.arakene.fillsa

import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.arakene.data.util.DailyNotificationWorker
import com.arakene.data.util.TokenProvider
import com.arakene.domain.usecase.common.GetAccessTokenUseCase
import com.arakene.domain.usecase.common.GetAlarmUsageUseCase
import com.arakene.presentation.BuildConfig
import com.google.firebase.FirebaseApp
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
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

    @Inject
    lateinit var getAlarmUsageUseCase: GetAlarmUsageUseCase

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

                    if (it) {
                        scheduleDailyNotification(this@FillsaApplication)
                    } else {
                        WorkManager.getInstance(this@FillsaApplication)
                            .cancelUniqueWork("DailyNotification")
                    }
                }
        }
    }

    private fun scheduleDailyNotification(context: Context) {
        val currentTime = LocalDateTime.now()
        val targetTime = currentTime.withHour(9).withMinute(0).withSecond(0).withNano(0)

        val delay = Duration.between(currentTime, targetTime).toMillis().let {
            if (it < 0) it + 24 * 60 * 60 * 1000 else it
        }

        val workRequest = PeriodicWorkRequestBuilder<DailyNotificationWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "DailyNotification",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}