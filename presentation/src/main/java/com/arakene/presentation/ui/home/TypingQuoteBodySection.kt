package com.arakene.presentation.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.LocaleType
import com.arakene.presentation.util.logDebug

@Composable
fun TypingQuoteBodySection(
    quote: String,
    write: String,
    localeType: LocaleType,
    setWrite: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    val gray = colorResource(R.color.gray_ca)
    val black = colorResource(R.color.gray_700)

    var input by remember(localeType) {
        mutableStateOf(write)
    }

    BasicTextField(
        value = input,
        onValueChange = {
            if (input.length <= quote.length) {
                val subString = quote.substring(0, it.length)
                if (it == subString) {
                    setWrite(it)
                }
            }

            input = it
        },
        textStyle = FillsaTheme.typography.body1,
        cursorBrush = SolidColor(Color.Black),
        modifier = modifier.fillMaxWidth(),
        decorationBox = { _ ->
            val annotated = buildAnnotatedString {

                for (i in input.indices) {
                    val expectedChar = quote[i]
                    val typedChar = input[i]
                    val color = when {
                        typedChar == expectedChar -> black
                        else -> gray
                    }

                    append(typedChar)
                    addStyle(SpanStyle(color = color), i, i + 1)
                }

                for (i in input.length until quote.length) {
                    val expectedChar = quote[i]

                    append(expectedChar)
                    addStyle(SpanStyle(color = gray), i, i + 1)
                }
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = annotated,
                    style = FillsaTheme.typography.body1,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        },
    )
}


@Composable
@Preview(showBackground = true)
private fun TypingQuoteBodySectionPreview() {
    TypingQuoteBodySection(
        quote = "상황을 가장 잘 활용하는 사람이 가장 좋은 상황을 맞는다.",
        write = "",
        setWrite = {},
        localeType = LocaleType.KOR
    )
}