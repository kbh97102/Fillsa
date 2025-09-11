package com.arakene.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.arakene.presentation.R
import com.arakene.presentation.ui.common.SingleLineAdSection
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.DialogDataHolder
import com.arakene.presentation.util.IsDarkMode
import com.arakene.presentation.util.LocalDialogDataHolder
import com.arakene.presentation.util.Screens

@Composable
fun BottomNavigationBar(
    isLogged: Boolean,
    displayAd: Boolean,
    displayBottomBar: Boolean,
    navController: NavHostController,
    dialogDataHolder: DialogDataHolder = LocalDialogDataHolder.current,
    darkMode: Boolean = IsDarkMode.current
) {
    val items = remember {
        listOf<Pair<Screens, Int>>(
            Pair(Screens.Home(), R.drawable.icn_bottom_menu_home),
            Pair(Screens.QuoteList(), R.drawable.icn_bottom_menu_list),
            Pair(Screens.Calendar, R.drawable.icn_bottom_menu_calendar),
            Pair(Screens.MyPage, R.drawable.icn_bottom_menu_my_page),
        )
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute by remember(navBackStackEntry?.destination?.route) {
        mutableStateOf(navBackStackEntry?.destination?.route?.substringBefore("?"))
    }


    Column(modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)) {
        if (displayBottomBar) {
            NavigationBar(
                containerColor = FillsaTheme.colorScheme.background
            ) {
                val unSelectedColor = remember {
                    if (darkMode) {
                        R.color.gray_400
                    } else {
                        R.color.gray_700
                    }
                }

                val selectedColor = if (darkMode) {
                    colorResource(R.color.white)
                } else {
                    colorResource(R.color.purple01)
                }


                items.forEach { item ->
                    val routeString = remember { item.first::class.qualifiedName }

                    NavigationBarItem(
                        selected = currentRoute == routeString,
                        onClick = {
                            if (currentRoute != routeString) {
                                navController.navigate(item.first) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        },
                        icon = { Icon(painterResource(item.second), contentDescription = null) },
                        label = { Text(item.first.routeString) },
                        colors = NavigationBarItemColors(
                            selectedIconColor = selectedColor,
                            selectedTextColor = selectedColor,
                            selectedIndicatorColor = Color.Transparent,
                            unselectedIconColor = colorResource(unSelectedColor),
                            unselectedTextColor = colorResource(unSelectedColor),
                            disabledIconColor = colorResource(unSelectedColor),
                            disabledTextColor = colorResource(unSelectedColor)
                        )
                    )
                }
            }
        }
        if (displayAd) {
            SingleLineAdSection(
                currentRoute = currentRoute ?: "",
                refresh = displayBottomBar,
            )
        }
    }
}


