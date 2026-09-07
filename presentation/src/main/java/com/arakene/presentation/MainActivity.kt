package com.arakene.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.res.colorResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.arakene.domain.util.DarkModeType
import com.arakene.domain.responses.MemberStreakResponse
import com.arakene.presentation.ui.BottomNavigationBar
import com.arakene.presentation.ui.common.CircleLoadingSpinner
import com.arakene.presentation.ui.common.DialogSection
import com.arakene.presentation.ui.common.GeneralDialogs
import com.arakene.presentation.ui.common.MainNavHost
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.DialogDataHolder
import com.arakene.presentation.util.IsDarkMode
import com.arakene.presentation.util.LocalDialogDataHolder
import com.arakene.presentation.util.LocalHomeRuntimeQaFixture
import com.arakene.presentation.util.LocalLoadingState
import com.arakene.presentation.util.LocalMoveHolder
import com.arakene.presentation.util.LocalSnackbarHost
import com.arakene.presentation.util.Screens
import com.arakene.presentation.util.SnackbarContent
import com.arakene.presentation.util.StreakProvider
import com.arakene.presentation.util.HomeRuntimeQaFixture
import com.arakene.presentation.util.logError
import com.arakene.presentation.viewmodel.MainActivityViewModel
import com.arakene.presentation.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: SplashViewModel by viewModels()

    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplash()

        super.onCreate(savedInstanceState)

        val homeQaStreakOverride = if (BuildConfig.DEBUG && intent.hasExtra(EXTRA_HOME_QA_STREAK)) {
            intent.getIntExtra(EXTRA_HOME_QA_STREAK, 0)
        } else {
            null
        }
        val homeQaDarkModeOverride = if (BuildConfig.DEBUG && intent.hasExtra(EXTRA_HOME_QA_DARK_MODE)) {
            intent.getBooleanExtra(EXTRA_HOME_QA_DARK_MODE, false)
        } else {
            null
        }
        val homeQaHideAd = BuildConfig.DEBUG && intent.getBooleanExtra(EXTRA_HOME_QA_HIDE_AD, false)
        val homeQaShowRecordedAnswer = BuildConfig.DEBUG && intent.getBooleanExtra(
            EXTRA_HOME_QA_RECORDED_ANSWER,
            false
        )
        val homeQaShowSelectedImageState = BuildConfig.DEBUG && intent.getBooleanExtra(
            EXTRA_HOME_QA_SELECTED_IMAGE,
            false
        )
        val homeQaShowImageDialog = BuildConfig.DEBUG && intent.getBooleanExtra(
            EXTRA_HOME_QA_IMAGE_DIALOG,
            false
        )
        val homeQaShowTemplateDialog = BuildConfig.DEBUG && intent.getBooleanExtra(
            EXTRA_HOME_QA_TEMPLATE_DIALOG,
            false
        )
        val homeQaShowLoginDialog = BuildConfig.DEBUG && intent.getBooleanExtra(
            EXTRA_HOME_QA_LOGIN_DIALOG,
            false
        )
        val homeQaFixture = if (BuildConfig.DEBUG && intent.getBooleanExtra(EXTRA_HOME_QA_FIXTURE, false)) {
            HomeRuntimeQaFixture(
                quote = "사랑이라는 선물은 억지로 줄 수 없고 받아들여지기를 기다릴 뿐이다.",
                author = "존우든",
                showRecordedAnswer = homeQaShowRecordedAnswer,
                showSelectedImageState = homeQaShowSelectedImageState,
                showImageDialog = homeQaShowImageDialog,
                showTemplateDialog = homeQaShowTemplateDialog,
                showLoginDialog = homeQaShowLoginDialog,
            )
        } else {
            null
        }

        mainActivityViewModel.initWidgetData()
//        mainActivityViewModel.getPopupGeneral()


        enableEdgeToEdge()

        setContent {

            val darkModeType by mainActivityViewModel.getDarkModeType()
                .collectAsState(DarkModeType.SYSTEM)

            val systemDarkMode = isSystemInDarkTheme()

            val isDarkMode by remember(darkModeType, homeQaDarkModeOverride) {
                mutableStateOf(
                    homeQaDarkModeOverride ?: when (darkModeType) {
                        DarkModeType.DARK -> true
                        DarkModeType.LIGHT -> false
                        DarkModeType.SYSTEM -> systemDarkMode
                    },
                )
            }

            val streakCount by remember {
                mainActivityViewModel.streakCount
            }
            val providedStreak = homeQaStreakOverride?.let { streak ->
                MemberStreakResponse(currentStreak = streak, isTodayWritten = false)
            } ?: streakCount

            LaunchedEffect(streakCount) {
                logError("업데이트 되는거니? $streakCount")
            }

            val generalPopup by mainActivityViewModel.popupResponse.collectAsStateWithLifecycle(null)

            val snackbarHostState = remember { SnackbarHostState() }

            val navController = rememberNavController()

            val logoutEvent = remember {
                {
                    viewModel.clearToken()
                }
            }

            val currentDestination by navController.currentBackStackEntryAsState()

            val isOnboardingGuide = currentDestination?.destination?.route?.contains(
                Screens.OnBoardingGuide.routeString
            ) == true

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
            val displayAd = shouldShowAd && !homeQaHideAd

            LaunchedEffect(currentDestination) {
                viewModel.updateAdVisibilityByRoute(currentDestination?.destination?.route)
                mainActivityViewModel.updateStreakInfo(currentDestination?.destination?.route)
            }

            FillsaTheme(darkTheme = isDarkMode) {
                CompositionLocalProvider(
                    LocalSnackbarHost provides snackbarHostState,
                    LocalDialogDataHolder provides dialogData,
                    LocalLoadingState provides globalLoadingState,
                    LocalMoveHolder provides navController,
                    LocalHomeRuntimeQaFixture provides homeQaFixture,
                    IsDarkMode provides isDarkMode,
                    StreakProvider provides providedStreak
                ) {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        GeneralDialogs(
                            generalPopup,
                            getNextPopUp = mainActivityViewModel::getNextGeneralPopUp,
                            addHiddenPopUp = mainActivityViewModel::addHiddenPopUp
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .imePadding()
                        ) {
                            Scaffold(
                                modifier = Modifier.weight(1f),
                                snackbarHost = {
                                    SnackbarHost(snackbarHostState) {
                                        SnackbarContent(
                                            snackBarData = it.visuals
                                        )
                                    }
                                },
                                bottomBar = {
                                    BottomNavigationBar(
                                        isLogged = isLogged,
                                        navController = navController,
                                        displayAd = displayAd,
                                        displayBottomBar = displayBottomBar
                                    )
                                },
                                containerColor = when {
                                    isDarkMode -> colorResource(R.color.gray_700)
                                    isOnboardingGuide -> colorResource(R.color.white)
                                    currentDestination?.destination?.route?.contains("Splash") == true -> colorResource(R.color.white)
                                    else -> colorResource(R.color.primary)
                                },
                                contentWindowInsets = if (displayAd) {
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
                                    startDestination = if (homeQaFixture == null) Screens.Splash else Screens.Home(),
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
    }

    private fun shouldShowBottomBar(route: String?): Boolean {

        return route?.substringBefore("?") in setOf(
            Screens.Home::class.qualifiedName,
            Screens.QuoteList::class.qualifiedName,
            Screens.Calendar::class.qualifiedName,
            Screens.MyPage::class.qualifiedName
        )
    }

    private companion object {
        const val EXTRA_HOME_QA_STREAK = "com.arakene.fillsa.extra.HOME_QA_STREAK"
        const val EXTRA_HOME_QA_DARK_MODE = "com.arakene.fillsa.extra.HOME_QA_DARK_MODE"
        const val EXTRA_HOME_QA_HIDE_AD = "com.arakene.fillsa.extra.HOME_QA_HIDE_AD"
        const val EXTRA_HOME_QA_FIXTURE = "com.arakene.fillsa.extra.HOME_QA_FIXTURE"
        const val EXTRA_HOME_QA_RECORDED_ANSWER = "com.arakene.fillsa.extra.HOME_QA_RECORDED_ANSWER"
        const val EXTRA_HOME_QA_SELECTED_IMAGE = "com.arakene.fillsa.extra.HOME_QA_SELECTED_IMAGE"
        const val EXTRA_HOME_QA_IMAGE_DIALOG = "com.arakene.fillsa.extra.HOME_QA_IMAGE_DIALOG"
        const val EXTRA_HOME_QA_TEMPLATE_DIALOG = "com.arakene.fillsa.extra.HOME_QA_TEMPLATE_DIALOG"
        const val EXTRA_HOME_QA_LOGIN_DIALOG = "com.arakene.fillsa.extra.HOME_QA_LOGIN_DIALOG"
    }
}
