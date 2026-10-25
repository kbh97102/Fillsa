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

@Preview(widthDp = 360, heightDp = 1053, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma3051899() = AuditRoot(true, "com.arakene.presentation.util.Screens.Calendar") {
    AuditCalendar(CalendarRuntimeQaState.CompletedUnanswered)
}


@Preview(widthDp = 360, heightDp = 1053, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma30511130() = AuditRoot(true, "com.arakene.presentation.util.Screens.Calendar") {
    AuditCalendar(CalendarRuntimeQaState.CompletedAnsweredImage)
}


@Preview(widthDp = 360, heightDp = 768, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma303924906() = AuditRoot(true, "com.arakene.presentation.util.Screens.Calendar") {
    AuditCalendar(CalendarRuntimeQaState.Basic)
}


@Preview(widthDp = 360, heightDp = 768, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma303928370() = AuditRoot(true, "com.arakene.presentation.util.Screens.Calendar") {
    AuditCalendar(CalendarRuntimeQaState.Basic)
}


@Preview(widthDp = 360, heightDp = 768, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma298521952() = AuditRoot(false, "com.arakene.presentation.util.Screens.Calendar") {
    AuditCalendar(CalendarRuntimeQaState.Basic)
}


@Preview(widthDp = 360, heightDp = 1053, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma298522510() = AuditRoot(false, "com.arakene.presentation.util.Screens.Calendar") {
    AuditCalendar(CalendarRuntimeQaState.CompletedAnsweredImage)
}


@Preview(widthDp = 360, heightDp = 1053, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma298722796() = AuditRoot(false, "com.arakene.presentation.util.Screens.Calendar") {
    AuditCalendar(CalendarRuntimeQaState.CompletedUnanswered)
}
