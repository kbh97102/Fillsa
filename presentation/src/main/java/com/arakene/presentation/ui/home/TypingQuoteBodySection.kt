package com.arakene.presentation.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
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

    var input by remember(localeType, write) {
        mutableStateOf(TextFieldValue(write, selection = TextRange(write.length)))
    }

    var lastCondition by remember {
        mutableStateOf(true)
    }

    BasicTextField(
        value = input,
        onValueChange = {
            if (it.text.length <= quote.length) {
                val newTextEnd = it.composition?.end ?: 0
                val inputTextEnd = input.composition?.end ?: 0
                val answerSubString = quote.substring(0, it.text.length)


                logDebug("newText ${it.text} inputText ${input.text}  newTextEnd $newTextEnd inputEnd $inputTextEnd lastCondition $lastCondition")

                if (newTextEnd > inputTextEnd && !lastCondition) {

                    val newTextSubString = it.text.substring(0, newTextEnd - 1)
                    val quoteTextSubString = quote.substring(0, newTextEnd - 1)

                    logDebug("Checking newText $newTextSubString quote $quoteTextSubString")

                    if (newTextSubString == quoteTextSubString) {
                        input = it
                    }

                    return@BasicTextField
                }

                lastCondition =
                    it.text.lastOrNull() == answerSubString.lastOrNull()

                input = it
//                setWrite(it.text)
            }
        },
        textStyle = FillsaTheme.typography.body1,
        cursorBrush = SolidColor(Color.Black),
        modifier = modifier.fillMaxWidth(),
        decorationBox = { _ ->
            val annotated = buildAnnotatedString {

                for (i in input.text.indices) {
                    val expectedChar = quote[i]
                    val typedChar = input.text[i]
                    val color = when {
                        typedChar == expectedChar -> black
                        else -> gray
                    }

                    append(typedChar)
                    addStyle(SpanStyle(color = color), i, i + 1)
                }

                for (i in input.text.length until quote.length) {
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