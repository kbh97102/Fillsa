package com.arakene.presentation.ui.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.arakene.presentation.R
import com.arakene.presentation.ui.common.HeaderSection
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.viewmodel.MyPageViewModel

@Composable
fun NoticeView(
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyPageViewModel = hiltViewModel()
) {

    val data = viewModel.getNotice.collectAsLazyPagingItems()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 20.dp)
    ) {

        HeaderSection(
            text = stringResource(R.string.notice),
            onBackPress = onBackPress
        )

        if (data.itemCount == 0) {
            EmptyNoticeSection()
        } else {
            NoticeListSection(
                data
            )
        }

    }

}

@Preview
@Composable
private fun NoticeViewPreview() {
    FillsaTheme {
        NoticeView(
            onBackPress = {}
        )
    }
}