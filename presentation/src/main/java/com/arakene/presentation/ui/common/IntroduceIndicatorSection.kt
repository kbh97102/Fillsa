package com.arakene.presentation.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme
import okhttp3.internal.wait

@Composable
fun IntroduceIndicatorSection(
    currentPage: Int,
    modifier: Modifier = Modifier,
    darkMode: Boolean = isSystemInDarkTheme()
) {

    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            repeat(3) {
                IntroduceIndicatorItem(
                    isCurrent = it <= currentPage
                )
            }

        }

        Text(
            modifier = Modifier.padding(top = 30.dp),
            text = stringResource(R.string.introduce_title),
            style = FillsaTheme.typography.heading4,
            color = if (darkMode){
                Color.White
            } else {
                Color.Black
            }
        )

    }

}


@Composable
private fun RowScope.IntroduceIndicatorItem(
    isCurrent: Boolean,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .height(5.dp)
            .weight(1f)
            .background(
                color = if (isCurrent) colorResource(R.color.purple01) else colorResource(R.color.gray_200),
                shape = RoundedCornerShape(100.dp)
            )
    )

}