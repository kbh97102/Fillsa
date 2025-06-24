package com.arakene.presentation.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.arakene.presentation.R
import com.arakene.presentation.util.DialogData
import com.arakene.presentation.util.DialogDataHolder
import com.arakene.presentation.util.LocalDialogDataHolder
import com.arakene.presentation.util.Screens
import com.arakene.presentation.util.logDebug

@Composable
fun BottomNavigationBar(
    isLogged: Boolean,
    navController: NavHostController,
    dialogDataHolder: DialogDataHolder = LocalDialogDataHolder.current
) {

    val items = remember {
        listOf<Pair<Screens, Int>>(
            Pair(Screens.Home(), R.drawable.icn_bottom_menu_home),
            Pair(Screens.QuoteList, R.drawable.icn_bottom_menu_list),
            Pair(Screens.Calendar, R.drawable.icn_bottom_menu_calendar),
            Pair(Screens.MyPage, R.drawable.icn_bottom_menu_my_page),
        )
    }

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute by remember(navBackStackEntry?.destination?.route) {
            mutableStateOf(navBackStackEntry?.destination?.route?.substringBefore("?"))
        }

        val black = colorResource(R.color.gray_700)
        val purple = colorResource(R.color.purple01)

        items.forEach { item ->
            val routeString = remember { item.first::class.qualifiedName }

            NavigationBarItem(
                selected = currentRoute == routeString,
                onClick = {
                    if (currentRoute != routeString) {

                        if (item.first.needLogin && !isLogged) {

                            dialogDataHolder.apply {
                                data = DialogData.Builder()
                                    .title("로그인 후 사용하실 수 있습니다.")
                                    .onClick {
                                        navController.navigate(Screens.Login())
                                    }
                                    .build()
                            }.run {
                                show = true
                            }

                            return@NavigationBarItem
                        }

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