package com.arakene.presentation.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.arakene.presentation.R
import com.arakene.presentation.util.Screens

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        Pair(Screens.Home, R.drawable.icn_bottom_menu_home),
        Pair(Screens.List, R.drawable.icn_bottom_menu_list),
        Pair(Screens.Calendar, R.drawable.icn_bottom_menu_calendar),
        Pair(Screens.MyPage, R.drawable.icn_bottom_menu_my_page),
    )



    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        val black = colorResource(R.color.gray_700)
        val purple = colorResource(R.color.purple01)

        items.forEach { item ->
            val routeString = remember { item.first::class.qualifiedName }
            NavigationBarItem(
                selected = currentRoute == routeString,
                onClick = {
                    if (currentRoute != routeString) {
                        navController.navigate(item.first) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(painterResource(item.second), contentDescription = null) },
                label = { Text(item.first.routeString) },
                colors = NavigationBarItemColors(
                    selectedIconColor = purple,
                    selectedTextColor = purple,
                    selectedIndicatorColor = Color.Transparent,
                    unselectedIconColor = black,
                    unselectedTextColor = black,
                    disabledIconColor = black,
                    disabledTextColor = black
                )
            )
        }
    }


}