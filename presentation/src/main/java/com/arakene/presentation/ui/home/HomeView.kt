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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.ui.theme.ImageSection
import com.arakene.presentation.util.CommonEffect
import com.arakene.presentation.util.DialogDataHolder
import com.arakene.presentation.util.HandleViewEffect
import com.arakene.presentation.util.HomeAction
import com.arakene.presentation.util.HomeEffect
import com.arakene.presentation.util.ImageDialogDataHolder
import com.arakene.presentation.util.LocalDialogDataHolder
import com.arakene.presentation.util.LocalSnackbarHost
import com.arakene.presentation.util.LocaleType
import com.arakene.presentation.util.Screens
import com.arakene.presentation.util.copyToClipboard
import com.arakene.presentation.util.rememberBaseViewModel
import com.arakene.presentation.util.resizeImageToMaxSize
import com.arakene.presentation.util.uriToCacheFile
import com.arakene.presentation.viewmodel.HomeViewModel
import kotlinx.coroutines.delay
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

    /**
     *
     */
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
                            uri = it.toString()
                        )
                    )
                },
                backgroundImageUrl = backgroundImageUrl,
                deleteOnClick = {
                    viewModel.handleContract(HomeAction.ClickDeleteImage)
                }
            )
        }

        HomeTopSection(navigate = navigate)

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
                imageUri = backgroundImageUrl,
                onClick = {
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
            date = date,
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
            requestDate = LocalDate.now(),
            navigate = {}
        )
    }
}