package com.arakene.presentation.ui.quotelist

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arakene.domain.responses.MemberQuotesResponse

@Composable
fun QuoteListSection(
    list: List<MemberQuotesResponse>,
    modifier: Modifier = Modifier
) {

    LazyColumn(modifier = modifier) {

        items(list.size) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                val firstIndex = it * 2
                val firstItem = list[firstIndex]
                val secondItem = if (firstIndex + 1 < list.size) list[firstIndex + 1] else null

                QuoteListItem(
                    data = firstItem,
                    modifier = Modifier.weight(1f)
                )

                secondItem?.let { second ->
                    QuoteListItem(
                        data = second,
                        modifier = Modifier.weight(1f)
                    )
                } ?: let {
                    Spacer(Modifier.weight(1f))
                }

            }
        }

    }

}