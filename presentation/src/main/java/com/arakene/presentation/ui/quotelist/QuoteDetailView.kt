package com.arakene.presentation.ui.quoteli

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.arakene.presentation.R
import com.arakene.presentation.ui.common.HeaderSection
import com.arakene.presentation.ui.quotelist.QuoteDetailContentSection
import com.arakene.presentation.util.CommonAction
import com.arakene.presentation.util.CommonEffect
import com.arakene.presentation.util.HandleViewEffect
import com.arakene.presentation.util.LocaleType
import com.arakene.presentation.util.Navigate
import com.arakene.presentation.util.QuoteListAction
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

            is CommonEffect.PopBackStack -> {
                onBackPress()
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
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
    ) {

        // 탑 영역, 뒤로가기 , 메모 텍스트
        HeaderSection(text = stringResource(R.string.memo), onBackPress = {
            viewModel.handleContract(CommonAction.PopBackStack)
        })

        QuoteDetailContentSection(
            imagePath = imagePath,
            localeType = localeType,
            setLocaleType = {
                localeType = it
            },
            quote = quote,
            author = author,
            memo = memo,
            clickMemo = {
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