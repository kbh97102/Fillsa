package com.arakene.presentation.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arakene.presentation.R
import com.arakene.presentation.util.noEffectClickable

@Composable
fun InteractionButtonSection(

    copy: () -> Unit,
    share: () -> Unit,
    isLike: Boolean,
    setIsLike: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    darkMode: Boolean = isSystemInDarkTheme(),
    darkModeColor: Int = R.color.purple01,
    lightModeColor: Int = R.color.gray_700
) {

    val color = remember(darkMode) {
        if (darkMode) {
            darkModeColor
        } else {
            lightModeColor
        }
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(R.drawable.icn_copy),
            contentDescription = null,
            modifier = Modifier.noEffectClickable {
                copy()
            },
            colorFilter = ColorFilter.tint(color = colorResource(color))
        )

        Image(
            painter = painterResource(R.drawable.icn_share),
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 40.dp)
                .noEffectClickable { share() },
            colorFilter = ColorFilter.tint(color = colorResource(color))
        )

        Image(
            painter =
                if (isLike) {
                    painterResource(R.drawable.icn_fill_heart)
                } else {
                    painterResource(R.drawable.icn_empty_heart)
                }, contentDescription = null,
            modifier = Modifier.noEffectClickable {
                setIsLike(!isLike)
            },
            colorFilter = ColorFilter.tint(color = colorResource(color))
        )

    }

}

@Preview(showBackground = true)
@Composable
private fun HomeBottomSectionPreview() {
    InteractionButtonSection(
        copy = {},
        share = {},
        isLike = false,
        setIsLike = {}
    )
}