package com.arakene.presentation.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.ui.theme.ImageSection
import com.arakene.presentation.util.CommonEffect
import com.arakene.presentation.util.HandleViewEffect
import com.arakene.presentation.util.HomeAction
import com.arakene.presentation.util.HomeEffect
import com.arakene.presentation.util.ImageDialogDataHolder
import com.arakene.presentation.util.LocalSnackbarHost
import com.arakene.presentation.util.LocaleType
import com.arakene.presentation.util.Screens
import com.arakene.presentation.util.copyToClipboard
import com.arakene.presentation.util.logDebug
import com.arakene.presentation.util.resizeImageToMaxSize
import com.arakene.presentation.util.uriToCacheFile
import com.arakene.presentation.viewmodel.HomeViewModel

@Composable
fun HomeView(
    navigate: (Screens) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState = LocalSnackbarHost.current
) {

    LaunchedEffect(Unit) {
        // TODO: 여기서 해야할까?
        viewModel.handleContract(HomeAction.Refresh)
    }

    val backgroundImageUri by viewModel.backgroundImageUri.collectAsState("")

    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    val clipboard = LocalClipboard.current

    val isLogged by viewModel.isLogged.collectAsState(false)

    val date by remember {
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
        }
    }

    val isLike by remember {
        viewModel.isLike
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 20.dp)
    ) {

        if (imageDialogDataHolder.show) {
            ImageDialog(
                author = imageDialogDataHolder.author,
                quote = imageDialogDataHolder.quote,
                onDismiss = {
                    imageDialogDataHolder.show = false
                },
                uploadImage = {
                    viewModel.handleContract(
                        HomeAction.ClickChangeImage(
                            // TODO: 뷰모델에서 하고싶은데 context가 계속 걸림
                            uriToCacheFile(context = context, uri = it)?.let { file ->
                                resizeImageToMaxSize(
                                    originalFile = file,
                                    cacheDir = context.cacheDir
                                )
                            },
                            uri = it.path ?: ""
                        )
                    )
                }
            )
        }

        HomeTopSection()

        Row(
            modifier = Modifier.padding(top = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            CalendarSection(
                date = date,
                modifier = Modifier.weight(1f)
            )

            ImageSection(
                isLogged = isLogged,
                modifier = Modifier.weight(1f),
                imageUri = backgroundImageUri,
                onClick = {

                    logDebug("check URI ${backgroundImageUri}")

                    viewModel.handleContract(
                        HomeAction.ClickImage(
                            isLogged = isLogged,
                            author = author,
                            quote = quote
                        )
                    )
                }
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            LocaleSwitch(
                selected = selectedLocale,
                setSelected = {
                    selectedLocale = it
                },
                modifier = Modifier.padding(top = 20.dp)
            )
        }

        DailyQuotaSection(
            text = quote,
            author = author,
            next = {
                viewModel.handleContract(HomeAction.ClickNext)
            },
            before = {
                viewModel.handleContract(HomeAction.ClickBefore)
            },
            navigate = {
                viewModel.handleContract(HomeAction.ClickQuote)
            },
            modifier = Modifier.padding(top = 20.dp)
        )

        InteractionButtonSection(
            copy = {
                copyToClipboard(context, scope, clipboard, snackbarHostState, quote, author)
            },
            share = {
                viewModel.handleContract(
                    HomeAction.ClickShare(
                        author = author,
                        quote = quote
                    )
                )
            },
            isLike = isLike,
            setIsLike = {
                viewModel.handleContract(HomeAction.ClickLike)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp)
        )
    }

}


@Preview
@Composable
private fun HomeViewPreview() {
    FillsaTheme {
        HomeView(
            navigate = {}
        )
    }
}