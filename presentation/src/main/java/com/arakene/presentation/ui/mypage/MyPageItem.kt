package com.arakene.presentation.ui.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import com.arakene.presentation.util.IsDarkMode
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.noEffectClickable

@Composable
fun MyPageItem(
    image: Painter,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    useArrow: Boolean = true,
    darkMode: Boolean = IsDarkMode.current
) {

    Row(
        modifier = modifier
            .noEffectClickable { onClick() }
            .fillMaxWidth()
            .background(
                FillsaTheme.colorScheme.backgroundContainer,
                shape = MaterialTheme.shapes.medium
            )
            .border(
                1.dp, color = if (darkMode) {
                    colorResource(R.color.gray_500)
                } else {
                    colorResource(R.color.purple02)
                }, shape = MaterialTheme.shapes.medium
            )
            .padding(vertical = 18.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            painter = image, contentDescription = null
        )

        Text(
            text = text,
            style = FillsaTheme.typography.subtitle1,
            color = FillsaTheme.colorScheme.onBackground1,
            modifier = Modifier.padding(start = 8.dp)
        )

        Spacer(Modifier.weight(1f))

        if (useArrow) {
            Image(
                painter = painterResource(R.drawable.icn_arrow),
                contentDescription = null,
                colorFilter = ColorFilter.tint(
                    FillsaTheme.colorScheme.onBackground1
                ),
                modifier = Modifier
                    .size(24.dp)
                    .rotate(180f)
            )
        }
    }

}

@Preview
@Composable
private fun MyPageItemPreview() {
    FillsaTheme {
        MyPageItem(
            image = painterResource(R.drawable.icn_info),
            text = "공지사항",
            onClick = {}
        )
    }

}