package com.arakene.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.arakene.domain.requests.LikeRequest
import com.arakene.domain.requests.LocalQuoteInfo
import com.arakene.domain.requests.TypingQuoteRequest
import com.arakene.domain.responses.DailyQuoteDto
import com.arakene.domain.util.CommonError
import com.arakene.domain.usecase.common.DeleteLocalQuoteUseCase
import com.arakene.domain.usecase.common.GetLoginStatusUseCase
import com.arakene.domain.usecase.common.GetMemberStreaksUseCase
import com.arakene.domain.usecase.db.AddLocalQuoteUseCase
import com.arakene.domain.usecase.db.GetLocalQuoteUseCase
import com.arakene.domain.usecase.db.InsertStreakInfoUseCase
import com.arakene.domain.usecase.db.UpdateLocalQuoteLikeUseCase
import com.arakene.domain.usecase.home.GetTypingUseCase
import com.arakene.domain.usecase.home.GetMemberQuoteDayUseCase
import com.arakene.domain.usecase.home.PostLikeUseCase
import com.arakene.domain.usecase.home.PostTypingUseCase
import com.arakene.domain.util.YN
import com.arakene.presentation.util.Action
import com.arakene.presentation.util.BaseViewModel
import com.arakene.presentation.util.CommonEffect
import com.arakene.presentation.util.DialogData
import com.arakene.presentation.util.Effect
import com.arakene.presentation.util.Screens
import com.arakene.presentation.util.TypingEffect
import com.arakene.presentation.util.TypingResultKind
import com.arakene.presentation.util.action.TypingAction
import com.arakene.presentation.util.getDayOfWeekEnglish
import com.arakene.presentation.util.logError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicBoolean

@HiltViewModel
class TypingViewModel @Inject constructor(
    private val postLikeUseCase: PostLikeUseCase,
    private val addLocalQuoteUseCase: AddLocalQuoteUseCase,
    private val getLoginStateUseCase: GetLoginStatusUseCase,
    private val updateLocalQuoteLikeUseCase: UpdateLocalQuoteLikeUseCase,
    private val getTypingUseCase: GetTypingUseCase,
    private val postTypingUseCase: PostTypingUseCase,
    private val getMemberQuoteDayUseCase: GetMemberQuoteDayUseCase,
    private val getLocalQuoteUseCase: GetLocalQuoteUseCase,
    private val deleteLocalQuoteUseCase: DeleteLocalQuoteUseCase,
    private val getMemberStreakResponse: GetMemberStreaksUseCase,
    private val setTodayTypingComplete: InsertStreakInfoUseCase,
) : BaseViewModel() {

    private val saving = AtomicBoolean(false)

    var isLike = mutableStateOf(false)

    val savedKorTyping = mutableStateOf("")
    val savedEngTyping = mutableStateOf("")

    override fun handleAction(action: Action) {
        when (val typingAction = action as TypingAction) {

            is TypingAction.ClickShare -> {
                emitEffect(
                    CommonEffect.Move(
                        Screens.Share(
                            quote = typingAction.quote,
                            author = typingAction.author
                        )
                    )
                )
            }

            is TypingAction.ClickLike -> {
                postLike(typingAction.like, typingAction.dailyQuoteSeq)
            }

            is TypingAction.Back -> {
                saveTyping(
                    typingAction.korTyping,
                    typingAction.engTyping,
                    typingAction.dailyQuote,
                    likeYn = typingAction.isLike
                )
            }

            is TypingAction.Save -> {
                emitEffect(CommonEffect.HideKeyboard)
                saveTyping(
                    typingAction.korTyping,
                    typingAction.engTyping,
                    typingAction.dailyQuote,
                    likeYn = typingAction.isLike,
                    useSaveSnackBar = true
                )
            }

            else -> {

            }
        }
    }

    override fun emitEffect(effect: Effect) {
        when (effect) {
            is TypingEffect.Refresh -> {
                getQuote(effect.seq)
            }

            else -> super.emitEffect(effect)
        }
    }

    private fun getQuote(seq: Int) {
        viewModelScope.launch {

            val loginStatus = getLoginStateUseCase().firstOrNull() ?: false

            if (loginStatus) {
                val typingResult = withLoading { getTypingUseCase(seq) }
                getResponse(typingResult)?.let { response ->
                    savedEngTyping.value = response.typingEngQuote ?: ""
                    savedKorTyping.value = response.typingKorQuote ?: ""
                    isLike.value = YN.getYN(response.likeYn) == YN.Y
                }
            } else {
                getLocalQuoteUseCase(seq)?.let {
                    savedEngTyping.value = it.engTyping
                    savedKorTyping.value = it.korTyping
                    isLike.value = YN.getYN(it.likeYn) == YN.Y
                }
            }
        }
    }

    private fun saveTyping(
        korTyping: String,
        engTyping: String,
        dailyQuoteDto: DailyQuoteDto,
        likeYn: Boolean,
        useSaveSnackBar: Boolean = false,
    ) {
        if (!saving.compareAndSet(false, true)) return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val loginStatus = getLoginStateUseCase().firstOrNull() ?: false
                val isComplete =
                    (dailyQuoteDto.korQuote?.isNotBlank() == true && korTyping == dailyQuoteDto.korQuote) ||
                        (dailyQuoteDto.engQuote?.isNotBlank() == true && engTyping == dailyQuoteDto.engQuote)
                val quoteDate = runCatching { LocalDate.parse(dailyQuoteDto.quoteDate) }.getOrNull()
                if (quoteDate == null) {
                    emitError(CommonError.DefaultError)
                    return@launch
                }
                val isTodayQuote = quoteDate == LocalDate.now()
                if (!loginStatus) {

                    val emptyDraft = korTyping.isEmpty() && engTyping.isEmpty() && !likeYn
                    if (emptyDraft) {

                        val data = getLocalQuoteUseCase(dailyQuoteDto.dailyQuoteSeq)

                        if (data != null) {
                            if (data.memo.isEmpty() && data.likeYn == YN.N.type) {
                                deleteLocalQuoteUseCase(dailyQuoteDto.dailyQuoteSeq)
                            }
                        }
                        if (!useSaveSnackBar) return@launch
                    }

                    if (isComplete && isTodayQuote) {
                        logError("Save start in typingViewModel")
                        setTodayTypingComplete()
                    }

                    if (!emptyDraft) addLocalQuoteUseCase(
                        LocalQuoteInfo(
                            memo = "",
                            korAuthor = dailyQuoteDto.korAuthor ?: "",
                            korQuote = dailyQuoteDto.korQuote ?: "",
                            engQuote = dailyQuoteDto.engQuote ?: "",
                            engAuthor = dailyQuoteDto.engAuthor ?: "",
                            korTyping = korTyping,
                            engTyping = engTyping,
                            dailyQuoteSeq = dailyQuoteDto.dailyQuoteSeq,
                            likeYn = if (likeYn) {
                                YN.Y.type
                            } else {
                                YN.N.type
                            },
                            date = dailyQuoteDto.quoteDate,
                            dayOfWeek = getDayOfWeekEnglish(dateStr = dailyQuoteDto.quoteDate)
                        )
                    )
                } else {
                    val saved = getResponse(postTypingUseCase(
                        dailyQuoteSeq = dailyQuoteDto.dailyQuoteSeq,
                        request = TypingQuoteRequest(
                            typingKorQuote = korTyping,
                            typingEngQuote = engTyping
                        )
                    ))
                    if (saved == null) return@launch
                }

                if (useSaveSnackBar) {
                    val completed = if (loginStatus) {
                        val day = getResponse(getMemberQuoteDayUseCase(dailyQuoteDto.quoteDate)) ?: return@launch
                        if (day.dailyQuoteSeq != dailyQuoteDto.dailyQuoteSeq || day.date != dailyQuoteDto.quoteDate) {
                            emitError(CommonError.DefaultError)
                            return@launch
                        }
                        day.completed
                    } else {
                        isComplete
                    }
                    val todayWritten = if (completed && !isTodayQuote) {
                        val status = getMemberStreakResponse()
                        if (loginStatus && status == null) {
                            emitError(CommonError.DefaultError)
                            return@launch
                        }
                        status?.isTodayWritten == true
                    } else false
                    val kind = when {
                        !completed -> TypingResultKind.Incomplete
                        isTodayQuote -> TypingResultKind.Streak
                        todayWritten -> TypingResultKind.Completed
                        else -> TypingResultKind.Continue
                    }
                    val incomplete = kind == TypingResultKind.Incomplete
                    val title = when (kind) {
                        TypingResultKind.Incomplete -> "필사가 완료되지 않았어요."
                        TypingResultKind.Streak -> "연속 필사 완료!"
                        TypingResultKind.Completed -> "필사 완료!"
                        TypingResultKind.Continue -> "오늘도 이어서 필사를 해주세요!"
                    }
                    emitEffect(CommonEffect.ShowDialog(
                        DialogData.Builder()
                            .title(title)
                            .typingResultKind(kind)
                            .singleButton(!incomplete)
                            .okText(if (incomplete) "필사하기" else "확인")
                            .onClick(if (incomplete) null else { { emitEffect(CommonEffect.Move(Screens.Home())) } })
                            .build()
                    ))
                }
            } finally {
                saving.set(false)
            }
        }

    }

    private fun postLike(like: Boolean, dailyQuoteSeq: Int) = viewModelScope.launch {

        val isLogged = getLoginStateUseCase().firstOrNull() ?: false

        if (isLogged) {
            postLikeUseCase(
                LikeRequest(
                    likeYn =
                        if (like) {
                            YN.Y.type
                        } else {
                            YN.N.type
                        }
                ),
                dailyQuoteSeq = dailyQuoteSeq
            )
        } else {
            updateLocalQuoteLikeUseCase(
                likeYN = if (like) {
                    YN.Y
                } else {
                    YN.N
                }, seq = dailyQuoteSeq
            )
        }
    }

}
