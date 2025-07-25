package com.arakene.presentation.ui.quotelist

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.arakene.presentation.ui.home.HomeTopSection
import com.arakene.presentation.util.CommonEffect
import com.arakene.presentation.util.HandlePagingError
import com.arakene.presentation.util.HandleViewEffect
import com.arakene.presentation.util.Navigate
import com.arakene.presentation.util.QuoteListAction
import com.arakene.presentation.viewmodel.ListViewModel

@Composable
fun QuoteListView(
    startDate: String,
    endDate: String,
    navigate: Navigate,
    popBackStack: () -> Unit,
    navController: NavController,
    viewModel: ListViewModel = hiltViewModel()
) {

    val isLike by viewModel.likeFilter.collectAsState()

    val paging = viewModel.quotesFlow.collectAsLazyPagingItems()


    HandlePagingError(paging, refresh = {
        paging.refresh()
    })

    val lifeCycle = LocalLifecycleOwner.current

    LaunchedEffect(navController.currentBackStackEntry) {
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Boolean>("memo_updated")
            ?.observe(lifeCycle) { updated ->
                if (updated == true) {
                    paging.refresh()
                }
            }
    }

    BackHandler {
        popBackStack()
    }

    HandleViewEffect(
        viewModel.effect,
        lifeCycle
    ) {
        when (it) {
            is CommonEffect.Move -> {
                navigate(it.screen)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 20.dp)
    ) {

        HomeTopSection(
            navigate = navigate
        )

        /**
         * Ver 2.0에서 도입될 기능
         */
//        DateSelectSection(
//            startDate = startDate,
//            endDate = endDate,
//            modifier = Modifier.padding(top = 20.dp)
//        )

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            IsLikeSection(
                modifier = Modifier.padding(top = 20.dp),
                isLike = isLike,
                setIsLike = {
                    viewModel.updateLikeFilter(it)
                }
            )
        }

        if (paging.itemCount == 0) {
            QuoteListEmptySection()
        } else {

            QuoteListSection(
                modifier = Modifier.padding(top = 10.dp),
                list = paging,
                onClick = {
                    viewModel.handleContract(QuoteListAction.ClickItem(it))
                }
            )
        }

    }


}