package com.arakene.presentation.ui.mypage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.arakene.domain.responses.NoticeResponse
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.noEffectClickable

@Composable
fun NoticeListSection(
    data: LazyPagingItems<NoticeResponse>,
    onClick: (NoticeResponse) -> Unit,
    modifier: Modifier = Modifier
) {

    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {

        items(data.itemCount, key = {
            "${data[it].hashCode()}_$it"
        }) {
            data[it]?.let { it1 ->
                NoticeItem(
                    noticeResponse = it1,
                    modifier = Modifier.noEffectClickable { onClick(it1) }
                )
            }
        }
    }
}

@Composable
fun NoticeItem(
    noticeResponse: NoticeResponse,
    modifier: Modifier = Modifier
) {

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            noticeResponse.createdAt,
            style = FillsaTheme.typography.body3,
            color = colorResource(R.color.gray_400)
        )

        Text(
            noticeResponse.title,
            style = FillsaTheme.typography.body3,
            color = colorResource(R.color.gray_700),
        )

        HorizontalDivider(color = colorResource(R.color.gray_200))
    }

}