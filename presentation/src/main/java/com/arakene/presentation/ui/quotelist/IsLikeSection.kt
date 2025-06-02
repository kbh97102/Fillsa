package com.arakene.presentation.ui.quotelist

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arakene.presentation.R
import com.arakene.presentation.ui.common.CheckBox
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.noEffectClickable

@Composable
fun IsLikeSection(
    isLike: Boolean,
    setIsLike: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier
        .noEffectClickable { setIsLike(!isLike) },
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(painter = painterResource(R.drawable.icn_filled_heart), contentDescription = null)

        Text(
            stringResource(R.string.like),
            style = FillsaTheme.typography.buttonMediumNormal,
            color = colorResource(R.color.gray_700),
            modifier = Modifier.padding(start = 4.dp)
        )

        CheckBox(
            isLike = isLike,
            modifier = Modifier.padding(start = 8.dp)
        )

    }

}


@Composable
@Preview
private fun IsLikeSectionPreview() {
    IsLikeSection(
        isLike = false,
        setIsLike = {}
    )
}