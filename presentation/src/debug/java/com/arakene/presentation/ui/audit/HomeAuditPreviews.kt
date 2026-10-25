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

@Preview(widthDp = 360, heightDp = 773, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma303926518() = AuditRoot(true, "com.arakene.presentation.util.Screens.Home") {
    AuditHome()
}


@Preview(widthDp = 360, heightDp = 773, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma31391753() = AuditRoot(true, "com.arakene.presentation.util.Screens.Home") {
    AuditHome(calendar = true)
}


@Preview(widthDp = 360, heightDp = 773, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma303926778() = AuditRoot(true, "com.arakene.presentation.util.Screens.Home") {
    AuditHome(image = true, tooltip = true)
}


@Preview(widthDp = 360, heightDp = 773, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma303926996() = AuditRoot(true, "com.arakene.presentation.util.Screens.Home") {
    AuditHome(image = true)
}


@Preview(widthDp = 360, heightDp = 773, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma303927295() = AuditRoot(true, "com.arakene.presentation.util.Screens.Home") {
    AuditHome(liked = true, image = true)
}


@Preview(widthDp = 360, heightDp = 773, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma31361198() = AuditRoot(true, "com.arakene.presentation.util.Screens.Home") {
    AuditHome(editing = true)
}


@Preview(widthDp = 360, heightDp = 773, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma3139910() = AuditRoot(true, "com.arakene.presentation.util.Screens.Home") {
    AuditHome()
}


@Preview(widthDp = 360, heightDp = 773, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma31391061() = AuditRoot(true, "com.arakene.presentation.util.Screens.Home") {
    AuditHome(recorded = true)
}


@Preview(widthDp = 360, heightDp = 773, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma31391238() = AuditRoot(true, "com.arakene.presentation.util.Screens.Home") {
    AuditHome(recorded = true, image = true)
}


@Preview(widthDp = 360, heightDp = 773, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292913556() = AuditRoot(false, "com.arakene.presentation.util.Screens.Home") {
    AuditHome()
}


@Preview(widthDp = 360, heightDp = 773, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma31391501() = AuditRoot(false, "com.arakene.presentation.util.Screens.Home") {
    AuditHome(calendar = true)
}


@Preview(widthDp = 360, heightDp = 773, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292918871() = AuditRoot(false, "com.arakene.presentation.util.Screens.Home") {
    AuditHome(tooltip = true)
}


@Preview(widthDp = 360, heightDp = 773, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292919645() = AuditRoot(false, "com.arakene.presentation.util.Screens.Home") {
    AuditHome()
}


@Preview(widthDp = 360, heightDp = 773, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292919784() = AuditRoot(false, "com.arakene.presentation.util.Screens.Home") {
    AuditHome(recorded = true, liked = true)
}


@Preview(widthDp = 360, heightDp = 773, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma311033783() = AuditRoot(false, "com.arakene.presentation.util.Screens.Home") {
    AuditHome()
}


@Preview(widthDp = 360, heightDp = 773, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma311034293() = AuditRoot(false, "com.arakene.presentation.util.Screens.Home") {
    AuditHome(recorded = true, image = true)
}


@Preview(widthDp = 360, heightDp = 773, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma311034438() = AuditRoot(false, "com.arakene.presentation.util.Screens.Home") {
    AuditHome(recorded = true, image = true)
}


@Preview(widthDp = 360, heightDp = 773, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma311034152() = AuditRoot(false, "com.arakene.presentation.util.Screens.Home") {
    AuditHome(editing = true)
}
