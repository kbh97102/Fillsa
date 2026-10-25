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
internal fun AuditFigma29293723() = AuditRoot(false) {
    LoginContent(false, false, {}, {}, {}, {}, {}, {})
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma29295815() = AuditRoot(true) {
    LoginContent(true, false, {}, {}, {}, {}, {}, {})
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma29299931() = AuditRoot(true, "com.arakene.presentation.util.Screens.Home") {
    AuditOverlay(background = { AuditHome() }) {
        CommonDialogContent("로그인 후 사용하실 수 있습니다.", {}, {}, positiveText = "로그인 하기", layoutMode = DialogLayoutMode.HomeDarkMeasured)
    }
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292910788() = AuditRoot(true) {
    LoginContent(true, true, {}, {}, {}, {}, {}, {})
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292918449() = AuditRoot(false) {
    LoginContent(false, true, {}, {}, {}, {}, {}, {})
}


@Preview(widthDp = 360, heightDp = 773, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292919027() = AuditRoot(false, "com.arakene.presentation.util.Screens.Home") {
    AuditOverlay(background = { AuditHome() }) {
        CommonDialogContent("로그인 후 사용하실 수 있습니다.", {}, {}, positiveText = "로그인 하기", layoutMode = DialogLayoutMode.HomeDarkMeasured)
    }
}
