package com.arakene.presentation.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.arakene.domain.responses.DailyQuoteDto
import com.arakene.presentation.ui.LoginView
import com.arakene.presentation.ui.calendar.CalendarView
import com.arakene.presentation.ui.home.HomeView
import com.arakene.presentation.ui.home.ShareView
import com.arakene.presentation.ui.home.TypingQuoteView
import com.arakene.presentation.ui.mypage.AlertView
import com.arakene.presentation.ui.mypage.MyPageView
import com.arakene.presentation.ui.mypage.NoticeView
import com.arakene.presentation.ui.quotelist.MemoInsertView
import com.arakene.presentation.ui.quotelist.QuoteDetailView
import com.arakene.presentation.ui.quotelist.QuoteListView
import com.arakene.presentation.util.DailyQuoteDtoTypeMap
import com.arakene.presentation.util.DataKey
import com.arakene.presentation.util.MyPageScreens
import com.arakene.presentation.util.Screens
import com.arakene.presentation.viewmodel.CalendarViewModel
import com.arakene.presentation.viewmodel.HomeViewModel
import com.arakene.presentation.viewmodel.ListViewModel
import com.arakene.presentation.viewmodel.LoginViewModel
import com.arakene.presentation.viewmodel.MyPageViewModel
import com.arakene.presentation.viewmodel.TypingViewModel
import kotlin.reflect.typeOf

@Composable
fun MainNavHost(
    navController: NavHostController,
    startDestination: Screens,
    modifier: Modifier = Modifier
) {

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
    ) {

        composable<Screens.Login> {
            WithBaseErrorHandling<LoginViewModel> {
                LoginView(
                    navigate = {
                        navController.navigate(it)
                    },
                    popBackStack = {
                        navController.popBackStack()
                    }
                )
            }
        }

        composable<Screens.Home> {
            WithBaseErrorHandling<HomeViewModel> {
                HomeView(
                    navigate = {
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
            WithBaseErrorHandling<TypingViewModel> {
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
                author = data.author
            )
        }

        composable<Screens.QuoteList> {
            WithBaseErrorHandling<ListViewModel>(
                content = {
                    QuoteListView(
                        startDate = "",
                        endDate = "",
                        navigate = {
                            navController.navigate(it)
                        }
                    )
                }
            )
        }

        composable<Screens.QuoteDetail> {
            WithBaseErrorHandling<ListViewModel> {
                val data = it.toRoute<Screens.QuoteDetail>()

                val insertedMemoInMemoInsertView =
                    navController.currentBackStackEntry?.savedStateHandle?.get<String>(
                        DataKey.INSERTED_MEMO
                    )

                val memo =
                    if (insertedMemoInMemoInsertView.isNullOrBlank()) {
                        data.memo ?: ""
                    } else {
                        insertedMemoInMemoInsertView
                    }

                QuoteDetailView(
                    memo = memo,
                    authorUrl = data.authorUrl,
                    author = data.author,
                    quote = data.quote,
                    memberQuoteSeq = data.memberQuoteSeq,
                    navigate = {
                        navController.navigate(it)
                    },
                    onBackPress = {
                        navController.popBackStack()
                    }
                )
            }
        }

        composable<Screens.MemoInsert> {
            WithBaseErrorHandling<ListViewModel> {
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
            WithBaseErrorHandling<CalendarViewModel> {
                CalendarView(
                    navigate = {
                        navController.navigate(it)
                    }
                )
            }
        }

        composable<Screens.MyPage> {
            WithBaseErrorHandling<MyPageViewModel> {
                MyPageView(
                    navigate = {
                        navController.navigate(it)
                    }
                )
            }
        }

        composable<MyPageScreens.Notice> {
            WithBaseErrorHandling<MyPageViewModel> {
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
            WithBaseErrorHandling<MyPageViewModel> {
                AlertView()
            }
        }

    }
}