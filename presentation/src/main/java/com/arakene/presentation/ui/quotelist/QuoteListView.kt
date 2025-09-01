package com.arakene.presentation.ui.quotelist

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.arakene.domain.responses.MemberQuotesResponse
import com.arakene.domain.util.CommonErrorType
import com.arakene.presentation.ui.home.HomeTopSection
import com.arakene.presentation.util.CommonEffect
import com.arakene.presentation.util.Contract
import com.arakene.presentation.util.DialogData
import com.arakene.presentation.util.DialogDataHolder
import com.arakene.presentation.util.HandlePagingError
import com.arakene.presentation.util.HandleViewEffect
import com.arakene.presentation.util.LocalDialogDataHolder
import com.arakene.presentation.util.Navigate
import com.arakene.presentation.util.action.QuoteListAction
import com.arakene.presentation.util.noEffectClickable
import com.arakene.presentation.viewmodel.ListViewModel

@Composable
fun QuoteListView(
    navigate: Navigate,
    popBackStack: () -> Unit,
    logoutEvent: () -> Unit,
    viewModel: ListViewModel = hiltViewModel(),
    dialogDataHolder: DialogDataHolder = LocalDialogDataHolder.current,
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    val paging = viewModel.quoteListFlow.collectAsLazyPagingItems()

    val context = LocalContext.current

    HandlePagingError(paging, handleError = {
        when (it) {
            CommonErrorType.NETWORK -> {
                dialogDataHolder.apply {
                    data = DialogData.Builder()
                        .buildNetworkError(context, okOnClick = { paging.refresh() })
                }.show = true
            }

            CommonErrorType.ACCESS_VERSION -> {
                dialogDataHolder.apply {
                    DialogData.Builder()
                        .title("로그인 시간이 만료되었습니다.\n재로그인해주세요")
                        .onClick {
                            logoutEvent()
                        }
                        .build()
                }.show = true
            }
        }
    })

    val lifeCycle = LocalLifecycleOwner.current

    LifecycleResumeEffect(Unit) {
        paging.refresh()
        onPauseOrDispose { }
    }

    BackHandler {
        popBackStack()
    }

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 20.dp)
            .noEffectClickable {
                viewModel.handleContract(QuoteListAction.ClickOutside)
            }
    ) {

        HomeTopSection(
            navigate = navigate
        )

        SubcomposeLayout { constraints ->
            val dataSelectionSection = subcompose("DataSelectSelection") {
                DateSelectSection(
                    startDate = state.startDate,
                    endDate = state.endDate,
                    isCalendarDisplayed = state.displayCalendar,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .noEffectClickable {
                            viewModel.handleContract(QuoteListAction.ClickDateSection)
                        }
                )
            }.firstOrNull()?.measure(constraints)

            val calenderSection = subcompose("CalendarSection") {
                DurationCalendarSection(
                    startDate = state.startDate,
                    endDate = state.endDate,
                    selectDate = viewModel::handleContract,
                    displayCalendar = state.displayCalendar,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }.firstOrNull()?.measure(constraints)

            val likeSection = subcompose("LikeSection") {
                IsLikeSection(
                    modifier = Modifier.padding(top = 20.dp),
                    isLike = state.likeFilter,
                    setIsLike = {
                        viewModel.handleContract(QuoteListAction.UpdateLikeFilter(it))
                    }
                )
            }.firstOrNull()?.measure(constraints)

            val likeSectionHeight = dataSelectionSection?.height ?: 0
            val listSectionHeight = likeSectionHeight + (likeSection?.height ?: 0)
            val ListSectionHeightConstraint = constraints.maxHeight - listSectionHeight

            val listSection = subcompose("ListSection") {
                QuoteListSection(
                    paging,
                    handleContract = {
                        viewModel.handleContract(it)
                    }
                )
            }.firstOrNull()
                ?.measure(
                    constraints.copy(
                        maxHeight = ListSectionHeightConstraint,
                        minHeight = ListSectionHeightConstraint
                    )
                )

            layout(constraints.maxWidth, constraints.maxHeight) {
                dataSelectionSection?.placeRelative(0, 0)

                calenderSection?.placeRelative(
                    0,
                    likeSectionHeight - 16.dp.roundToPx(),
                    zIndex = 0.1f
                )

                likeSection?.placeRelative(
                    constraints.maxWidth - likeSection.measuredWidth,
                    likeSectionHeight
                )
                listSection?.placeRelative(0, listSectionHeight)
            }
        }

    }
}

@Composable
private fun QuoteListSection(
    paging: LazyPagingItems<MemberQuotesResponse>,
    handleContract: (Contract) -> Unit,
    modifier: Modifier = Modifier
) {
    if (paging.itemCount == 0) {
        QuoteListEmptySection(
            modifier = modifier
        )
    } else {

        QuoteListSection(
            modifier = modifier.padding(top = 10.dp),
            list = paging,
            onClick = {
                handleContract(QuoteListAction.ClickItem(it))
            }
        )
    }
}