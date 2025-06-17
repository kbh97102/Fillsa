package com.arakene.presentation.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arakene.presentation.R
import com.arakene.presentation.util.Navigate
import com.arakene.presentation.util.Screens
import com.arakene.presentation.util.noEffectClickable

@Composable
fun HomeTopSection(
    navigate: Navigate,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(painterResource(R.drawable.icn_logo), contentDescription = null)

        Image(
            painterResource(R.drawable.icn_my_page),
            contentDescription = null,
            modifier = Modifier.noEffectClickable {
                navigate(Screens.MyPage)
            })
    }

}


@Preview(showBackground = true, widthDp = 400)
@Composable
private fun HomeTopSectionPreview() {
    HomeTopSection(
        navigate = {}
    )
}