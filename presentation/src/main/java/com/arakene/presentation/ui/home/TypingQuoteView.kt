package com.arakene.presentation.ui.home

import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.arakene.domain.responses.DailyQuoteDto
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.ui.theme.defaultButtonColors
import com.arakene.presentation.util.CommonAction
import com.arakene.presentation.util.CommonEffect
import com.arakene.presentation.util.HandleViewEffect
import com.arakene.presentation.util.LocalSnackbarHost
import com.arakene.presentation.util.LocaleType
import com.arakene.presentation.util.Screens
import com.arakene.presentation.util.TypingAction
import com.arakene.presentation.util.TypingEffect
import com.arakene.presentation.util.copyToClipboard
import com.arakene.presentation.util.noEffectClickable
import com.arakene.presentation.viewmodel.TypingViewModel

@Composable
fun TypingQuoteView(
    data: DailyQuoteDto,
    navigate: (Screens) -> Unit,
    backOnClick: () -> Unit,
    viewModel: TypingViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState = LocalSnackbarHost.current
) {

    val focusManager = LocalFocusManager.current

    val typingSectionFocusRequester = remember {
        FocusRequester()
    }

    LaunchedEffect(data.dailyQuoteSeq) {
        viewModel.handleContract(TypingEffect.Refresh(data.dailyQuoteSeq))
    }

    var isLike by remember {
        viewModel.isLike
    }

    val savedKorTyping by remember {
        viewModel.savedKorTyping
    }

    val savedEngTyping by remember {
        viewModel.savedEngTyping
    }

    var korTyping by remember(savedKorTyping) {
        mutableStateOf(
            TextFieldValue(savedKorTyping, selection = TextRange(savedKorTyping.length))
        )
    }

    var engTyping by remember(savedEngTyping) {
        mutableStateOf(
            TextFieldValue(savedEngTyping, selection = TextRange(savedEngTyping.length))
        )
    }

    var localeType by remember {
        mutableStateOf(LocaleType.KOR)
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    val context = LocalContext.current
    val clipBoard = LocalClipboard.current

    val scope = rememberCoroutineScope()

    val keyboardController = LocalSoftwareKeyboardController.current

    val updateBackEvent by rememberUpdatedState({
        viewModel.handleContract(
            TypingAction.Back(
                korTyping = korTyping.text,
                engTyping = engTyping.text,
                data,
                localeType,
                isLike
            )
        )

        backOnClick()
    })

    BackHandler {
        updateBackEvent.invoke()
    }

    HandleViewEffect(
        viewModel.effect,
        lifecycleOwner = lifecycleOwner
    ) {

        when (it) {
            is CommonEffect.Move -> {
                navigate(it.screen)
            }

            is CommonEffect.PopBackStack -> {
                updateBackEvent()
            }

            is CommonEffect.ShowSnackBar -> {
                snackbarHostState.showSnackbar(it.message)
            }

            is CommonEffect.HideKeyboard -> {
                keyboardController?.hide()
            }
        }
    }

    Column(
        modifier = Modifier
            .background(Color.White)
            .noEffectClickable {
                focusManager.clearFocus()
            }) {
        TypingQuoteTopSection(
            locale = localeType,
            setLocale = {
                localeType = it
            },
            onBackClick = { viewModel.handleContract(CommonAction.PopBackStack) },
            modifier = Modifier.padding(horizontal = 15.dp)
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp)
                .noEffectClickable {
                    typingSectionFocusRequester.requestFocus()
                }
        ) {
            TypingQuoteBodySection(
                modifier = Modifier.focusRequester(focusRequester = typingSectionFocusRequester),
                quote = if (localeType == LocaleType.KOR) {
                    data.korQuote ?: ""
                } else {
                    data.engQuote ?: ""
                },
                write = if (localeType == LocaleType.KOR) {
                    korTyping
                } else {
                    engTyping
                },
                setWrite = {
                    if (localeType == LocaleType.KOR) {
                        korTyping = it
                    } else {
                        engTyping = it
                    }
                },
                localeType = localeType
            )

            Spacer(Modifier.weight(1f))

            TypingQuoteBottomSection(
                saveOnClick = {
                    viewModel.handleContract(
                        TypingAction.Save(
                            korTyping = korTyping.text,
                            engTyping = engTyping.text,
                            data,
                            localeType,
                            isLike
                        )
                    )
                },
                shareOnClick = {
                    viewModel.handleContract(
                        CommonEffect.Move(
                            Screens.Share(
                                quote = if (localeType == LocaleType.KOR) {
                                    data.korQuote
                                } else {
                                    data.engQuote
                                } ?: "",
                                author = if (localeType == LocaleType.KOR) {
                                    data.korAuthor
                                } else {
                                    data.engAuthor
                                } ?: ""
                            )
                        )
                    )
                },
                copyOnClick = {
                    copyToClipboard(
                        context = context,
                        clipBoard = clipBoard,
                        author = if (localeType == LocaleType.KOR) {
                            data.korAuthor
                        } else {
                            data.engAuthor
                        } ?: "",
                        quote = if (localeType == LocaleType.KOR) {
                            data.korQuote
                        } else {
                            data.engQuote
                        } ?: "",
                        scope = scope,
                        snackbarHostState = snackbarHostState
                    )
                },
                like = isLike,
                setLike = {
                    // TODO: 어떻게 관리하는게 mvi 패턴을 더 잘 사용하는 걸까 너무 갇히는건가
                    isLike = !isLike
                    viewModel.handleContract(
                        TypingAction.ClickLike(
                            like = isLike,
                            dailyQuoteSeq = data.dailyQuoteSeq
                        )
                    )
                },
            )

        }
    }

}

@Composable
private fun TypingQuoteBottomSection(
    saveOnClick: () -> Unit,
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

        InteractionButtonSection(
            copy = copyOnClick,
            share = shareOnClick,
            isLike = like,
            setIsLike = setLike,
        )

        Button(
            onClick = saveOnClick,
            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(1.dp, color = colorResource(R.color.gray_700)),
            colors = MaterialTheme.colorScheme.defaultButtonColors
        ) {

            Text(
                stringResource(R.string.save),
                color = colorResource(R.color.gray_700),
                style = FillsaTheme.typography.body3
            )

        }

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
        ),
        navigate = {},
        backOnClick = {}
    )
}