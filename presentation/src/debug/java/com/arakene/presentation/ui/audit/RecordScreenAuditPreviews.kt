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
internal fun AuditAndroidQuotelistcontentLight() = AuditRoot(false) {
    val paging = remember { MutableStateFlow(PagingData.from(listOf(MemberQuotesResponse().copy(quoteDate = "2026-08-16", quoteDayOfWeek = "SUNDAY", korQuote = AuditQuote, korAuthor = "존 우든")))) }.collectAsLazyPagingItems()
    QuoteListContent(QuoteListState(LocalDate.of(2026,8,1), LocalDate.of(2026,8,16)), paging, {}, {})
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditAndroidQuotelistcontentDark() = AuditRoot(true) {
    val paging = remember { MutableStateFlow(PagingData.from(listOf(MemberQuotesResponse().copy(quoteDate = "2026-08-16", quoteDayOfWeek = "SUNDAY", korQuote = AuditQuote, korAuthor = "존 우든")))) }.collectAsLazyPagingItems()
    QuoteListContent(QuoteListState(LocalDate.of(2026,8,1), LocalDate.of(2026,8,16)), paging, {}, {})
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditAndroidQuotedetailcontentLight() = AuditRoot(false) {
    QuoteDetailContent(AuditQuote, "존 우든", "기록한 메모", "", LocaleType.KOR, {}, "1", {})
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditAndroidQuotedetailcontentDark() = AuditRoot(true) {
    QuoteDetailContent(AuditQuote, "존 우든", "기록한 메모", "", LocaleType.KOR, {}, "1", {})
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditAndroidMemoinsertcontentLight() = AuditRoot(false) {
    MemoInsertContent("기록한 메모", {}, {}, {})
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditAndroidMemoinsertcontentDark() = AuditRoot(true) {
    MemoInsertContent("기록한 메모", {}, {}, {})
}
