package com.arakene.presentation.ui.quotelist

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.arakene.presentation.R
import com.arakene.presentation.ui.common.CustomAsyncImage
import com.arakene.presentation.ui.common.HeaderSection
import com.arakene.presentation.ui.home.LocaleSwitch
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.CommonEffect
import com.arakene.presentation.util.HandleViewEffect
import com.arakene.presentation.util.LocaleType
import com.arakene.presentation.util.Navigate
import com.arakene.presentation.util.QuoteListAction
import com.arakene.presentation.util.dropShadow
import com.arakene.presentation.util.noEffectClickable
import com.arakene.presentation.viewmodel.ListViewModel

@Composable
fun QuoteDetailView(
    engQuote: String,
    engAuthor: String,
    korQuote: String,
    korAuthor: String,
    authorUrl: String,
    memberQuoteSeq: String,
    memo: String,
    imagePath: String,
    navigate: Navigate,
    onBackPress: () -> Unit,
    viewModel: ListViewModel = hiltViewModel()
) {

    var localeType by remember {
        mutableStateOf(LocaleType.KOR)
    }

    val lifeCycle = LocalLifecycleOwner.current

    HandleViewEffect(
        viewModel.effect,
        lifeCycle
    ) {
        when (it) {
            is CommonEffect.Move -> {
                navigate(it.screen)
            }
        }
    }

    val quote by remember(localeType) {
        mutableStateOf(
            when (localeType) {
                LocaleType.KOR -> {
                    korQuote
                }

                else -> {
                    engQuote
                }
            }
        )
    }

    val author by remember(localeType) {
        mutableStateOf(
            when (localeType) {
                LocaleType.KOR -> {
                    korAuthor
                }

                else -> {
                    engAuthor
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 20.dp)
    ) {

        // 탑 영역, 뒤로가기 , 메모 텍스트
        HeaderSection(text = stringResource(R.string.memo), onBackPress = onBackPress)

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
                    viewModel.handleContract(
                        QuoteListAction.ClickMemo(
                            memberQuoteSeq = memberQuoteSeq,
                            savedMemo = memo
                        )
                    )
                }
        )

    }

}

@Preview
@Composable
private fun MemoViewPreview() {
    QuoteDetailView(
        memo = "",
        memberQuoteSeq = "",
        korQuote = "",
        korAuthor = "",
        engQuote = "",
        engAuthor = "",
        authorUrl = "",
        navigate = {},
        onBackPress = {},
        imagePath = ""
    )
}