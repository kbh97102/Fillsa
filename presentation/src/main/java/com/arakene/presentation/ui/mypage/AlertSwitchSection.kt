package com.arakene.presentation.ui.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme

@Composable
fun AlertSwitchSection(modifier: Modifier = Modifier) {

    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary)
            .padding(horizontal = 20.dp)
    ) {

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 16.dp)
        ) {
            Text(
                "오늘의 필사 알림",
                style = FillsaTheme.typography.subtitle1,
                color = colorResource(R.color.gray_700)
            )
            Text(
                "매일 오전 9시에 새로운 문장 알림을 받을 수 있습니다.",
                style = FillsaTheme.typography.body3,
                color = colorResource(R.color.gray_700)
            )
        }

        Switch(
            checked = true,
            onCheckedChange = null,
            colors = SwitchDefaults.colors(
                checkedTrackColor = MaterialTheme.colorScheme.tertiary,
                uncheckedTrackColor = colorResource(R.color.gray_bd)
            )
        )

    }

}

@Preview
@Composable
private fun AlertSwitchSectionPreview() {
    AlertSwitchSection()
}