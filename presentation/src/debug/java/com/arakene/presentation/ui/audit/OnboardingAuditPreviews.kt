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

@Preview(widthDp = 360, heightDp = 652, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma29294324() = AuditRoot(false) {
    val composition = LottieCompositionFactory.fromRawResSync(LocalContext.current, R.raw.lottie_splash).value
    SplashContent(composition, 0.5f, false)
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma308830089() = AuditRoot(false) {
    IntroduceView(navigate = {}, initialPage = 0)
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma308830215() = AuditRoot(false) {
    IntroduceView(navigate = {}, initialPage = 1)
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma308830412() = AuditRoot(false) {
    IntroduceView(navigate = {}, initialPage = 2)
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma29295948() = AuditRoot(true) {
    val composition = LottieCompositionFactory.fromRawResSync(LocalContext.current, R.raw.lottie_splash_dark).value
    SplashContent(composition, 0.5f, true)
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma311031987() = AuditRoot(true) {
    IntroduceView(navigate = {}, initialPage = 0)
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma311032112() = AuditRoot(true) {
    IntroduceView(navigate = {}, initialPage = 2)
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma311032359() = AuditRoot(true) {
    IntroduceView(navigate = {}, initialPage = 1)
}
