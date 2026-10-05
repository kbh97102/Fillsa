package com.arakene.presentation.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.arakene.presentation.R
import com.arakene.presentation.ui.common.SingleLineAdSection
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.DialogDataHolder
import com.arakene.presentation.util.IsDarkMode
import com.arakene.presentation.util.LocalDialogDataHolder
import com.arakene.presentation.util.Screens
import com.arakene.presentation.util.noEffectClickable

internal data class MainBottomNavigationItem(
    val screen: Screens,
    @param:DrawableRes val iconRes: Int,
)

internal val mainBottomNavigationItems = listOf(
    MainBottomNavigationItem(Screens.Home(), R.drawable.icn_bottom_menu_home),
    MainBottomNavigationItem(Screens.Calendar, R.drawable.icn_bottom_menu_calendar),
    MainBottomNavigationItem(Screens.MyPage, R.drawable.icn_bottom_menu_my_page),
)

internal fun shouldShowMainBottomNavigation(route: String?): Boolean {
    val routeWithoutArguments = route?.substringBefore("?")
    return mainBottomNavigationItems.any {
        routeWithoutArguments == it.screen::class.qualifiedName
    }
}

@Composable
fun BottomNavigationBar(
    isLogged: Boolean,
    displayAd: Boolean,
    displayBottomBar: Boolean,
    noticeEmpty: Boolean = false,
    navController: NavHostController,
    navigationEnabled: Boolean = true,
    dialogDataHolder: DialogDataHolder = LocalDialogDataHolder.current,
    darkMode: Boolean = IsDarkMode.current
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute by remember(navBackStackEntry?.destination?.route) {
        mutableStateOf(navBackStackEntry?.destination?.route?.substringBefore("?"))
    }


    Column(modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)) {
        if (displayBottomBar) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(FillsaTheme.colorScheme.background)
            ) {
                val unSelectedColor = if (darkMode) {
                    colorResource(R.color.gray_400)
                } else {
                    colorResource(R.color.gray_700)
                }

                val selectedColor = if (darkMode) {
                    colorResource(R.color.white)
                } else {
                    colorResource(R.color.purple01)
                }

                mainBottomNavigationItems.forEach { item ->
                    val routeString = remember(item.screen) { item.screen::class.qualifiedName }
                    val selected = currentRoute == routeString
                    val itemColor = if (selected) selectedColor else unSelectedColor

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .semantics {
                                this.selected = selected
                                role = Role.Tab
                            }
                            .noEffectClickable(enable = navigationEnabled && !selected) {
                                navController.navigate(item.screen) {
                                    popUpTo(0) { inclusive = true }
                                }
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Spacer(Modifier.height(4.dp))
                        Icon(
                            painter = painterResource(item.iconRes),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = itemColor,
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = item.screen.routeString,
                            color = itemColor,
                            style = FillsaTheme.typography.body4,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
        if (displayAd) {
            SingleLineAdSection(
                currentRoute = currentRoute ?: "",
                refresh = displayBottomBar,
                noticeEmpty = noticeEmpty,
            )
        }
    }
}
