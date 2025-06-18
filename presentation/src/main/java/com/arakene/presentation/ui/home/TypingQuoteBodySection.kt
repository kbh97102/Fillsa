package com.arakene.presentation.ui.home

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.LocaleType
import com.arakene.presentation.util.cursorBlinking

@Composable
fun TypingQuoteBodySection(
    quote: String,
    write: TextFieldValue,
    localeType: LocaleType,
    setWrite: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier
) {

    val gray = colorResource(R.color.gray_ca)
    val black = colorResource(R.color.gray_700)
    val red = Color.Red

    var lastCondition by remember {
        mutableStateOf(true)
    }

    val interactionSource = remember {
        MutableInteractionSource()
    }

    val hasFocus by interactionSource.collectIsFocusedAsState()

    var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    BasicTextField(
        value = write,
        onValueChange = {
            if (it.text.length <= quote.length) {
                val newTextEnd = it.composition?.end ?: 0
                val inputTextEnd = write.composition?.end ?: 0
                val answerSubString = quote.substring(0, it.text.length)

                if (newTextEnd > inputTextEnd && !lastCondition) {

                    val newTextSubString = it.text.substring(0, newTextEnd - 1)
                    val quoteTextSubString = quote.substring(0, newTextEnd - 1)

                    if (newTextSubString == quoteTextSubString) {
                        setWrite(it)
                    }

                    return@BasicTextField
                }

                lastCondition =
                    it.text.lastOrNull() == answerSubString.lastOrNull()

                setWrite(it)
            }
        },
        textStyle = FillsaTheme.typography.body1,
        cursorBrush = SolidColor(Color.Black),
        modifier = modifier.fillMaxWidth(),
        interactionSource = interactionSource,
        decorationBox = { inner ->
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                val annotated = buildAnnotatedString {

                    for (i in write.text.indices) {
                        val expectedChar = quote[i]
                        val typedChar = write.text[i]
                        val color = when {
                            typedChar == expectedChar -> black
                            else -> red
                        }

                        append(typedChar)
                        addStyle(SpanStyle(color = color), i, i + 1)
                    }

                    for (i in write.text.length until quote.length) {
                        val expectedChar = quote[i]

                        append(expectedChar)
                        addStyle(SpanStyle(color = gray), i, i + 1)
                    }
                }



                Text(
                    text = annotated,
                    style = FillsaTheme.typography.body1,
                    modifier = Modifier
                        .cursorBlinking(
                            value = write,
                            layoutResult = layoutResult,
                            hasFocus = hasFocus
                        ),
                    textAlign = TextAlign.Center,
                    onTextLayout = { layoutResult = it }
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
        write = TextFieldValue(),
        setWrite = {},
        localeType = LocaleType.KOR
    )
}