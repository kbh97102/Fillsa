package com.arakene.presentation.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.arakene.presentation.ui.theme.FillsaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypingQuoteBodySection(
    quote: String,
    modifier: Modifier = Modifier
) {

    var write by remember {
        mutableStateOf("")
    }

    BasicTextField(
        value = write,
        onValueChange = {
            if (it.length <= quote.length) {
                write = it
            }
        },
        textStyle = FillsaTheme.typography.body1,
        cursorBrush = SolidColor(Color.Black),
        modifier = modifier,
        decorationBox = { _ ->
            val annotated = buildAnnotatedString {
                for (i in quote.indices) {
                    val expectedChar = quote[i]
                    val typedChar = write.getOrNull(i)

                    val color = when {
                        typedChar == null -> Color.LightGray // 아직 입력 안 됨
                        typedChar == expectedChar -> Color.Black // 일치
                        else -> Color.Red // 틀림
                    }

                    append(expectedChar)
                    addStyle(SpanStyle(color = color), i, i + 1)
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
        quote = "상황을 가장 잘 활용하는 사람이 가장 좋은 상황을 맞는다."
    )
}