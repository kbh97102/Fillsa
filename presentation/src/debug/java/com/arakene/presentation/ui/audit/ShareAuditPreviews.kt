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

@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292910850() = AuditRoot(true) {
    ShareContent(AuditQuote, "존 우든", {}, false, {})
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292910884() = AuditRoot(true) {
    ShareContent(AuditQuote, "존 우든", {}, true, {})
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292918239() = AuditRoot(false) {
    ShareContent(AuditQuote, "존 우든", {}, true, {})
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292918543() = AuditRoot(false) {
    ShareContent(AuditQuote, "존 우든", {}, false, {})
}


@Preview(widthDp = 270, heightDp = 400, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292910923() = AuditRoot(true) {
    ShareItem(AuditQuote, "존 우든", null, Modifier.fillMaxSize(), R.drawable.img_share_background_1, Color(0xFF212121))
}


@Preview(widthDp = 270, heightDp = 400, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292910927() = AuditRoot(true) {
    ShareItem(AuditQuote, "존 우든", null, Modifier.fillMaxSize(), R.drawable.img_share_background_2, Color.White)
}


@Preview(widthDp = 270, heightDp = 400, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292910931() = AuditRoot(true) {
    ShareItem(AuditQuote, "존 우든", null, Modifier.fillMaxSize(), R.drawable.img_share_background_3, Color(0xFF212121))
}


@Preview(widthDp = 270, heightDp = 400, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292910936() = AuditRoot(true) {
    ShareItem(AuditQuote, "존 우든", null, Modifier.fillMaxSize(), R.drawable.img_share_background_10, Color(0xFF212121))
}


@Preview(widthDp = 270, heightDp = 400, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292910941() = AuditRoot(true) {
    ShareItem(AuditQuote, "존 우든", null, Modifier.fillMaxSize(), R.drawable.img_share_background_8, Color.White)
}


@Preview(widthDp = 270, heightDp = 400, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292910949() = AuditRoot(true) {
    ShareItem(AuditQuote, "존 우든", null, Modifier.fillMaxSize(), R.drawable.img_share_background_4, Color(0xFF212121))
}


@Preview(widthDp = 270, heightDp = 400, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292910963() = AuditRoot(true) {
    ShareItem(AuditQuote, "존 우든", null, Modifier.fillMaxSize(), R.drawable.img_share_background_5, Color(0xFF212121))
}


@Preview(widthDp = 270, heightDp = 400, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292910968() = AuditRoot(true) {
    ShareItem(AuditQuote, "존 우든", null, Modifier.fillMaxSize(), R.drawable.img_share_background_7, Color(0xFF212121))
}


@Preview(widthDp = 270, heightDp = 400, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292918577() = AuditRoot(false) {
    ShareItem(AuditQuote, "존 우든", null, Modifier.fillMaxSize(), R.drawable.img_share_background_1, Color(0xFF212121))
}


@Preview(widthDp = 270, heightDp = 400, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292918581() = AuditRoot(false) {
    ShareItem(AuditQuote, "존 우든", null, Modifier.fillMaxSize(), R.drawable.img_share_background_2, Color.White)
}


@Preview(widthDp = 270, heightDp = 400, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292918585() = AuditRoot(false) {
    ShareItem(AuditQuote, "존 우든", null, Modifier.fillMaxSize(), R.drawable.img_share_background_3, Color(0xFF212121))
}


@Preview(widthDp = 270, heightDp = 400, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292918589() = AuditRoot(false) {
    ShareItem(AuditQuote, "존 우든", null, Modifier.fillMaxSize(), R.drawable.img_share_background_10, Color(0xFF212121))
}


@Preview(widthDp = 270, heightDp = 400, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292918593() = AuditRoot(false) {
    ShareItem(AuditQuote, "존 우든", null, Modifier.fillMaxSize(), R.drawable.img_share_background_8, Color.White)
}


@Preview(widthDp = 270, heightDp = 400, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292918601() = AuditRoot(false) {
    ShareItem(AuditQuote, "존 우든", null, Modifier.fillMaxSize(), R.drawable.img_share_background_4, Color(0xFF212121))
}


@Preview(widthDp = 270, heightDp = 400, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292918611() = AuditRoot(false) {
    ShareItem(AuditQuote, "존 우든", null, Modifier.fillMaxSize(), R.drawable.img_share_background_5, Color(0xFF212121))
}


@Preview(widthDp = 270, heightDp = 400, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292918615() = AuditRoot(false) {
    ShareItem(AuditQuote, "존 우든", null, Modifier.fillMaxSize(), R.drawable.img_share_background_7, Color(0xFF212121))
}
