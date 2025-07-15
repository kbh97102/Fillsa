package com.arakene.presentation.ui.quotelist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.arakene.presentation.R
import com.arakene.presentation.ui.common.CustomAsyncImage
import com.arakene.presentation.ui.home.LocaleSwitch
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.LocaleType
import com.arakene.presentation.util.dropShadow
import com.arakene.presentation.util.noEffectClickable

@Composable
fun QuoteDetailContentSection(
    imagePath: String,
    localeType: LocaleType,
    setLocaleType: (LocaleType) -> Unit,
    quote: String,
    author: String,
    memo: String,
    clickMemo: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        // 이미지
        if (imagePath.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .fillMaxWidth()
                    .aspectRatio(320f / 350f)
                    .dropShadow(
                        shape = MaterialTheme.shapes.medium,
                        color = colorResource(R.color.gray_89).copy(alpha = 0.5f),
                        blur = 23.2.dp,
                        spread = 2.dp
                    ),
            ) {
                CustomAsyncImage(
                    imagePath = imagePath,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.medium),
                    error = null
                )
            }
        }

        // 스위치
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            LocaleSwitch(
                selected = localeType,
                setSelected = setLocaleType,
                textStyle = FillsaTheme.typography.subtitle2,
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                rootPadding = PaddingValues(horizontal = 4.dp, vertical = 3.dp)
            )
        }

        // 명언
        MemoQuoteSection(
            author = author,
            quote = quote,
            modifier = Modifier.padding(top = 20.dp)
        )

        // 메모
        MemoInsertSection(
            memo = memo,
            modifier = Modifier
                .padding(top = 20.dp, bottom = 50.dp)
                .noEffectClickable {
                    clickMemo()
                }
        )

    }
}