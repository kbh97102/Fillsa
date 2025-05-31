package com.arakene.presentation.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arakene.domain.responses.DailyQuoteDto
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.ui.theme.defaultButtonColors
import com.arakene.presentation.util.LocaleType
import com.arakene.presentation.util.noEffectClickable

@Composable
fun TypingQuoteView(
    data: DailyQuoteDto
) {

    var write by remember {
        mutableStateOf("")
    }

    var localeType by remember {
        mutableStateOf(LocaleType.KOR)
    }

    Column(modifier = Modifier.background(Color.White)) {
        TypingQuoteTopSection(
            locale = localeType,
            setLocale = {
                localeType = it
            },
            onBackClick = {

            },
            modifier = Modifier.padding(horizontal = 15.dp)
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp)
        ) {
            TypingQuoteBodySection(
                quote = if (localeType == LocaleType.KOR) {
                    data.korQuote ?: ""
                } else {
                    data.engQuote ?: ""
                },
                write = write,
                setWrite = {
                    write = it
                }
            )

            Spacer(Modifier.weight(1f))

            TypingQuoteBottomSection(
                onBackClick = {},
                shareOnClick = {},
                copyOnClick = {},
                like = false,
                setLike = {},
            )

        }
    }

}

@Composable
private fun TypingQuoteBottomSection(
    onBackClick: () -> Unit,
    shareOnClick: () -> Unit,
    copyOnClick: () -> Unit,
    like: Boolean,
    setLike: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Button(
            onClick = onBackClick,
            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(1.dp, color = colorResource(R.color.gray_700)),
            colors = MaterialTheme.colorScheme.defaultButtonColors
        ) {

            Text(
                stringResource(R.string.out),
                color = colorResource(R.color.gray_700),
                style = FillsaTheme.typography.body3
            )

        }

        InteractionButtonSection(
            copy = copyOnClick,
            share = shareOnClick,
            isLike = like,
            setIsLike = setLike,
        )

    }

}


@Composable
private fun TypingQuoteTopSection(
    locale: LocaleType,
    setLocale: (LocaleType) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            painterResource(R.drawable.icn_arrow),
            contentDescription = null,
            modifier = Modifier.noEffectClickable {
                onBackClick()
            })

        LocaleSwitch(
            selected = locale,
            setSelected = setLocale,
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
            textStyle = FillsaTheme.typography.buttonSmallNormal,
            rootPadding = PaddingValues(horizontal = 4.dp, vertical = 3.dp)
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

@Composable
@Preview
private fun TypingQuoteViewPreview() {
    TypingQuoteView(
        DailyQuoteDto(
            quote = "Live as if you were to die tomorrow."
        )
    )
}