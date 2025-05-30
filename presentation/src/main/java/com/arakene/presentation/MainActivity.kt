package com.arakene.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.arakene.domain.responses.DailyQuoteDto
import com.arakene.presentation.ui.home.TypingQuoteView
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.Screens
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val navController = rememberNavController()

            val currentDestination by navController.currentBackStackEntryAsState()

            val notAllowList = remember {
                listOf(Screens.Login::class.qualifiedName)
            }

            val displayBottomBar by remember(currentDestination) {
                mutableStateOf(
                    notAllowList.none {
                        it == (currentDestination?.destination?.route ?: "")
                    }
                )
            }

            FillsaTheme {
                TypingQuoteView(
                    DailyQuoteDto(
                        quote = "Live as if you were to die tomorrow."
                    )
                )

//                Scaffold(
//                    bottomBar = {
//                        if (displayBottomBar) {
//                            BottomNavigationBar(navController)
//                        }
//                    }
//                ) { paddingValues ->
//                    NavHost(
//                        modifier = Modifier.padding(paddingValues),
//                        navController = navController,
//                        startDestination = Screens.Login
//                    ) {
//
//                        composable<Screens.Login> {
//                            LoginView(
//                                navigate = {
//                                    navController.navigate(it)
//                                }
//                            )
//                        }
//
//                        composable<Screens.Home> {
//                            HomeView()
//                        }
//                    }
//                }
            }
        }
    }
}