package com.arakene.fillsa.widget.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arakene.fillsa.R
import com.arakene.presentation.ui.theme.FillsaTheme

@Composable
fun WidgetPreview4x2(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .sizeIn(minWidth = 245.dp, minHeight = 115.dp)
            .background(colorResource(com.arakene.presentation.R.color.primary), shape = RoundedCornerShape(10.dp))
            .padding(8.dp)

        ,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(com.arakene.presentation.R.drawable.icn_logo),
                    contentDescription = null
                )

                Text(
                    "오늘의 문장",
                    color = colorResource(com.arakene.presentation.R.color.gray_700),
                    fontSize = 12.sp
                )
            }



            Box(
                modifier = Modifier
                    .background(
                        color = colorResource(com.arakene.presentation.R.color.purple01),
                        shape = RoundedCornerShape(100.dp)
                    )
                    .padding(horizontal = 6.dp),
            ) {
                Text(
                    text = stringResource(R.string.write_continuation),
                    color = Color.White,
                    fontSize = 8.sp
                )
            }

        }

        Spacer(Modifier.height(11.dp))

        Text(
            "진정한 용기는 두려움 속에서도 행동하는 것이다.",
            textAlign = TextAlign.Center,
            color = colorResource(com.arakene.presentation.R.color.purple01)
        )
        Spacer(Modifier.height(5.dp))
        Text(
            "-넬슨 만델라",
        )
        Spacer(Modifier.height(38.dp))
    }
}


@Preview
@Composable
private fun WidgetPreview4x2Preview() {
    FillsaTheme {
        WidgetPreview4x2()
    }
}