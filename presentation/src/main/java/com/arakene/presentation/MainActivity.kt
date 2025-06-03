package com.arakene.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.arakene.domain.responses.DailyQuoteDto
import com.arakene.presentation.ui.BottomNavigationBar
import com.arakene.presentation.ui.LoginView
import com.arakene.presentation.ui.common.CommonDialog
import com.arakene.presentation.ui.home.HomeView
import com.arakene.presentation.ui.home.ShareView
import com.arakene.presentation.ui.home.TypingQuoteView
import com.arakene.presentation.ui.quotelist.MemoInsertView
import com.arakene.presentation.ui.quotelist.QuoteDetailView
import com.arakene.presentation.ui.quotelist.QuoteListView
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.DailyQuoteDtoTypeMap
import com.arakene.presentation.util.DialogDataHolder
import com.arakene.presentation.util.LocalDialogDataHolder
import com.arakene.presentation.util.LocalSnackbarHost
import com.arakene.presentation.util.Screens
import com.arakene.presentation.util.SnackbarContent
import com.arakene.presentation.util.logDebug
import dagger.hilt.android.AndroidEntryPoint
import kotlin.reflect.typeOf

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var keepSplash by mutableStateOf(true)

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
//        val granted = result.values.all { it }
        keepSplash = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { keepSplash }

        checkPermission()

        super.onCreate(savedInstanceState)
        setContent {

            val snackbarHostState = remember { SnackbarHostState() }

            val navController = rememberNavController()

            val currentDestination by navController.currentBackStackEntryAsState()

            val displayBottomBar by remember(currentDestination) {
                mutableStateOf(
                    shouldShowBottomBar(currentDestination?.destination?.route)
                )
            }

            val dialogData = remember {
                DialogDataHolder()
            }

            FillsaTheme {

                CompositionLocalProvider(
                    LocalSnackbarHost provides snackbarHostState,
                    LocalDialogDataHolder provides dialogData
                ) {
                    Scaffold(
                        snackbarHost = {
                            SnackbarHost(snackbarHostState) {
                                SnackbarContent(message = it.visuals.message)
                            }
                        },
                        bottomBar = {
                            logDebug("bottomBar $displayBottomBar")
                            if (displayBottomBar) {
                                BottomNavigationBar(navController)
                            }
                        }
                    ) { paddingValues ->

                        if (dialogData.show) {
                            CommonDialog(
                                title = dialogData.data?.title ?: "",
                                positiveOnClick = dialogData.data?.onClick ?: {},
                                dismiss = {
                                    dialogData.show = false
                                }
                            )
                        }

                        NavHost(
                            modifier = Modifier.padding(paddingValues),
                            navController = navController,
                            startDestination = Screens.Login,
                        ) {

                            composable<Screens.Login> {
                                LoginView(
                                    navigate = {
                                        navController.navigate(it)
                                    }
                                )
                            }

                            composable<Screens.Home> {
                                HomeView(
                                    navigate = {
                                        navController.navigate(it)
                                    }
                                )
                            }

                            composable<Screens.DailyQuote>(
                                typeMap = mapOf(
                                    typeOf<DailyQuoteDto>() to DailyQuoteDtoTypeMap
                                )
                            ) {
                                val data = it.toRoute<Screens.DailyQuote>()
                                TypingQuoteView(
                                    data.dailyQuoteDto,
                                    navigate = {
                                        navController.navigate(it)
                                    },
                                    backOnClick = {
                                        navController.popBackStack()
                                    }
                                )
                            }

                            composable<Screens.Share> {
                                val data = it.toRoute<Screens.Share>()
                                ShareView(
                                    quote = data.quote,
                                    author = data.author
                                )
                            }

                            composable<Screens.QuoteList> {
                                QuoteListView(
                                    startDate = "",
                                    endDate = "",
                                    navigate = {
                                        navController.navigate(it)
                                    }
                                )
                            }

                            composable<Screens.QuoteDetail> {
                                val data = it.toRoute<Screens.QuoteDetail>()
                                QuoteDetailView(
                                    memo = data.memo,
                                    authorUrl = data.authorUrl,
                                    author = data.author,
                                    quote = data.quote,
                                    memberQuoteSeq = data.memberQuoteSeq
                                )
                            }

                            composable<Screens.MemoInsert> {
                                MemoInsertView(
                                    memberQuoteSeq = ""
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getImagePermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
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
            keepSplash = false
        }
    }

    private fun shouldShowBottomBar(route: String?): Boolean {
        return route?.substringBefore("/") in setOf(
            Screens.Home::class.qualifiedName,
            Screens.List::class.qualifiedName,
            Screens.Calendar::class.qualifiedName,
            Screens.MyPage::class.qualifiedName
        )
    }
}