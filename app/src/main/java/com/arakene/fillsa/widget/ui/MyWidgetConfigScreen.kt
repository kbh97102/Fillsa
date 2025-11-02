package com.arakene.fillsa.widget.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme


@Composable
fun MyWidgetConfigScreen(

) {

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painterResource(R.drawable.icn_arrow_black),
                contentDescription = null
            )

            Spacer(Modifier.width(10.dp))

            Text(
                stringResource(com.arakene.fillsa.R.string.widget),
                style = FillsaTheme.typography.buttonLargeBold
            )
        }

        HorizontalDivider(color = FillsaTheme.colorScheme.outlineVariant)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                stringResource(com.arakene.fillsa.R.string.widget_example_view),
                style = FillsaTheme.typography.body2,
                color = colorResource(
                    R.color.gray_700
                )
            )

            Image(painterResource(R.drawable.icn_arrow_down_black), contentDescription = null)
        }

        HorizontalDivider(color = FillsaTheme.colorScheme.outlineVariant)

        Spacer(Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            WidgetPreview2x2()
            Spacer(Modifier.height(5.dp))
            Text("비율(2x2)", style = FillsaTheme.typography.body3, color = Color.Black)

            Spacer(Modifier.height(20.dp))

            WidgetPreview4x2()
            Spacer(Modifier.height(5.dp))
            Text("비율(4x2)", style = FillsaTheme.typography.body3, color = Color.Black)
        }

        Spacer(Modifier.height(20.dp))

        HorizontalDivider(color = FillsaTheme.colorScheme.outlineVariant)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                "언어설정",
                style = FillsaTheme.typography.body2,
                color = colorResource(
                    R.color.gray_700
                )
            )

            Image(
                painterResource(R.drawable.icn_arrow_black),
                contentDescription = null,
                modifier = Modifier.rotate(180f)
            )
        }

        HorizontalDivider(color = FillsaTheme.colorScheme.outlineVariant)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                "텍스트 크기",
                style = FillsaTheme.typography.body2,
                color = colorResource(
                    R.color.gray_700
                )
            )

            Image(
                painterResource(R.drawable.icn_arrow_black),
                contentDescription = null,
                modifier = Modifier.rotate(180f)
            )
        }

        HorizontalDivider(color = FillsaTheme.colorScheme.outlineVariant)


        Spacer(Modifier.weight(1f))

        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .background(
                    color = FillsaTheme.colorScheme.onSecondaryContainer2,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(vertical = 15.dp)
        ) {
            Text(
                "확인",
                color = Color.White,
                style = FillsaTheme.typography.buttonMediumBold,
                modifier = Modifier.align(
                    Alignment.Center
                )
            )
        }

        Spacer(Modifier.height(20.dp))

    }


}

@Preview(showBackground = true)
@Composable
private fun ConfigPreview() {
    FillsaTheme {
        MyWidgetConfigScreen()
    }
}