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
import com.arakene.presentation.ui.BottomNavigationBar
import com.arakene.presentation.ui.LoginView
import com.arakene.presentation.ui.home.HomeView
import com.arakene.presentation.ui.home.TypingQuoteBodySection
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
                TypingQuoteBodySection(
                    quote = "상황을 가장 잘 활용하는 사람이 가장 좋은 상황을 맞는다."
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