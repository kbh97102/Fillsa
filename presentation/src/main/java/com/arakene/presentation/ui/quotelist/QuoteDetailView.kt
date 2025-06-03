package com.arakene.presentation.ui.quotelist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arakene.presentation.R
import com.arakene.presentation.ui.home.LocaleSwitch
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.LocaleType

@Composable
fun QuoteDetailView(
    quote: String,
    author: String,
    authorUrl: String,
    memberQuoteSeq: String,
    memo: String
) {

    var localeType by remember {
        mutableStateOf(LocaleType.KOR)
    }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 20.dp)
    ) {

        // TODO: 공통화 필요
        // 탑 영역, 뒤로가기 , 메모 텍스트
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(painter = painterResource(R.drawable.icn_arrow), contentDescription = null)

            Text(
                stringResource(R.string.memo),
                style = FillsaTheme.typography.heading4,
                color = colorResource(R.color.gray_700),
                modifier = Modifier.padding(start = 10.dp)
            )
        }

        // 이미지
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(320f / 350f)
                .shadow(
                    2.dp,
                    shape = MaterialTheme.shapes.medium,
                    ambientColor = colorResource(R.color.gray_89).copy(alpha = 0.5f)
                ),
        ) {
            Image(
                painter = painterResource(R.drawable.img_memo_image_default),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
        }

        // 스위치
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            horizontalArrangement = Arrangement.End
        ) {
            LocaleSwitch(
                selected = localeType,
                setSelected = {
                    localeType = it
                },
                textStyle = FillsaTheme.typography.subtitle2,
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                rootPadding = PaddingValues(horizontal = 4.dp, vertical = 3.dp)
            )
        }

        // 명언
        MemoQuoteSection(
            author = "존 우든",
            quote = "상황을 가장 잘 활용하는 사람이 가장 좋은 상황을 맞는다.",
            modifier = Modifier.padding(top = 20.dp)
        )

        // 메모
        MemoInsertSection(
            memo = "",
            modifier = Modifier.padding(top = 20.dp, bottom = 50.dp)
        )

    }

}

@Preview
@Composable
private fun MemoViewPreview() {
    FillsaTheme {
        QuoteDetailView(
            memo = "",
            memberQuoteSeq = "",
            quote = "",
            author = "",
            authorUrl = ""
        )
    }
}