package com.arakene.presentation.ui.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.arakene.domain.responses.NoticeResponse
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme

@Composable
fun NoticeDetailView(
    noticeResponse: NoticeResponse,
    modifier: Modifier = Modifier
) {

    Column(modifier = modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.primary)) {

        Text(
            noticeResponse.title,
            style = FillsaTheme.typography.subtitle1,
            color = colorResource(R.color.gray_700),
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            noticeResponse.createdAt,
            style = FillsaTheme.typography.body3,
            color = colorResource(R.color.gray_400),
            modifier = Modifier.padding(top = 10.dp)
        )

        HorizontalDivider(
            color = colorResource(R.color.gray_200),
            modifier = Modifier.padding(top = 10.dp)
        )

        Text(
            noticeResponse.content,
            style = FillsaTheme.typography.body3,
            color = colorResource(R.color.gray_700),
            modifier = Modifier.padding(top = 20.dp)
        )

    }

}