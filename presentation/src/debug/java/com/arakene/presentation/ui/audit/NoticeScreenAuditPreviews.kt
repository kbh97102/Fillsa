package com.arakene.presentation.ui.audit

import android.content.res.Configuration
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.LottieCompositionFactory
import com.arakene.domain.responses.*
import com.arakene.domain.util.DarkModeType
import com.arakene.presentation.R
import com.arakene.presentation.ui.LoginContent
import com.arakene.presentation.ui.common.*
import com.arakene.presentation.ui.home.*
import com.arakene.presentation.ui.mypage.*
import com.arakene.presentation.ui.calendar.*
import com.arakene.presentation.ui.quotelist.*
import com.arakene.presentation.ui.quoteli.QuoteDetailContent
import com.arakene.presentation.util.*
import com.arakene.presentation.util.state.QuoteListState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDate

@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292911354() = AuditRoot(false) {
    AuditNoticeRows(false, "공지사항", "2025.03.03", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the in")
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292911389() = AuditRoot(false) {
    NoticeDetailView({}, NoticeResponse(1, "2.0 버전 업데이트 공지", "사용성 향상을 위한 업데이트를 실시했습니다.사용성 향상을 위한 업데이트를 실시했습니다.사용성 향상을 위한 업데이트를 실시했습니다.사용성 향상을 위한 업데이트를 실시했습니다.사용성 향상을 위한 업데이트를 실시했습니다.사용성 향상을 위한 업데이트를 실시했습니다.사용성 향상을 위한 업데이트를 실시했습니다.사용성 향상을 위한 업데이트를 실시했습니다.사용성 향상을 위한 업데이트를 실시했습니다.사용성 향상을 위한 업데이트를 실시했습니다.사용성 향상을 위한 업데이트를 실시했습니다.사용성 향상을 위한 업데이트를 실시했습니다.사용성 향상을 위한 업데이트를 실시했습니다.", "2025.03.03"))
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292911409() = AuditRoot(false) {
    AuditNoticeRows(true, "공지사항", "2025. 03. 21", "광고가 들어가는 영역입니다.")
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292911529() = AuditRoot(true) {
    AuditNoticeRows(false, "공지사항", "2025.03.03", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the in")
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292911568() = AuditRoot(true) {
    NoticeDetailView({}, NoticeResponse(1, "2.0 버전 업데이트 공지", "사용성 향상을 위한 업데이트를 실시했습니다.사용성 향상을 위한 업데이트를 실시했습니다.사용성 향상을 위한 업데이트를 실시했습니다.사용성 향상을 위한 업데이트를 실시했습니다.사용성 향상을 위한 업데이트를 실시했습니다.사용성 향상을 위한 업데이트를 실시했습니다.사용성 향상을 위한 업데이트를 실시했습니다.사용성 향상을 위한 업데이트를 실시했습니다.사용성 향상을 위한 업데이트를 실시했습니다.사용성 향상을 위한 업데이트를 실시했습니다.사용성 향상을 위한 업데이트를 실시했습니다.사용성 향상을 위한 업데이트를 실시했습니다.사용성 향상을 위한 업데이트를 실시했습니다.", "2025.03.03"))
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292911588() = AuditRoot(true) {
    AuditNoticeRows(true, "공지사항", "2025. 03. 21", "광고가 들어가는 영역입니다.")
}

@Composable
private fun AuditNoticeRows(empty: Boolean, title: String, date: String, content: String) {
    val flow = remember { MutableStateFlow(PagingData.from(if (empty) emptyList() else
        List(5) { NoticeResponse(it, content, content, date) })) }
    NoticeContent(flow.collectAsLazyPagingItems(), {})
}
