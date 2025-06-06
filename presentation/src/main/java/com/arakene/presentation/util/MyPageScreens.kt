package com.arakene.presentation.util

import com.arakene.domain.responses.NoticeResponse
import kotlinx.serialization.Serializable

sealed interface MyPageScreens: Screens {

    @Serializable
    data object Notice: MyPageScreens {
        override val routeString: String
            get() = "Notice"
    }

    @Serializable
    data class NoticeDetail(
        val noticeResponse: NoticeResponse
    ): MyPageScreens {
        override val routeString: String
            get() = "NoticeDetail"
    }

}