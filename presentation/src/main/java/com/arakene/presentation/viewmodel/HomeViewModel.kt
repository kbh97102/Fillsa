package com.arakene.presentation.viewmodel


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import com.arakene.presentation.util.Action
import com.arakene.presentation.util.BaseViewModel
import com.arakene.presentation.util.CommonEffect
import com.arakene.presentation.util.DialogData
import com.arakene.presentation.util.HomeAction
import com.arakene.presentation.util.HomeEffect
import com.arakene.presentation.util.Screens
import com.arakene.presentation.util.logDebug
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
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

    private val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    val isLogged = getLoginStatusUseCase()

    var currentQuota by mutableStateOf(DailyQuoteDto())

    var isLike = mutableStateOf(false)

    val backgroundImageUri = mutableStateOf("")

    val date = mutableStateOf(LocalDate.now())

    private val today = LocalDate.now()

    override fun handleAction(action: Action) {
        when (action) {
            is HomeAction.ClickBefore -> {
                date.value = date.value.minusDays(1)
                refresh(date.value)
            }

            is HomeAction.ClickNext -> {
                if (date.value.plusDays(1) <= today) {
                    date.value = date.value.plusDays(1)
                    refresh(date.value)
                }
            }

            is HomeAction.Refresh -> {
                refresh(action.date)
            }

            is HomeAction.ClickImage -> {
                clickImage(action)
            }

            is HomeAction.ClickLike -> {
                postLike(date.value)
            }

            is HomeAction.ClickQuote -> {
                emitEffect(CommonEffect.Move(Screens.DailyQuote(currentQuota)))
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

            is HomeAction.SetDate -> {
                date.value = action.date
            }

            else -> {

            }
        }

    }

    private fun uploadBackgroundImage(homeAction: HomeAction.ClickChangeImage) =
        viewModelScope.launch {
            backgroundImageUri.value = homeAction.uri

            homeAction.file?.let { file ->
                postUploadImageUseCase(
                    dailyQuoteSeq = currentQuota.dailyQuoteSeq,
                    imageFile = file
                )
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
                        getResponse(deleteUploadImageUseCase(currentQuota.dailyQuoteSeq), useLoading = false)?.let {
                            emitEffect(CommonEffect.ShowSnackBar("이미지가 삭제되었습니다."))
                        }
                        backgroundImageUri.value = ""
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
        isLike.value = !isLike.value
        val isLogged = getLoginStatusUseCase().firstOrNull() ?: false

        if (isLogged) {
            getResponse(
                postLikeUseCase(
                    LikeRequest(
                        if (isLike.value) {
                            YN.Y.type
                        } else {
                            YN.N.type
                        }
                    ),
                    dailyQuoteSeq = currentQuota.dailyQuoteSeq
                )
            )
        } else {
            postLocalLike(date)
        }
    }

    private suspend fun postLocalLike(date: LocalDate) {
        findLocalQuoteByIdUseCase(currentQuota.dailyQuoteSeq)?.let {
            updateLocalQuoteLikeUseCase(
                likeYN = if (isLike.value) {
                    YN.Y
                } else {
                    YN.N
                }, seq = currentQuota.dailyQuoteSeq
            )
        } ?: addLocalQuote(date)
    }

    private suspend fun addLocalQuote(date: LocalDate) {
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
        getResponse(getDailyQuoteUseCase(date))?.let {
            currentQuota = it
            isLike.value = it.likeYn == YN.Y.type
            backgroundImageUri.value = (it.imagePath ?: "")
        }
    }

    private fun getDailyQuoteNoToken(date: String) = viewModelScope.launch {
        val localList = getLocalQuoteListUseCase()


        getResponse(getDailyQuoteNoTokenUseCase(date))?.let {
            currentQuota = DailyQuoteDto(
                likeYn = "N",
                imagePath = "",
                dailyQuoteSeq = it.dailyQuoteSeq,
                korQuote = it.korQuote,
                engQuote = it.engQuote,
                korAuthor = it.korAuthor,
                engAuthor = it.engAuthor,
                authorUrl = it.authorUrl,
            ).apply {
                quoteDate = date
            }

            localList.find {
                it.dailyQuoteSeq == currentQuota.dailyQuoteSeq
            }.also {
                logDebug("find $it")
            }

            localList.find { local ->
                local.dailyQuoteSeq == it.dailyQuoteSeq
            }?.let { find ->
                isLike.value = find.likeYn == YN.Y.type
            } ?: let {
                isLike.value = false
            }

            backgroundImageUri.value = ""
        }
    }

    private fun convertDate(date: LocalDate) = dateFormat.format(date)

}