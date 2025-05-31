package com.arakene.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.arakene.domain.responses.DailyQuoteDto
import com.arakene.presentation.ui.BottomNavigationBar
import com.arakene.presentation.ui.LoginView
import com.arakene.presentation.ui.home.HomeView
import com.arakene.presentation.ui.home.TypingQuoteView
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.DailyQuoteDtoTypeMap
import com.arakene.presentation.util.Screens
import com.arakene.presentation.util.logDebug
import dagger.hilt.android.AndroidEntryPoint
import kotlin.reflect.typeOf

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val navController = rememberNavController()

            val currentDestination by navController.currentBackStackEntryAsState()

            val displayBottomBar by remember(currentDestination) {
                mutableStateOf(
                    shouldShowBottomBar(currentDestination?.destination?.route)
                )
            }

            FillsaTheme {
                Scaffold(
                    bottomBar = {
                        logDebug("bottomBar $displayBottomBar")
                        if (displayBottomBar) {
                            BottomNavigationBar(navController)
                        }
                    }
                ) { paddingValues ->
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
                                data.dailyQuoteDto
                            )
                        }
                    }
                }
            }
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