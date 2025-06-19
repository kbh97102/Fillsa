package com.arakene.presentation.ui.quotelist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.arakene.domain.responses.MemberQuotesResponse
import com.arakene.presentation.util.noEffectClickable

@Composable
fun QuoteListSection(
    list: LazyPagingItems<MemberQuotesResponse>,
    onClick: (MemberQuotesResponse) -> Unit,
    modifier: Modifier = Modifier
) {

    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(list.itemCount) {
            list[it]?.let { it1 ->
                QuoteListItem(
                    data = it1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(150f / 162f)
                        .noEffectClickable {
                            onClick(it1)
                        }
                )
            }
        }
    }

}