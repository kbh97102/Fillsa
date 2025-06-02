package com.arakene.presentation.ui.quotelist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.arakene.presentation.viewmodel.ListViewModel

@Composable
fun QuoteListView(
    startDate: String,
    endDate: String,
    viewModel: ListViewModel = hiltViewModel()
) {

    var isLike by remember {
        mutableStateOf(false)
    }

    val paging = viewModel.getQuotesList(isLike).collectAsLazyPagingItems()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {

        DateSelectSection(
            startDate = startDate,
            endDate = endDate,
            modifier = Modifier.padding(top = 20.dp)
        )

        IsLikeSection(
            isLike = isLike,
            setIsLike = {
                isLike = it
            }
        )

        QuoteListSection(
            list = paging
        )

    }


}