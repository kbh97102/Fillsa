package com.arakene.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.arakene.presentation.ui.LoginView
import com.arakene.presentation.ui.home.HomeView
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.Screens
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val navController = rememberNavController()

            FillsaTheme {

                NavHost(
                    navController = navController,
                    startDestination = Screens.Login
                ) {

                    composable<Screens.Login> {
                        LoginView(
                            navigate = {
                                navController.navigate(it)
                            }
                        )
                    }

                    composable<Screens.Home> {
                        HomeView()
                    }

                }
            }
        }
    }
}