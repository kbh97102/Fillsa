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
internal fun AuditFigma308728815() = AuditRoot(true) {
    AuditTyping(true, written = false)
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma29299764() = AuditRoot(true) {
    AuditTyping(true, written = true)
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma29299801() = AuditRoot(true) {
    AuditOverlay(background = { AuditTyping(true, written = true) }, dim = if (IsDarkMode.current) 0.485f else 0.694f) {
        TypingResultContent(TypingResultKind.Continue, "오늘도 이어서 필사를 해주세요!", true, {}, {})
    }
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma29299844() = AuditRoot(true) {
    AuditOverlay(background = { AuditTyping(true, written = true) }, dim = if (IsDarkMode.current) 0.485f else 0.694f) {
        TypingResultContent(TypingResultKind.Streak, "연속 필사 완료!", true, {}, {})
    }
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma29299884() = AuditRoot(true) {
    AuditOverlay(background = { AuditTyping(true, written = true) }, dim = if (IsDarkMode.current) 0.485f else 0.694f) {
        TypingResultContent(TypingResultKind.Incomplete, "필사가 완료되지 않았어요.", true, {}, {})
    }
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292917920() = AuditRoot(false) {
    AuditTyping(false, written = false)
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292918278() = AuditRoot(false) {
    AuditTyping(false, written = true)
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma298823906() = AuditRoot(false) {
    AuditOverlay(background = { AuditTyping(false, written = true) }, dim = if (IsDarkMode.current) 0.485f else 0.694f) {
        TypingResultContent(TypingResultKind.Continue, "오늘도 이어서 필사를 해주세요!", false, {}, {})
    }
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma298823962() = AuditRoot(false) {
    AuditOverlay(background = { AuditTyping(false, written = true) }, dim = if (IsDarkMode.current) 0.485f else 0.694f) {
        TypingResultContent(TypingResultKind.Streak, "연속 필사 완료!", false, {}, {})
    }
}


@Preview(widthDp = 360, heightDp = 672, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma298824015() = AuditRoot(false) {
    AuditOverlay(background = { AuditTyping(false, written = true) }, dim = if (IsDarkMode.current) 0.485f else 0.694f) {
        TypingResultContent(TypingResultKind.Incomplete, "필사가 완료되지 않았어요.", false, {}, {})
    }
}


@Preview(widthDp = 320, heightDp = 245, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292910996() = AuditRoot(true) {
    TypingResultContent(TypingResultKind.Continue, "오늘도 이어서 필사를 해주세요!", true, {}, {})
}


@Preview(widthDp = 320, heightDp = 245, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma302524578() = AuditRoot(true) {
    TypingResultContent(TypingResultKind.Streak, "연속 필사 완료!", true, {}, {})
}


@Preview(widthDp = 320, heightDp = 245, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma302524618() = AuditRoot(true) {
    TypingResultContent(TypingResultKind.Incomplete, "필사가 완료되지 않았어요.", true, {}, {})
}


@Preview(widthDp = 320, heightDp = 243, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma298824099() = AuditRoot(false) {
    TypingResultContent(TypingResultKind.Continue, "오늘도 이어서 필사를 해주세요!", false, {}, {})
}


@Preview(widthDp = 320, heightDp = 243, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma298824127() = AuditRoot(false) {
    TypingResultContent(TypingResultKind.Streak, "연속 필사 완료!", false, {}, {})
}


@Preview(widthDp = 320, heightDp = 243, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma302524232() = AuditRoot(false) {
    TypingResultContent(TypingResultKind.Incomplete, "필사가 완료되지 않았어요.", false, {}, {})
}
