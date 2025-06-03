package com.arakene.presentation.ui.quotelist

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import com.arakene.domain.responses.MemberQuotesResponse
import com.arakene.presentation.util.noEffectClickable

@Composable
fun QuoteListSection(
    list: LazyPagingItems<MemberQuotesResponse>,
    onClick: (MemberQuotesResponse) -> Unit,
    modifier: Modifier = Modifier
) {

    LazyColumn(modifier = modifier) {

        items(list.itemCount) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                val firstIndex = it * 2
                val firstItem = list[firstIndex]
                val secondItem = if (firstIndex + 1 < list.itemCount) list[firstIndex + 1] else null

                firstItem?.let { first ->
                    QuoteListItem(
                        data = first,
                        modifier = Modifier.weight(1f)
                            .noEffectClickable {
                                onClick(first)
                            }
                    )
                }

                secondItem?.let { second ->
                    QuoteListItem(
                        data = second,
                        modifier = Modifier.weight(1f)
                            .noEffectClickable {
                                onClick(second)
                            }
                    )
                } ?: run {
                    Spacer(Modifier.weight(1f))
                }

            }
        }

    }

}