package com.arakene.presentation.ui.quotelist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.arakene.domain.responses.MemberQuotesResponse
import com.arakene.presentation.util.noEffectClickable

@Composable
fun QuoteListSection(
    imageUri: String,
    list: LazyPagingItems<MemberQuotesResponse>,
    onClick: (MemberQuotesResponse) -> Unit,
    modifier: Modifier = Modifier
) {

    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(20.dp)) {

        items(list.itemCount) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                val firstIndex = it * 2
                val firstItem = if (firstIndex < list.itemCount) {
                    list[firstIndex]
                } else null
                val secondItem = if (firstIndex + 1 < list.itemCount) list[firstIndex + 1] else null

                firstItem?.let { first ->
                    QuoteListItem(
                        data = first,
                        imagePath = imageUri,
                        modifier = Modifier
                            .weight(1f)
                            .noEffectClickable {
                                onClick(first)
                            }
                    )
                }

                secondItem?.let { second ->
                    QuoteListItem(
                        data = second,
                        imagePath = imageUri,
                        modifier = Modifier
                            .weight(1f)
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