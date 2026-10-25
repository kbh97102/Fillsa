package com.arakene.presentation.ui.home

import android.content.res.Configuration
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInspectionMode
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
import com.arakene.presentation.util.DialogDataHolder
import com.arakene.presentation.util.DialogData
import com.arakene.presentation.util.HandleViewEffect
import com.arakene.presentation.util.IsDarkMode
import com.arakene.presentation.util.LocalDialogDataHolder
import com.arakene.presentation.util.LocalHomeRuntimeQaFixture
import com.arakene.presentation.util.LocalSnackbarHost
import com.arakene.presentation.util.LocaleType
import com.arakene.presentation.util.Screens
import com.arakene.presentation.util.TypingEffect
import com.arakene.presentation.util.action.TypingAction
import com.arakene.presentation.util.copyToClipboard
import com.arakene.presentation.util.noEffectClickable
import com.arakene.presentation.viewmodel.TypingViewModel

@Composable
fun TypingQuoteView(
    data: DailyQuoteDto,
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

    LaunchedEffect(data.dailyQuoteSeq) {
        viewModel.handleContract(TypingEffect.Refresh(data.dailyQuoteSeq))
    }

    var isLike by remember {
        viewModel.isLike
    }

    val savedKorTyping by remember {
        viewModel.savedKorTyping
    }
    val homeQaFixture = LocalHomeRuntimeQaFixture.current

    val savedEngTyping by remember {
        viewModel.savedEngTyping
    }

    var korTyping by remember(savedKorTyping, homeQaFixture, data) {
        val initialTyping = if (homeQaFixture != null) data.korQuote.orEmpty().take(8) else savedKorTyping
        mutableStateOf(
            TextFieldValue(initialTyping, selection = TextRange(initialTyping.length))
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
    var typingResultDialog by remember { mutableStateOf<DialogData?>(null) }

    val updateBackEvent by rememberUpdatedState({
        if (homeQaFixture == null) {
            viewModel.handleContract(
                TypingAction.Back(
                    korTyping = korTyping.text,
                    engTyping = engTyping.text,
                    data,
                    localeType,
                    isLike
                )
            )
        }

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
                if (it.screen is Screens.Home) backOnClick() else navigate(it.screen)
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
                if (it.dialogData.typingResultKind != null) {
                    typingResultDialog = it.dialogData
                } else {
                    dialogDataHolder.data = it.dialogData
                    dialogDataHolder.show = true
                }
            }
        }
    }

    TypingQuoteContent(data, korTyping, engTyping, localeType, isLike, darkMode,
        onLocaleChanged = { localeType = it },
        onWriteChanged = { if (localeType == LocaleType.KOR) korTyping = it else engTyping = it },
        onBack = { viewModel.handleContract(CommonAction.PopBackStack) },
        onSave = {
                    if (homeQaFixture == null) {
                        viewModel.handleContract(
                            TypingAction.Save(
                                korTyping = korTyping.text,
                                engTyping = engTyping.text,
                                data,
                                localeType,
                                isLike
                            )
                        )
                    }
                },
        onShare = {
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
        onCopy = {
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
        onLikeChanged = {
                    // TODO: 어떻게 관리하는게 mvi 패턴을 더 잘 사용하는 걸까 너무 갇히는건가
                    isLike = !isLike
                    if (homeQaFixture == null) {
                        viewModel.handleContract(
                            TypingAction.ClickLike(
                                like = isLike,
                                dailyQuoteSeq = data.dailyQuoteSeq
                            )
                        )
                    }
                }
    )

    typingResultDialog?.let { result ->
        TypingResultDialog(
            kind = result.typingResultKind!!,
            title = result.title,
            darkMode = darkMode,
            onConfirm = {
                typingResultDialog = null
                result.onClick?.invoke()
            },
            onCancel = {
                typingResultDialog = null
                result.cancelOnClick?.invoke()
            },
            onDismiss = { typingResultDialog = null },
        )
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
            .height(50.dp)
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

        // Approved Preview exclusion: Fast Preview emits an incompatible LocaleSwitch call.
        if (!LocalInspectionMode.current) {
            LocaleSwitch(
                selected = locale,
                setSelected = setLocale,
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                textStyle = FillsaTheme.typography.buttonSmallNormal,
                rootPadding = PaddingValues(horizontal = 4.dp, vertical = 3.dp)
            )
        }

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

@Preview(widthDp = 360, heightDp = 672, locale = "ko", fontScale = 1f,
    uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun TypingQuoteLightPreview() = TypingQuotePreview(darkMode = false)

@Preview(widthDp = 360, heightDp = 672, locale = "ko", fontScale = 1f,
    uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TypingQuoteDarkPreview() = TypingQuotePreview(darkMode = true)

@Composable
private fun TypingQuotePreview(darkMode: Boolean) = FillsaTheme(darkTheme = darkMode) {
    CompositionLocalProvider(IsDarkMode provides darkMode) {
        TypingQuoteContent(
            data = DailyQuoteDto("상황을 가장 잘 활용하는 사람이 가장 좋은 상황을 맞는다."),
            korTyping = TextFieldValue("상황을 가장 잘 "), engTyping = TextFieldValue(),
            localeType = LocaleType.KOR, isLike = false, darkMode = darkMode,
            onLocaleChanged = {}, onWriteChanged = {}, onBack = {}, onSave = {},
            onShare = {}, onCopy = {}, onLikeChanged = {},
        )
    }
}


@Composable
internal fun TypingQuoteContent(
    data: DailyQuoteDto, korTyping: TextFieldValue, engTyping: TextFieldValue,
    localeType: LocaleType, isLike: Boolean, darkMode: Boolean,
    onLocaleChanged: (LocaleType) -> Unit, onWriteChanged: (TextFieldValue) -> Unit,
    onBack: () -> Unit, onSave: () -> Unit, onShare: () -> Unit, onCopy: () -> Unit,
    onLikeChanged: (Boolean) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val typingSectionFocusRequester = remember { FocusRequester() }
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
                onLocaleChanged(it)
            },
            onBackClick = { onBack() },
            modifier = Modifier.padding(horizontal = 15.dp)
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .noEffectClickable {
                    typingSectionFocusRequester.requestFocus()
                    keyboardController?.show()
                }
        ) {
            TypingQuoteGuide(
                modifier = Modifier.align(Alignment.CenterHorizontally).widthIn(max = 296.dp)
            )
            Spacer(Modifier.height(20.dp))

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
                setWrite = onWriteChanged,
                localeType = localeType
            )

            Spacer(Modifier.weight(1f))

            TypingQuoteBottomSection(
                saveOnClick = onSave,
                shareOnClick = onShare,
                copyOnClick = onCopy,
                like = isLike,
                setLike = onLikeChanged,
            )

        }
    }

}
