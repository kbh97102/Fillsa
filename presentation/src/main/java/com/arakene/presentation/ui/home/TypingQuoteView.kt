package com.arakene.presentation.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
import com.arakene.presentation.util.DialogDataHolder
import com.arakene.presentation.util.HandleViewEffect
import com.arakene.presentation.util.IsDarkMode
import com.arakene.presentation.util.LocalDialogDataHolder
import com.arakene.presentation.util.LocalSnackbarHost
import com.arakene.presentation.util.LocaleType
import com.arakene.presentation.util.Screens
import com.arakene.presentation.util.TypingEffect
import com.arakene.presentation.util.action.TypingAction
import com.arakene.presentation.util.copyToClipboard
import com.arakene.presentation.util.homeAnswerInputState
import com.arakene.presentation.util.homePromptAnswerRecord
import com.arakene.presentation.util.noEffectClickable
import com.arakene.presentation.viewmodel.TypingViewModel

internal fun typingInitialAnswerDraft(initialAnswer: String): String =
    homeAnswerInputState(initialAnswer).text

@Composable
fun TypingQuoteView(
    data: DailyQuoteDto,
    initialAnswer: String = "",
    promptDate: String = "",
    promptQuestion: String = "",
    navigate: (Screens) -> Unit,
    backOnClick: () -> Unit,
    viewModel: TypingViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState = LocalSnackbarHost.current,
    darkMode: Boolean = IsDarkMode.current,
    dialogDataHolder: DialogDataHolder = LocalDialogDataHolder.current
) {

    val focusManager = LocalFocusManager.current

    val typingSectionFocusRequester = remember {
        FocusRequester()
    }
    val hasPromptAnswer = promptDate.isNotBlank() && promptQuestion.isNotBlank()

    LaunchedEffect(data.dailyQuoteSeq) {
        viewModel.handleContract(TypingEffect.Refresh(data.dailyQuoteSeq))
    }

    LaunchedEffect(promptDate, promptQuestion) {
        if (hasPromptAnswer) {
            viewModel.loadPromptAnswer(promptDate, promptQuestion)
        }
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

    val savedPromptAnswer by remember {
        viewModel.savedPromptAnswer
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

    val routeOrSavedAnswer = if (initialAnswer.isNotEmpty()) initialAnswer else savedPromptAnswer
    var answerDraft by rememberSaveable(initialAnswer, savedPromptAnswer) {
        mutableStateOf(typingInitialAnswerDraft(routeOrSavedAnswer))
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
        val promptAnswer = if (hasPromptAnswer) {
            homePromptAnswerRecord(promptDate, promptQuestion, answerDraft)
        } else {
            null
        }
        viewModel.handleContract(
            TypingAction.Back(
                korTyping = korTyping.text,
                engTyping = engTyping.text,
                data,
                localeType,
                isLike,
                promptAnswer = promptAnswer,
            )
        )

        if (promptAnswer == null) {
            backOnClick()
        }
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

            is CommonEffect.ShowDialog -> {
                dialogDataHolder.data = it.dialogData
                dialogDataHolder.show = true
            }

            is TypingEffect.PromptAnswerSaved -> {
                backOnClick()
            }
        }
    }

    Column(
        modifier = Modifier
            .background(
                if (darkMode) {
                    colorResource(R.color.gray_700)
                } else {
                    colorResource(R.color.white)
                }
            )
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
                    keyboardController?.show()
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

            if (hasPromptAnswer) {
                TypingPromptAnswerDraftSection(
                    answer = answerDraft,
                    onAnswerChanged = { answerDraft = homeAnswerInputState(it).text },
                    modifier = Modifier.padding(top = 24.dp),
                )
            }

            Spacer(Modifier.weight(1f))

            TypingQuoteBottomSection(
                saveOnClick = {
                    val promptAnswer = if (hasPromptAnswer) {
                        homePromptAnswerRecord(promptDate, promptQuestion, answerDraft)
                    } else {
                        null
                    }
                    viewModel.handleContract(
                        TypingAction.Save(
                            korTyping = korTyping.text,
                            engTyping = engTyping.text,
                            data,
                            localeType,
                            isLike,
                            promptAnswer = promptAnswer,
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
private fun TypingPromptAnswerDraftSection(
    answer: String,
    onAnswerChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text("오늘의 답변", style = FillsaTheme.typography.body3)
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .height(120.dp)
                .background(colorResource(R.color.white), RoundedCornerShape(8.dp))
                .border(1.dp, colorResource(R.color.gray_ca), RoundedCornerShape(8.dp)),
        ) {
            BasicTextField(
                value = answer,
                onValueChange = onAnswerChanged,
                textStyle = FillsaTheme.typography.body3.copy(color = colorResource(R.color.gray_700)),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
                    .semantics { contentDescription = "오늘의 답변 초안 입력" },
                decorationBox = { innerTextField ->
                    if (answer.isEmpty()) {
                        Text(
                            "오늘의 답변을 기록해보세요.",
                            style = FillsaTheme.typography.body4,
                            color = colorResource(R.color.gray_ca),
                        )
                    }
                    innerTextField()
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
            darkModeColor = R.color.white
        )

        Button(
            onClick = saveOnClick,
            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
            shape = RoundedCornerShape(8.dp),
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
            },
            colorFilter = ColorFilter.tint(FillsaTheme.colorScheme.onBackground1)
        )

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
