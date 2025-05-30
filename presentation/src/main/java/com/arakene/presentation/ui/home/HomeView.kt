package com.arakene.presentation.ui.home

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.ui.theme.ImageSection
import com.arakene.presentation.util.HomeAction
import com.arakene.presentation.util.LocaleType
import com.arakene.presentation.viewmodel.HomeViewModel

@Composable
fun HomeView(
    viewModel: HomeViewModel = hiltViewModel()
) {

    LaunchedEffect(Unit) {
        // TODO: 여기서 해야할까?
        viewModel.handleContract(HomeAction.Refresh)
    }

    val date by remember {
        viewModel.date
    }

    var selectedLocale by remember {
        mutableStateOf(LocaleType.KOR)
    }

    val quota by remember(viewModel.currentQuota, selectedLocale) {
        mutableStateOf(
            if (selectedLocale == LocaleType.KOR) {
                viewModel.currentQuota.korQuote ?: ""
            } else {
                viewModel.currentQuota.engQuote ?: ""
            }
        )
    }

    val author by remember(viewModel.currentQuota, selectedLocale) {
        mutableStateOf(
            if (selectedLocale == LocaleType.KOR) {
                viewModel.currentQuota.korAuthor ?: ""
            } else {
                viewModel.currentQuota.engAuthor ?: ""
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 20.dp)
    ) {

        HomeTopSection()

        Row(
            modifier = Modifier.padding(top = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            CalendarSection(
                date = date,
                modifier = Modifier.weight(1f)
            )

            ImageSection(
                isLogged = false,
                modifier = Modifier.weight(1f)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            LocaleSwitch(
                selected = selectedLocale,
                setSelected = {
                    selectedLocale = it
                },
                modifier = Modifier.padding(top = 20.dp)
            )
        }

        DailyQuotaSection(
            text = quota,
            author = author,
            next = {
                viewModel.handleContract(HomeAction.ClickNext)
            },
            before = {
                viewModel.handleContract(HomeAction.ClickBefore)
            },
            modifier = Modifier.padding(top = 20.dp)
        )

        HomeBottomSection(
            copy = {},
            share = {},
            isLike = false,
            setIsLike = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp)
        )
    }

}


@Preview
@Composable
private fun HomeViewPreview() {
    FillsaTheme {
        HomeView()
    }
}