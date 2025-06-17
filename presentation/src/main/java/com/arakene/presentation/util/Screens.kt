package com.arakene.presentation.util

import androidx.annotation.Keep
import com.arakene.domain.responses.DailyQuoteDto
import kotlinx.serialization.Serializable

@Keep
sealed interface Screens {

    val routeString: String
    val needLogin: Boolean
        get() = false

    @Serializable
    data class Login(
        val isOnBoarding: Boolean = false
    ) : Screens {
        override val routeString: String
            get() = "Login"
    }

    @Serializable
    @Keep
    data class Home(
        val targetMonth: Int = 0,
        val targetYear: Int = 0,
        val targetDay: Int = 0
    ) : Screens {
        override val routeString: String
            get() = "home"
    }

    @Serializable
    @Keep
    data object Calendar : Screens {
        override val routeString: String
            get() = "Calendar"
        override val needLogin: Boolean
            get() = false
    }

    @Serializable
    @Keep
    data object MyPage : Screens {
        override val routeString: String
            get() = "My page"
    }

    @Serializable
    data class DailyQuote(
        val dailyQuoteDto: DailyQuoteDto
    ) : Screens {
        override val routeString: String
            get() = "DailyQuote"
    }

    @Serializable
    data class Share(
        val quote: String,
        val author: String
    ) : Screens {
        override val routeString: String
            get() = "Share"
    }

    @Serializable
    @Keep
    data object QuoteList : Screens {
        override val routeString: String
            get() = "List"
        override val needLogin: Boolean
            get() = false
    }

    @Serializable
    data class QuoteDetail(
        val engQuote: String,
        val engAuthor: String,
        val korQuote: String,
        val korAuthor: String,
        val authorUrl: String,
        val memberQuoteSeq: String,
        val memo: String?,
        val imagePath: String
    ) : Screens {
        override val routeString: String
            get() = "QuoteDetail"
    }

    @Serializable
    data class MemoInsert(
        val savedMemo: String,
        val memberQuoteSeq: String,
    ) : Screens {
        override val routeString: String
            get() = "MemoInsert"
    }
}