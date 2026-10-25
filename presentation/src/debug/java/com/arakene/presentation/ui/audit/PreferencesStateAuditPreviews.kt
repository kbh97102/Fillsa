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
internal fun AuditFigma293620033() = AuditRoot(false) {
    AuditOverlay(background = { MyPageContent(false, "", "", DarkModeType.SYSTEM, {}, {}, versionOverride = "1.0.31") }) {
        CommonDialogContent("탈퇴하시겠습니까?", {}, {}, body = "탈퇴 후에는 작성하신 필사 정보를 되돌릴 수 없습니다. 😢", positiveText = "취소", negativeText = "탈퇴하기", reversed = true)
    }
}


@Preview(widthDp = 360, heightDp = 586, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292911178() = AuditRoot(false, "com.arakene.presentation.util.Screens.MyPage") {
    AuditOverlay(background = { MyPageContent(true, "닉네임 or 연동된 계정 이름?", "", DarkModeType.SYSTEM, {}, {}, versionOverride = "1.0.31") }) {
        ThemeDialogContent(if (IsDarkMode.current) DarkModeType.DARK else DarkModeType.LIGHT, {}, {}, modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp))
    }
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292911260() = AuditRoot(false) {
    AlertContent(true, true, {})
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292911314() = AuditRoot(false) {
    AlertContent(true, true, {})
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292911435() = AuditRoot(true) {
    AlertContent(true, true, {})
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292911489() = AuditRoot(true) {
    AlertContent(true, true, {})
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292911694() = AuditRoot(true, "com.arakene.presentation.util.Screens.MyPage") {
    AuditOverlay(background = { MyPageContent(true, "닉네임 or 연동된 계정 이름?", "", DarkModeType.SYSTEM, {}, {}, versionOverride = "1.0.31") }) {
        ThemeDialogContent(if (IsDarkMode.current) DarkModeType.DARK else DarkModeType.LIGHT, {}, {}, modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp))
    }
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma293620153() = AuditRoot(true) {
    AuditOverlay(background = { MyPageContent(false, "", "", DarkModeType.SYSTEM, {}, {}, versionOverride = "1.0.31") }) {
        CommonDialogContent("탈퇴하시겠습니까?", {}, {}, body = "탈퇴 후에는 작성하신 필사 정보를 되돌릴 수 없습니다. 😢", positiveText = "취소", negativeText = "탈퇴하기", reversed = true)
    }
}
