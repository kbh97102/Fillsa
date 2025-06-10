package com.arakene.presentation.ui.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme

@Composable
fun AlertSwitchSection(
    selected: Boolean,
    setSelected: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {

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
                text = stringResource(R.string.alarm_title),
                style = FillsaTheme.typography.subtitle1,
                color = colorResource(R.color.gray_700)
            )
            Text(
                text = stringResource(R.string.alarm_description),
                style = FillsaTheme.typography.body3,
                color = colorResource(R.color.gray_700)
            )
        }

        Switch(
            checked = selected,
            onCheckedChange = setSelected,
            colors = SwitchDefaults.colors(
                checkedTrackColor = MaterialTheme.colorScheme.tertiary,
                uncheckedTrackColor = colorResource(R.color.gray_bd),
                uncheckedThumbColor = Color.White,
                checkedThumbColor = Color.White,
                uncheckedBorderColor = Color.Transparent,
                checkedBorderColor = Color.Transparent
            ),
            thumbContent = {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(Color.White)
                        .clip(CircleShape)
                )
            }
        )

    }

}

@Preview
@Composable
private fun AlertSwitchSectionPreview() {
    FillsaTheme {
        AlertSwitchSection(
            selected = true,
            setSelected = {}
        )
    }
}

@Preview(name = "false")
@Composable
private fun AlertSwitchSectionPreview2() {
    FillsaTheme {
        AlertSwitchSection(
            selected = false,
            setSelected = {}
        )
    }
}