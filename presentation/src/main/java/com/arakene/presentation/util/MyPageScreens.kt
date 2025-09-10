package com.arakene.presentation.util

import androidx.annotation.Keep
import com.arakene.domain.responses.NoticeResponse
import kotlinx.serialization.Serializable

@Keep
sealed interface MyPageScreens: Screens {

    @Keep
    @Serializable
    data object Notice: MyPageScreens {
        override val routeString: String
            get() = "Notice"
    }

    @Keep
    @Serializable
    data class NoticeDetail(
        val noticeResponse: NoticeResponse = NoticeResponse()
    ): MyPageScreens {
        override val routeString: String
            get() = "NoticeDetail"
    }

    @Keep
    @Serializable
    data object Alert: MyPageScreens {
        override val routeString: String
            get() = "Alert"
    }

}