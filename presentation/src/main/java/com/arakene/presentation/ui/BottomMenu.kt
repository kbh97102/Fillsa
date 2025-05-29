package com.arakene.presentation.ui

import android.util.Log
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.arakene.presentation.R

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        Pair("home", R.drawable.icn_copy),
        Pair("list", R.drawable.icn_copy),
        Pair("calendar", R.drawable.icn_copy),
        Pair("my page", R.drawable.icn_copy),
    )



    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == "item.route",
                onClick = {

                    Log.e(">>>>", "route? ${currentRoute} item ${item}")

                    if (currentRoute != "item.route") {
//                        navController.navigate(item.route) {
//                            popUpTo(navController.graph.startDestinationId) { saveState = true }
//                            launchSingleTop = true
//                            restoreState = true
//                        }
                    }
                },
                icon = { Icon(painterResource(item.second), contentDescription = null) },
                label = { Text("label") }
            )
        }
    }


}