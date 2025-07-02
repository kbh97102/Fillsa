package com.arakene.presentation.util

import com.arakene.domain.responses.DailyQuoteDto
import com.arakene.domain.responses.MemberQuotesResponse
import com.kizitonwose.calendar.core.CalendarDay
import java.io.File
import java.time.LocalDate
import java.time.YearMonth


sealed interface LoginAction : Action {

    data class ClickGoogleLogin(
        val accessToken: String?,
        val refreshToken: String?,
        val idToken: String?,
        val accessTokenExpirationTime: Long?,
        val appVersion: String,
        val isOnboarding: Boolean
    ) : LoginAction

    data class ClickKakaoLogin(
        val accessToken: String?,
        val refreshToken: String?,
        val refreshTokenExpiresIn: String? = null,
        val expiresIn: String? = null,
        val appVersion: String,
        val isOnboarding: Boolean
    ) : LoginAction

    data object ClickNonMember : LoginAction


    data object ClickTermsOfUse : LoginAction

    data object ClickPrivacyPolicy : LoginAction
}


sealed interface HomeAction : Action {
    data object ClickNext : HomeAction
    data object ClickBefore : HomeAction
    data object ClickLike : HomeAction
    data object ClickQuote : HomeAction
    data class ClickImage(val isLogged: Boolean, val quote: String, val author: String) : HomeAction
    data class ClickShare(val quote: String, val author: String) : HomeAction
    data class ClickChangeImage(val file: File?, val uri: String) : HomeAction
    data object ClickDeleteImage : HomeAction
    data object ClickCalendar: HomeAction
}

sealed interface TypingAction : Action {
    // TODO: 공통으로?
    data class ClickShare(val quote: String, val author: String) : TypingAction
    data class ClickLike(val like: Boolean, val dailyQuoteSeq: Int) : TypingAction
    data class Back(
        val korTyping: String,
        val engTyping: String,
        val dailyQuote: DailyQuoteDto,
        val localeType: LocaleType,
        val isLike: Boolean
    ) : TypingAction
}

sealed interface QuoteListAction : Action {
    data class ClickItem(
        val memberQuotesResponse: MemberQuotesResponse
    ) : QuoteListAction

    data class ClickMemo(
        val memberQuoteSeq: String,
        val savedMemo: String
    ) : QuoteListAction
}

sealed interface CalendarAction : Action {
    data class ChangeMonth(
        val target: YearMonth
    ) : CalendarAction

    data class SelectDay(
        val target: CalendarDay
    ) : CalendarAction

    data object ClickBottomQuote : CalendarAction

    data object ClickCount: CalendarAction
}

sealed interface MyPageAction : Action {
    data object Resign : MyPageAction
    data object Logout : MyPageAction
    data object Login : MyPageAction
    data class ClickAlarmUsage(val usage: Boolean) : MyPageAction
}