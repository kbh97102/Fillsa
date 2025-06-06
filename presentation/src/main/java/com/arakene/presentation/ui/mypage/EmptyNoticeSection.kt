package com.arakene.presentation.ui.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme

@Composable
fun EmptyNoticeSection(modifier: Modifier = Modifier) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {


        Image(painter = painterResource(R.drawable.icn_empty_notice), contentDescription = null)

        Text(
            stringResource(R.string.empty_notice),
            style = FillsaTheme.typography.heading4,
            color = colorResource(R.color.gray_700),
            modifier = Modifier.padding(top = 20.dp)
        )


    }

}