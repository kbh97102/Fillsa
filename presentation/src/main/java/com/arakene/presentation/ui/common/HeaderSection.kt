package com.arakene.presentation.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.noEffectClickable

@Composable
fun HeaderSection(
    text: String,
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth().padding(vertical = 13.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.icn_arrow),
            contentDescription = null,
            modifier = Modifier.noEffectClickable { onBackPress() })

        Text(
            text,
            style = FillsaTheme.typography.heading4,
            color = colorResource(R.color.gray_700),
            modifier = Modifier.padding(start = 10.dp)
        )
    }

}