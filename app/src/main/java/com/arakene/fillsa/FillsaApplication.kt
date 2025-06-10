package com.arakene.fillsa

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.arakene.data.util.DailyNotificationWorker
import com.arakene.data.util.TokenProvider
import com.arakene.domain.usecase.common.GetAccessTokenUseCase
import com.arakene.presentation.BuildConfig
import com.google.firebase.FirebaseApp
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

        KakaoSdk.init(this, BuildConfig.kakao_key)

        CoroutineScope(Dispatchers.IO).launch {
            tokenProvider.setToken(getAccessTokenUseCase())
        }

        scheduleDailyNotification(this)
    }

    private fun scheduleDailyNotification(context: Context) {
        val currentTime = LocalDateTime.now()
        val targetTime = currentTime.withHour(16).withMinute(22).withSecond(0).withNano(0)

        val delay = Duration.between(currentTime, targetTime).toMillis().let {
            if (it < 0) it + 24 * 60 * 60 * 1000 else it
        }

//        val workRequest = PeriodicWorkRequestBuilder<DailyNotificationWorker>(1, TimeUnit.DAYS)
//            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
//            .build()

        val testWorkRequest = OneTimeWorkRequestBuilder<DailyNotificationWorker>()
            .setInitialDelay(10, TimeUnit.SECONDS)
            .build()

        Log.e(">>>>", "delay $delay currentTime $currentTime targetTime $targetTime")

        WorkManager.getInstance(context).enqueue(testWorkRequest)

//        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
//            "DailyNotification",
//            ExistingPeriodicWorkPolicy.UPDATE,
//            testWorkRequest
//        )
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}