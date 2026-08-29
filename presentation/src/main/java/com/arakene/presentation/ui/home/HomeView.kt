package com.arakene.presentation.ui.home

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.ui.theme.ImageSection
import com.arakene.presentation.util.CommonEffect
import com.arakene.presentation.util.DialogDataHolder
import com.arakene.presentation.util.DoubleBackPressHandler
import com.arakene.presentation.util.HandleViewEffect
import com.arakene.presentation.util.HomeEffect
import com.arakene.presentation.util.HomePromptQuestion
import com.arakene.presentation.util.ImageDialogDataHolder
import com.arakene.presentation.util.LocalDialogDataHolder
import com.arakene.presentation.util.LocalSnackbarHost
import com.arakene.presentation.util.LocaleType
import com.arakene.presentation.util.Screens
import com.arakene.presentation.util.action.HomeAction
import com.arakene.presentation.util.copyToClipboard
import com.arakene.presentation.util.noEffectClickable
import com.arakene.presentation.util.rememberBaseViewModel
import com.arakene.presentation.util.resizeImageToMaxSize
import com.arakene.presentation.util.uriToCacheFile
import com.arakene.presentation.viewmodel.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate

@Composable
fun HomeView(
    requestDate: LocalDate?,
    navigate: (Screens) -> Unit,
    viewModel: HomeViewModel = rememberBaseViewModel(),
    snackbarHostState: SnackbarHostState = LocalSnackbarHost.current,
    dialogDataHolder: DialogDataHolder = LocalDialogDataHolder.current
) {

    val backgroundImageUrl by remember {
        viewModel.backgroundImageUri
    }

    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    val scope = rememberCoroutineScope()

    val clipboard = LocalClipboard.current

    val isLogged by viewModel.isLogged.collectAsState(false)

    val date by rememberSaveable {
        viewModel.date
    }

    var selectedLocale by remember {
        mutableStateOf(LocaleType.KOR)
    }

    val quote by remember(viewModel.currentQuota, selectedLocale) {
        mutableStateOf(
            if (selectedLocale == LocaleType.KOR) {
                viewModel.currentQuota.korQuote ?: ""
            } else {
                viewModel.currentQuota.engQuote ?: ""
            }
        )
    }

    val author by remember(viewModel.currentQuota, selectedLocale) {
        mutableStateOf(
            if (selectedLocale == LocaleType.KOR) {
                viewModel.currentQuota.korAuthor ?: ""
            } else {
                viewModel.currentQuota.engAuthor ?: ""
            }
        )
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    val imageDialogDataHolder = remember {
        ImageDialogDataHolder()
    }

    DoubleBackPressHandler(
        onExit = {
            (context as? Activity)?.finishAffinity()
        }
    )

    LaunchedEffect(requestDate) {
        if (requestDate != null) {
            viewModel.handleContract(HomeEffect.SetDate(requestDate))
            viewModel.handleContract(HomeEffect.Refresh(requestDate))
        } else {
            viewModel.handleContract(HomeEffect.Refresh(date))
        }
    }

    HandleViewEffect(
        viewModel.effect,
        lifecycleOwner = lifecycleOwner
    ) {
        when (it) {
            is CommonEffect.Move -> {
                navigate(it.screen)
            }

            is HomeEffect.OpenImageDialog -> {
                imageDialogDataHolder.apply {
                    this.quote = it.quote
                    this.author = it.author
                }.run {
                    show = true
                }
            }

            is CommonEffect.ShowSnackBar -> {
                snackbarHostState.showSnackbar(it.message)
            }

            is CommonEffect.ShowDialog -> {
                dialogDataHolder.data = it.dialogData
                dialogDataHolder.show = true
            }

            is HomeEffect.ProcessImage -> {
                val file = withContext(Dispatchers.IO) {
                    uriToCacheFile(context = context, uri = it.uri.toUri())?.let { file ->
                        resizeImageToMaxSize(
                            originalFile = file,
                            cacheDir = context.cacheDir
                        )
                    }
                }
                viewModel.uploadImage(file)
            }
        }
    }

    val isLike by remember {
        viewModel.isLike
    }

    if (imageDialogDataHolder.show) {
        ImageDialog(
            author = imageDialogDataHolder.author,
            quote = imageDialogDataHolder.quote,
            onDismiss = { imageDialogDataHolder.show = false },
            uploadImage = {
                viewModel.handleContract(HomeAction.ClickChangeImage(uri = it.toString()))
            },
            backgroundImageUrl = backgroundImageUrl,
            deleteOnClick = { viewModel.handleContract(HomeAction.ClickDeleteImage) }
        )
    }

    FigmaHomeContent(
        date = date,
        quote = quote,
        author = author,
        selectedLocale = selectedLocale,
        isLike = isLike,
        canGoNext = date.isBefore(LocalDate.now()),
        onLocaleChanged = { selectedLocale = it },
        onHome = { navigate(Screens.Home()) },
        onProfile = { navigate(Screens.MyPage) },
        onCalendar = { viewModel.handleContract(HomeAction.ClickCalendar) },
        onQuote = { viewModel.handleContract(HomeAction.ClickQuote()) },
        onRecordAnswer = { answer ->
            viewModel.handleContract(
                HomeAction.ClickQuote(
                    initialAnswer = answer,
                    promptDate = date.toString(),
                    promptQuestion = HomePromptQuestion,
                )
            )
        },
        onAuthor = { uriHandler.openUri(homeAuthorUri(author)) },
        onPreviousQuote = { viewModel.handleContract(HomeAction.ClickBefore) },
        onNextQuote = { viewModel.handleContract(HomeAction.ClickNext) },
        onCopy = { copyToClipboard(context, scope, clipboard, snackbarHostState, quote, author) },
        onShare = {
            viewModel.handleContract(HomeAction.ClickShare(author = author, quote = quote))
        },
        onLike = { viewModel.handleContract(HomeAction.ClickLike) },
        onImage = {
            viewModel.handleContract(
                HomeAction.ClickImage(isLogged = isLogged, author = author, quote = quote)
            )
        }
    )

}


@Preview
@Composable
private fun HomeViewPreview() {
    FillsaTheme {
        HomeView(
            requestDate = LocalDate.now(),
            navigate = {}
        )
    }
}
