package com.arakene.presentation.ui.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arakene.presentation.R
import com.arakene.presentation.ui.common.HeaderSection
import com.arakene.presentation.ui.theme.FillsaTheme

@Composable
fun AlertView(modifier: Modifier = Modifier) {

    Column {
        HeaderSection(
            stringResource(R.string.alert),
            onBackPress = {}
        )


        AlertSwitchSection()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondary)
                .padding(horizontal = 20.dp, vertical = 19.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                stringResource(R.string.resign),
                style = FillsaTheme.typography.body2,
                color = colorResource(R.color.gray_700)
            )

            Image(painter = painterResource(R.drawable.icn_sign_out), contentDescription = null)

        }

    }

}