package com.arakene.fillsa.widget.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arakene.fillsa.R
import com.arakene.presentation.ui.theme.FillsaTheme


@Composable
fun WidgetPreview2x2(modifier: Modifier = Modifier) {

    Column(
        modifier = modifier
            .size(width = 109.dp, height = 115.dp)
            .background(
                color = colorResource(com.arakene.presentation.R.color.primary),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Spacer(Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .background(
                    color = colorResource(com.arakene.presentation.R.color.purple01),
                    shape = RoundedCornerShape(100.dp)
                )
                .padding(horizontal = 4.dp),
        ) {
            Text(
                text = stringResource(R.string.write_continuation),
                color = Color.White,
                style = FillsaTheme.typography.body4.copy(fontSize = 6.sp)
            )
        }

        Spacer(Modifier.height(5.dp))

        Text(
            stringResource(R.string.did_you_fotget_today_quote),
            color = colorResource(com.arakene.presentation.R.color.purple01),
            style = FillsaTheme.typography.buttonXSmallBold.copy(fontSize = 8.sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(Modifier.height(8.dp))

        Image(
            painter = painterResource(com.arakene.presentation.R.drawable.icn_logo),
            contentDescription = null
        )

    }
}


@Preview
@Composable
private fun WidgetPreview2x2Preview() {
    FillsaTheme {
        WidgetPreview2x2()
    }
}