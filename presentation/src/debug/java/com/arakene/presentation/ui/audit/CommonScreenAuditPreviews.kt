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
internal fun AuditFigma29295969() = AuditRoot(false) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircleLoadingSpinner(isLoading = true) }
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma29295975() = AuditRoot(false) {
    AuditOverlay(background = { AuditHome() }) {
        DialogWIthImageContent("서버와의 통신이 원활하지 않습니다.\n인터넷 연결 상태를 확인해 주세요.", "", R.drawable.icn_network_error, {}, {}, positiveText = "다시 시도", negativeText = "종료하기", titleTextStyle = TypographyEnum.Heading4)
    }
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma29296086() = AuditRoot(false) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircleLoadingSpinner(isLoading = true) }
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma29296181() = AuditRoot(true) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircleLoadingSpinner(isLoading = true) }
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma29296187() = AuditRoot(true) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircleLoadingSpinner(isLoading = true) }
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma29296283() = AuditRoot(true) {
    AuditOverlay(background = { AuditHome() }) {
        DialogWIthImageContent("서버와의 통신이 원활하지 않습니다.\n인터넷 연결 상태를 확인해 주세요.", "", R.drawable.icn_network_error, {}, {}, positiveText = "다시 시도", negativeText = "종료하기", titleTextStyle = TypographyEnum.Heading4)
    }
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma29296397() = AuditRoot(false) {
    AuditOverlay(background = { AuditHome() }) {
        UpdateDialogContent({})
    }
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma29296502() = AuditRoot(false) {
    AuditOverlay(background = { AuditHome() }) {
        DialogWIthImageContent("알림 수신 안내", "알림을 켜면 매일 오전 9시에\n오늘의 필사를 받아보실 수 있습니다.", R.drawable.icn_bell_fill, {}, {}, titleTextStyle = TypographyEnum.Heading4)
    }
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma29296626() = AuditRoot(true) {
    AuditOverlay(background = { AuditHome() }) {
        UpdateDialogContent({})
    }
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma29296747() = AuditRoot(true) {
    AuditOverlay(background = { AuditHome() }) {
        DialogWIthImageContent("알림 수신 안내", "알림을 켜면 매일 오전 9시에\n오늘의 필사를 받아보실 수 있습니다.", R.drawable.icn_bell_fill, {}, {}, titleTextStyle = TypographyEnum.Heading4)
    }
}


@Preview(widthDp = 360, heightDp = 300, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma29296610() = AuditRoot(false) {
    AuditOverlay(background = {  }) {
        UpdateDialogContent({})
    }
}


@Preview(widthDp = 360, heightDp = 300, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma29296732() = AuditRoot(true) {
    AuditOverlay(background = {  }) {
        UpdateDialogContent({})
    }
}


@Preview(widthDp = 360, heightDp = 300, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma29296856() = AuditRoot(true) {
    AuditOverlay(background = {  }) {
        DialogWIthImageContent("알림 수신 안내", "알림을 켜면 매일 오전 9시에\n오늘의 필사를 받아보실 수 있습니다.", R.drawable.icn_bell_fill, {}, {}, titleTextStyle = TypographyEnum.Heading4)
    }
}
