package com.arakene.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.arakene.presentation.ui.BottomNavigationBar
import com.arakene.presentation.ui.common.CircleLoadingSpinner
import com.arakene.presentation.ui.common.DialogSection
import com.arakene.presentation.ui.common.MainNavHost
import com.arakene.presentation.ui.common.SingleLineAdSection
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
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: SplashViewModel by viewModels()

    @Inject
    lateinit var alarmManagerHelper: AlarmManagerHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplash()

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

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

            val shouldShowAd by viewModel.shouldShowAd.collectAsState()

            LaunchedEffect(currentDestination) {
                viewModel.updateAdVisibilityByRoute(currentDestination?.destination?.route)
            }



            FillsaTheme {
                CompositionLocalProvider(
                    LocalSnackbarHost provides snackbarHostState,
                    LocalDialogDataHolder provides dialogData,
                    LocalLoadingState provides globalLoadingState
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .imePadding()
                        ) {
                            Scaffold(
                                modifier = Modifier.weight(1f),
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
                                },
                                containerColor = Color.White,
                                contentWindowInsets = if (shouldShowAd) {
                                    WindowInsets.statusBars
                                } else {
                                    ScaffoldDefaults.contentWindowInsets
                                }
                            ) { paddingValues ->
                                DialogSection(dialogData)

                                MainNavHost(
                                    modifier = Modifier
                                        .padding(paddingValues),
                                    navController = navController,
                                    startDestination = Screens.Splash,
                                    logoutEvent = logoutEvent
                                )
                            }

                            // TODO 7월 21일 운영배포에는 광고 제거
                            if (shouldShowAd) {
                                SingleLineAdSection()
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