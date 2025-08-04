package com.arakene.presentation.viewmodel


import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.arakene.domain.requests.LikeRequest
import com.arakene.domain.requests.LocalQuoteInfo
import com.arakene.domain.responses.DailyQuoteDto
import com.arakene.domain.usecase.common.GetLoginStatusUseCase
import com.arakene.domain.usecase.db.AddLocalQuoteUseCase
import com.arakene.domain.usecase.db.FindLocalQuoteByIdUseCase
import com.arakene.domain.usecase.db.GetLocalQuoteListUseCase
import com.arakene.domain.usecase.db.UpdateLocalQuoteLikeUseCase
import com.arakene.domain.usecase.home.DeleteUploadImageUseCase
import com.arakene.domain.usecase.home.GetDailyQuoteNoTokenUseCase
import com.arakene.domain.usecase.home.GetDailyQuoteUseCase
import com.arakene.domain.usecase.home.PostLikeUseCase
import com.arakene.domain.usecase.home.PostUploadImageUseCase
import com.arakene.domain.util.YN
import com.arakene.presentation.ui.home.HomeState
import com.arakene.presentation.util.Action
import com.arakene.presentation.util.BaseViewModel
import com.arakene.presentation.util.CommonEffect
import com.arakene.presentation.util.DateCondition
import com.arakene.presentation.util.DialogData
import com.arakene.presentation.util.Effect
import com.arakene.presentation.util.HomeAction
import com.arakene.presentation.util.HomeEffect
import com.arakene.presentation.util.LocaleType
import com.arakene.presentation.util.Screens
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    // TODO: 이거 정리하는거 디자인패턴? 설계? 관련 글 봤는데 찾아보기
    private val getDailyQuoteNoTokenUseCase: GetDailyQuoteNoTokenUseCase,
    private val getDailyQuoteUseCase: GetDailyQuoteUseCase,
    private val getLoginStatusUseCase: GetLoginStatusUseCase,
    private val postLikeUseCase: PostLikeUseCase,
    private val postUploadImageUseCase: PostUploadImageUseCase,
    private val deleteUploadImageUseCase: DeleteUploadImageUseCase,
    private val updateLocalQuoteLikeUseCase: UpdateLocalQuoteLikeUseCase,
    private val getLocalQuoteListUseCase: GetLocalQuoteListUseCase,
    private val findLocalQuoteByIdUseCase: FindLocalQuoteByIdUseCase,
    private val addLocalQuoteUseCase: AddLocalQuoteUseCase
) : BaseViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> get() = _state

    private val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override fun handleAction(action: Action) {
        when (action) {
            is HomeAction.ClickBefore -> {
                val targetDate = _state.value.date.minusDays(1)
                if (targetDate >= DateCondition.startDay) {
                    updateDateAndLoadData(targetDate)
                }
            }

            is HomeAction.ClickNext -> {
                val targetDate = _state.value.date.plusDays(1)
                if (targetDate >= DateCondition.startDay) {
                    updateDateAndLoadData(targetDate)
                }
            }

            is HomeAction.ClickImage -> {
                clickImage(action)
            }

            is HomeAction.ClickLike -> {
                postLike(_state.value.date)
            }

            is HomeAction.ClickQuote -> {
                emitEffect(CommonEffect.Move(Screens.DailyQuote(_state.value.dailyQuoteDto)))
            }

            is HomeAction.ClickShare -> {
                emitEffect(
                    CommonEffect.Move(
                        Screens.Share(
                            action.quote,
                            action.author
                        )
                    )
                )
            }

            is HomeAction.ClickChangeImage -> {
                uploadBackgroundImage(action)
            }

            is HomeAction.ClickDeleteImage -> {
                deleteBackgroundImage()
            }

            is HomeAction.ClickCalendar -> {
                emitEffect(CommonEffect.Move(Screens.Calendar))
            }

            else -> {

            }
        }

    }

    suspend fun collectLoginStatus() {
        getLoginStatusUseCase()
            .distinctUntilChanged()
            .collectLatest { logged ->
                updateState {
                    it.copy(
                        isLogged = logged
                    )
                }
            }
    }

    private fun updateState(update: (HomeState) -> HomeState) {
        _state.value = update(_state.value)
    }


    private fun updateDateAndLoadData(newDate: LocalDate) {
        updateState {
            it.copy(date = newDate)
        }
        refresh(newDate)
    }

    override fun emitEffect(effect: Effect) {
        when (effect) {
            is HomeEffect.SetDate -> {
//                date.value = effect.date
            }

            is HomeEffect.Refresh -> {
                refresh(effect.date)
            }

            else -> super.emitEffect(effect)
        }
    }

    private fun uploadBackgroundImage(homeAction: HomeAction.ClickChangeImage) =
        viewModelScope.launch {
//            backgroundImageUri.value = homeAction.uri
            emitEffect(HomeEffect.ProcessImage(homeAction.uri))
        }

    fun uploadImage(file: File?) {
        viewModelScope.launch {
            getResponse(
                postUploadImageUseCase(
                    dailyQuoteSeq = _state.value.dailyQuoteDto.dailyQuoteSeq,
                    imageFile = file ?: return@launch
                ), useLoading = false
            )?.let {
                emitEffect(CommonEffect.ShowSnackBar("이미지가 변경되었습니다."))
            }
        }
    }

    private fun deleteBackgroundImage() = viewModelScope.launch {
        emitEffect(
            CommonEffect.ShowDialog(
                dialogData = DialogData.Builder()
                    .title("이미지를 삭제하시겠습니까?")
                    .body("삭제 후 이미지를 되돌릴 수 없습니다. \uD83D\uDE22")
                    .titleTextSize(20.sp)
                    .bodyTextSize(16.sp)
                    .reversed(true)
                    .cancelText("삭제하기")
                    .okText("취소")
                    .cancelOnClick {
                        viewModelScope.launch {
                            getResponse(
                                deleteUploadImageUseCase(_state.value.dailyQuoteDto.dailyQuoteSeq),
                                useLoading = false
                            )?.let {
                                emitEffect(CommonEffect.ShowSnackBar("이미지가 삭제되었습니다."))
                            }
                            updateState { it.copy(backgroundImageUrl = "") }
                        }
                    }
                    .build()
            ))
    }

    private fun clickImage(action: HomeAction.ClickImage) {
        if (!action.isLogged) {
            emitEffect(
                CommonEffect.ShowDialog(
                    // TODO: 이 구조가 과연 좋은거일까? , onClick의 시점, textStyle도 지정하고싶긴한데 viewModel에서 composable함수 참조 해야함
                    DialogData.Builder()
                        .title("로그인 후 사용하실 수 있습니다.")
                        .onClick {
                            emitEffect(CommonEffect.Move(Screens.Login(isOnBoarding = true)))
                        }
                        .build()
                ))
        } else {
            emitEffect(
                HomeEffect.OpenImageDialog(
                    quote = action.quote,
                    author = action.author
                )
            )
        }
    }

    private fun postLike(date: LocalDate) = viewModelScope.launch {
        if (_state.value.isLogged) {
            getResponse(
                postLikeUseCase(
                    LikeRequest(
                        if (!_state.value.isLike) {
                            YN.Y.type
                        } else {
                            YN.N.type
                        }
                    ),
                    dailyQuoteSeq = _state.value.dailyQuoteDto.dailyQuoteSeq
                )
            )?.let {
                updateState { it.copy(isLike = !_state.value.isLike) }
            }
        } else {
            postLocalLike(date)
        }
    }

    private suspend fun postLocalLike(date: LocalDate) {
        findLocalQuoteByIdUseCase(_state.value.dailyQuoteDto.dailyQuoteSeq)?.let {
            updateLocalQuoteLikeUseCase(
                likeYN = if (!_state.value.isLike) {
                    YN.Y
                } else {
                    YN.N
                }, seq = _state.value.dailyQuoteDto.dailyQuoteSeq
            )

            updateState { it.copy(isLike = !_state.value.isLike) }
        } ?: addLocalQuote(date)
    }

    private suspend fun addLocalQuote(date: LocalDate) {
        val currentQuota = _state.value.dailyQuoteDto
        addLocalQuoteUseCase(
            LocalQuoteInfo(
                dailyQuoteSeq = currentQuota.dailyQuoteSeq,
                korQuote = currentQuota.korQuote ?: "",
                engQuote = currentQuota.engQuote ?: "",
                korAuthor = currentQuota.korAuthor ?: "",
                engAuthor = currentQuota.engAuthor ?: "",
                korTyping = "",
                engTyping = "",
                likeYn = YN.Y.type,
                memo = "",
                date = date.format(dateFormat),
                dayOfWeek = date.dayOfWeek.name
            )
        )
    }

    private fun refresh(date: LocalDate) = viewModelScope.launch {
        val isLogged = getLoginStatusUseCase().firstOrNull() ?: false
        val convertedDate = convertDate(date)

        if (isLogged) {
            getDailyQuote(convertedDate)
        } else {
            getDailyQuoteNoToken(convertedDate)
        }
    }

    private fun getDailyQuote(date: String) = viewModelScope.launch {
        getResponse(getDailyQuoteUseCase(date))?.let { quoteData ->
            updateState {
                it.copy(
                    dailyQuoteDto = quoteData,
                    isLike = quoteData.likeYn == YN.Y.type,
                    backgroundImageUrl = quoteData.imagePath ?: "",
                    author = getAuthor(quoteData),
                    quote = getQuote(quoteData)
                )
            }
        }
    }

    private fun getAuthor(data: DailyQuoteDto): String {
        return if (_state.value.selectedLocale == LocaleType.KOR) {
            data.korAuthor ?: ""
        } else {
            data.engAuthor ?: ""
        }
    }

    private fun getQuote(data: DailyQuoteDto): String {
        return if (_state.value.selectedLocale == LocaleType.KOR) {
            data.korQuote ?: ""
        } else {
            data.engQuote ?: ""
        }
    }

    private fun getDailyQuoteNoToken(date: String) = viewModelScope.launch {
        val localList = getLocalQuoteListUseCase()


        getResponse(getDailyQuoteNoTokenUseCase(date))?.let { quoteData ->
            val data = DailyQuoteDto(
                likeYn = "N",
                imagePath = "",
                dailyQuoteSeq = quoteData.dailyQuoteSeq,
                korQuote = quoteData.korQuote,
                engQuote = quoteData.engQuote,
                korAuthor = quoteData.korAuthor,
                engAuthor = quoteData.engAuthor,
                authorUrl = quoteData.authorUrl,
            ).apply {
                quoteDate = date
            }

            updateState {
                it.copy(
                    dailyQuoteDto = data,
                    isLike = localList.find { local ->
                        local.dailyQuoteSeq == quoteData.dailyQuoteSeq
                    }?.let { find ->
                        find.likeYn == YN.Y.type
                    } ?: let {
                        false
                    },
                    backgroundImageUrl = "",
                    author = getAuthor(data),
                    quote = getQuote(data)
                )
            }
        }
    }

    private fun convertDate(date: LocalDate) = dateFormat.format(date)

}