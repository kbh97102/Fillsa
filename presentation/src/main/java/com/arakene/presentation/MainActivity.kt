package com.arakene.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.arakene.presentation.ui.BottomNavigationBar
import com.arakene.presentation.ui.common.CircleLoadingSpinner
import com.arakene.presentation.ui.common.DialogSection
import com.arakene.presentation.ui.common.MainNavHost
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.AlarmManagerHelper
import com.arakene.presentation.util.DialogDataHolder
import com.arakene.presentation.util.LocalDialogDataHolder
import com.arakene.presentation.util.LocalLoadingState
import com.arakene.presentation.util.LocalSnackbarHost
import com.arakene.presentation.util.Screens
import com.arakene.presentation.util.SnackbarContent
import com.arakene.presentation.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: SplashViewModel by viewModels()

    @Inject
    lateinit var alarmManagerHelper: AlarmManagerHelper

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && result.containsKey(Manifest.permission.POST_NOTIFICATIONS)) {
            viewModel.setAlarmUsage(result[Manifest.permission.POST_NOTIFICATIONS] == true)

            alarmManagerHelper.setAlarm()
        } else {
            alarmManagerHelper.cancelAlarm()
        }


        viewModel.permissionChecked.value = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplash()

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        checkPermission()

        viewModel.checkReady()

        setContent {

            val snackbarHostState = remember { SnackbarHostState() }

            val navController = rememberNavController()

            val logoutEvent = remember {
                {
                    viewModel.clearToken()
                }
            }

            val currentDestination by navController.currentBackStackEntryAsState()

            val displayBottomBar by remember(currentDestination) {
                mutableStateOf(
                    shouldShowBottomBar(currentDestination?.destination?.route)
                )
            }

            val globalLoadingState = remember { MutableStateFlow(false) }

            val loadingState by globalLoadingState.collectAsState()

            val dialogData = remember {
                DialogDataHolder()
            }

            val isLogged by viewModel.isLogged.collectAsState(false)

            FillsaTheme {
                val ready by viewModel.ready.collectAsState()

                val startDestination by remember {
                    viewModel.destination
                }

                CompositionLocalProvider(
                    LocalSnackbarHost provides snackbarHostState,
                    LocalDialogDataHolder provides dialogData,
                    LocalLoadingState provides globalLoadingState
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Scaffold(
                            snackbarHost = {
                                SnackbarHost(snackbarHostState) {
                                    SnackbarContent(message = it.visuals.message)
                                }
                            },
                            bottomBar = {
                                if (displayBottomBar) {
                                    BottomNavigationBar(
                                        isLogged = isLogged,
                                        navController = navController
                                    )
                                }
                            }
                        ) { paddingValues ->
                            DialogSection(dialogData)

                            if (ready) {
                                MainNavHost(
                                    modifier = Modifier.padding(paddingValues),
                                    navController = navController,
                                    startDestination = startDestination,
                                    logoutEvent = logoutEvent
                                )
                            }
                        }

                        CircleLoadingSpinner(
                            isLoading = loadingState
                        )
                    }
                }
            }
        }
    }

    private fun installSplash() {
        installSplashScreen()
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    setOnExitAnimationListener {
                        lifecycleScope.launch {
                            delay(1000)
                            val isReady = viewModel.waitUntilReady()

                            if (isReady) {
                                it.remove()
                            }
                        }
                    }
                } else {
                    setKeepOnScreenCondition {
                        !viewModel.ready.value
                    }
                }
            }
    }

    private fun getImagePermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.POST_NOTIFICATIONS)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun checkPermission() {
        val permissions = getImagePermissions()
        val notGranted = permissions.any {
            checkSelfPermission(it) == PackageManager.PERMISSION_DENIED
        }

        if (notGranted) {
            permissionLauncher.launch(permissions)
        } else {
            viewModel.permissionChecked.value = true
        }
    }

    private fun shouldShowBottomBar(route: String?): Boolean {

        return route?.substringBefore("?") in setOf(
            Screens.Home::class.qualifiedName,
            Screens.QuoteList::class.qualifiedName,
            Screens.Calendar::class.qualifiedName,
            Screens.MyPage::class.qualifiedName
        )
    }
}