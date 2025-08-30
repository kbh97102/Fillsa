package com.arakene.presentation.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.arakene.domain.responses.DailyQuoteDto
import com.arakene.domain.responses.NoticeResponse
import com.arakene.presentation.ui.LoginView
import com.arakene.presentation.ui.calendar.CalendarView
import com.arakene.presentation.ui.home.HomeView
import com.arakene.presentation.ui.home.ShareView
import com.arakene.presentation.ui.home.TypingQuoteView
import com.arakene.presentation.ui.mypage.AlertView
import com.arakene.presentation.ui.mypage.MyPageView
import com.arakene.presentation.ui.mypage.NoticeDetailView
import com.arakene.presentation.ui.mypage.NoticeView
import com.arakene.presentation.ui.quoteli.QuoteDetailView
import com.arakene.presentation.ui.quotelist.MemoInsertView
import com.arakene.presentation.ui.quotelist.QuoteListView
import com.arakene.presentation.util.DailyQuoteDtoTypeMap
import com.arakene.presentation.util.DataKey
import com.arakene.presentation.util.MyPageScreens
import com.arakene.presentation.util.NoticeResponseTypeMap
import com.arakene.presentation.util.Screens
import com.arakene.presentation.viewmodel.CalendarViewModel
import com.arakene.presentation.viewmodel.HomeViewModel
import com.arakene.presentation.viewmodel.ListViewModel
import com.arakene.presentation.viewmodel.LoginViewModel
import com.arakene.presentation.viewmodel.MyPageViewModel
import com.arakene.presentation.viewmodel.TypingViewModel
import java.time.LocalDate
import kotlin.reflect.typeOf

@Composable
fun MainNavHost(
    navController: NavHostController,
    startDestination: Screens,
    logoutEvent: () -> Unit,
    modifier: Modifier = Modifier
) {

    val updatedLogoutEvent by rememberUpdatedState(logoutEvent)

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
    ) {

        composable<Screens.Splash> {
            SplashView(
                navigate = {
                    navController.navigate(it)
                }
            )
        }

        composable<Screens.Login> {

            val data = it.toRoute<Screens.Login>()

            WithBaseErrorHandling<LoginViewModel>(logoutEvent = updatedLogoutEvent) {
                LoginView(
                    isOnboarding = data.isOnBoarding,
                    navigate = {
                        navController.navigate(it)
                    },
                    popBackStack = {
                        navController.popBackStack()
                    }
                )
            }
        }

        composable<Screens.OnBoardingGuide> {
            IntroduceView(
                navigate = {
                    navController.navigate(it)
                }
            )
        }

        composable<Screens.Home> {
            WithBaseErrorHandling<HomeViewModel>(logoutEvent = updatedLogoutEvent) {

                val data = it.toRoute<Screens.Home>()

                val requestDate =
                    if (data.targetMonth != 0 && data.targetYear != 0 && data.targetDay != 0) {
                        LocalDate.of(data.targetYear, data.targetMonth, data.targetDay)
                    } else {
                        null
                    }

                HomeView(
                    requestDate = requestDate,
                    navigate = {
                        if (navController.currentDestination?.route?.lowercase()
                                ?.contains(it.routeString) == true
                        ) {
                            return@HomeView
                        }
                        navController.navigate(it)
                    }
                )
            }
        }

        composable<Screens.DailyQuote>(
            typeMap = mapOf(
                typeOf<DailyQuoteDto>() to DailyQuoteDtoTypeMap
            )
        ) {
            WithBaseErrorHandling<TypingViewModel>(logoutEvent = updatedLogoutEvent) {
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
        }

        composable<Screens.Share> {
            val data = it.toRoute<Screens.Share>()
            ShareView(
                quote = data.quote,
                author = data.author,
                popBackStack = {
                    navController.popBackStack()
                }
            )
        }

        composable<Screens.QuoteList> {
            WithBaseErrorHandling<ListViewModel>(logoutEvent = updatedLogoutEvent) {
                QuoteListView(
                    navigate = {
                        navController.navigate(it)
                    },
                    popBackStack = {
                        navController.navigate(Screens.Home()) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    logoutEvent = logoutEvent,
                )
            }
        }

        composable<Screens.QuoteDetail> {
            WithBaseErrorHandling<ListViewModel>(logoutEvent = updatedLogoutEvent) {
                val data = it.toRoute<Screens.QuoteDetail>()

                val memo by remember {
                    derivedStateOf {
                        navController.currentBackStackEntry?.savedStateHandle?.get<String>(
                            DataKey.INSERTED_MEMO
                        ) ?: (data.memo ?: "")
                    }
                }

                QuoteDetailView(
                    memo = memo,
                    authorUrl = data.authorUrl,
                    korAuthor = data.korAuthor,
                    engAuthor = data.engAuthor,
                    korQuote = data.korQuote,
                    engQuote = data.engQuote,
                    memberQuoteSeq = data.memberQuoteSeq,
                    navigate = {
                        navController.navigate(it)
                    },
                    onBackPress = {
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "memo_updated",
                            true
                        )
                        navController.popBackStack()
                    },
                    imagePath = data.imagePath
                )
            }
        }

        composable<Screens.MemoInsert> {
            WithBaseErrorHandling<ListViewModel>(logoutEvent = updatedLogoutEvent) {
                val data = it.toRoute<Screens.MemoInsert>()

                MemoInsertView(
                    memberQuoteSeq = data.memberQuoteSeq,
                    savedMemo = data.savedMemo,
                    popBackStack = {
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            DataKey.INSERTED_MEMO,
                            it
                        )

                        navController.popBackStack()
                    }
                )
            }
        }

        composable<Screens.Calendar> {
            WithBaseErrorHandling<CalendarViewModel>(logoutEvent = updatedLogoutEvent) {
                CalendarView(
                    navigate = {
                        navController.navigate(it)
                    },
                    popBackStack = {
                        navController.navigate(Screens.Home()) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }

        composable<Screens.MyPage> {
            WithBaseErrorHandling<MyPageViewModel>(logoutEvent = updatedLogoutEvent) {
                MyPageView(
                    navigate = {
                        navController.navigate(it)
                    },
                    popBackStack = {
                        navController.navigate(Screens.Home()) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }

        composable<MyPageScreens.Notice> {
            WithBaseErrorHandling<MyPageViewModel>(logoutEvent = updatedLogoutEvent) {
                NoticeView(
                    onBackPress = {
                        navController.popBackStack()
                    },
                    navigate = {
                        navController.navigate(it)
                    }
                )
            }
        }

        composable<MyPageScreens.Alert> {
            WithBaseErrorHandling<MyPageViewModel>(logoutEvent = updatedLogoutEvent) {
                AlertView(
                    popBackStack = {
                        navController.popBackStack()
                    },
                    navigate = {
                        navController.navigate(it)
                    }
                )
            }
        }

        composable<MyPageScreens.NoticeDetail>(
            typeMap =
                mapOf(typeOf<NoticeResponse>() to NoticeResponseTypeMap)
        ) {
            val data = it.toRoute<MyPageScreens.NoticeDetail>()
            WithBaseErrorHandling<MyPageViewModel> {
                NoticeDetailView(
                    popBackStack = {
                        navController.popBackStack()
                    },
                    noticeResponse = data.noticeResponse
                )
            }
        }

    }
}