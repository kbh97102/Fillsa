package com.arakene.presentation.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.arakene.presentation.R
import com.arakene.presentation.util.LocaleType
import com.arakene.presentation.util.noEffectClickable

@Composable
fun TypingQuoteView(
    quote: String,
    author: String
) {

    Column {

    }

}


@Composable
private fun TypingQuoteTopSection(
    locale: LocaleType,
    setLocale: (LocaleType) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

        // TODO: 이미지 간격 수정요청했으니 반영되면 아이콘 수정
        Image(painterResource(R.drawable.icn_arrow_circle), contentDescription = null, modifier = Modifier.noEffectClickable {
            onBackClick()
        })

        LocaleSwitch(
            selected = locale,
            setSelected = setLocale
        )

    }

}


@Composable
@Preview(showBackground = true)
private fun TypingQuoteTopSectionPreview() {
    TypingQuoteTopSection(
        locale = LocaleType.KOR,
        setLocale = {},
        onBackClick = {}
    )
}