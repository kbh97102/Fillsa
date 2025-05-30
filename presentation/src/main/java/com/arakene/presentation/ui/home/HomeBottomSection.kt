package com.arakene.presentation.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arakene.presentation.R
import com.arakene.presentation.util.noEffectClickable

@Composable
fun HomeBottomSection(

    copy: () -> Unit,
    share: () -> Unit,
    isLike: Boolean,
    setIsLike: (Boolean) -> Unit,
    modifier: Modifier = Modifier

) {

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {

        Image(
            painter = painterResource(R.drawable.icn_copy),
            contentDescription = null,
            modifier = Modifier.noEffectClickable {
                copy()
            })

        Image(
            painter = painterResource(R.drawable.icn_share),
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 40.dp)
                .noEffectClickable { share() })

        Image(
            painter =
                if (isLike) {
                    painterResource(R.drawable.icn_fill_heart)
                } else {
                    painterResource(R.drawable.icn_empty_heart)
                }, contentDescription = null,
            modifier = Modifier.noEffectClickable {
                setIsLike(!isLike)
            }
        )

    }

}

@Preview(showBackground = true)
@Composable
private fun HomeBottomSectionPreview() {
    HomeBottomSection(
        copy = {},
        share = {},
        isLike = false,
        setIsLike = {}
    )
}