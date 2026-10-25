package com.arakene.presentation.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme

@Composable
internal fun TypingQuoteGuide(modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(R.drawable.typing_handwriting),
            contentDescription = null,
            modifier = Modifier.size(60.dp),
        )
        Text(
            text = stringResource(R.string.typing_guide),
            style = FillsaTheme.typography.subtitle1.copy(
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.None,
                ),
            ),
            color = colorResource(R.color.purple01),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(widthDp = 296, heightDp = 84, locale = "ko", fontScale = 1f,
    uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun TypingQuoteGuideLightPreview() = FillsaTheme(darkTheme = false) {
    TypingQuoteGuide(Modifier.background(colorResource(R.color.white)))
}

@Preview(widthDp = 296, heightDp = 84, locale = "ko", fontScale = 1f,
    uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TypingQuoteGuideDarkPreview() = FillsaTheme(darkTheme = true) {
    TypingQuoteGuide(Modifier.background(colorResource(R.color.gray_700)))
}
