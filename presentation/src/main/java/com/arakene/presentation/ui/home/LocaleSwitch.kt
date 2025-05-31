package com.arakene.presentation.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.LocaleType
import com.arakene.presentation.util.noEffectClickable

@Composable
fun LocaleSwitch(
    selected: LocaleType,
    setSelected: (LocaleType) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = FillsaTheme.typography.buttonXSmallBold,
    rootPadding: PaddingValues = PaddingValues(horizontal = 3.dp, vertical = 4.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
) {

    Row(
        modifier = modifier
            .background(
                color = colorResource(R.color.purple02),
                shape = RoundedCornerShape(99.dp)
            )
            .padding(rootPadding)
            .noEffectClickable {
                setSelected(
                    if (selected == LocaleType.ENG) {
                        LocaleType.KOR
                    } else {
                        LocaleType.ENG
                    }
                )
            },
        verticalAlignment = Alignment.CenterVertically
    ) {

        LocaleItem(
            stringResource(R.string.locale_type_kor),
            selected = selected == LocaleType.KOR,
            textStyle = textStyle,
            contentPadding = contentPadding
        )

        LocaleItem(
            stringResource(R.string.locale_type_eng),
            selected = selected == LocaleType.ENG,
            textStyle = textStyle,
            contentPadding = contentPadding
        )

    }

}

@Composable
private fun LocaleItem(
    type: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = FillsaTheme.typography.buttonXSmallBold,
    contentPadding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
) {
    Text(
        type,
        style = textStyle,
        color = colorResource(R.color.gray_700),
        modifier = modifier
            .background(
                color = if (selected) colorResource(R.color.white)
                else Color.Transparent,
                shape = RoundedCornerShape(99.dp)
            )
            .padding(contentPadding)
    )
}

@Preview(showBackground = true, name = "KOR")
@Composable
private fun LocaleSwitchPreview() {
    LocaleSwitch(
        selected = LocaleType.KOR,
        setSelected = {}
    )
}

@Preview(showBackground = true, name = "ENG")
@Composable
private fun LocaleSwitchEngPreview() {
    LocaleSwitch(
        selected = LocaleType.ENG,
        setSelected = {}
    )
}