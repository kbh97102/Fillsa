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
internal fun AuditFigma32235985() = AuditRoot(true, "com.arakene.presentation.util.Screens.Home") {
    AuditHome(image = false)
}


@Preview(widthDp = 360, heightDp = 773, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma32236126() = AuditRoot(true, "com.arakene.presentation.util.Screens.Home") {
    AuditHome(image = true)
}


@Preview(widthDp = 360, heightDp = 773, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma32236435() = AuditRoot(true, "com.arakene.presentation.util.Screens.Home") {
    AuditOverlay(background = { AuditHome(image = true) }) {
        ImageDialogContent(AuditHomeQuote, "존우든", "android.resource://${LocalContext.current.packageName}/${R.drawable.home_registered_image_fixture}", {}, {}, {})
    }
}


@Preview(widthDp = 360, heightDp = 773, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma32236600() = AuditRoot(true, "com.arakene.presentation.util.Screens.Home") {
    AuditOverlay(background = { AuditHome(image = true) }) {
        ImageDialogContent(AuditHomeQuote, "존우든", "", {}, {}, {})
    }
}


@Preview(widthDp = 360, heightDp = 773, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma32236912() = AuditRoot(true, "com.arakene.presentation.util.Screens.Home") {
    AuditOverlay(background = { AuditHome(image = true) }) {
        CommonDialogContent("이미지를 삭제하시겠습니까?", {}, {}, body = "삭제 후 이미지를 되돌릴 수 없습니다. 😢", positiveText = "취소", negativeText = "삭제하기", reversed = true, layoutMode = DialogLayoutMode.HomeDarkMeasured)
    }
}


@Preview(widthDp = 360, heightDp = 773, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma298723282() = AuditRoot(false, "com.arakene.presentation.util.Screens.Home") {
    AuditOverlay(background = { AuditHome(image = true) }) {
        ImageDialogContent(AuditHomeQuote, "존우든", "android.resource://${LocalContext.current.packageName}/${R.drawable.home_registered_image_fixture}", {}, {}, {})
    }
}


@Preview(widthDp = 360, heightDp = 773, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma32234982() = AuditRoot(false, "com.arakene.presentation.util.Screens.Home") {
    AuditHome(image = false)
}


@Preview(widthDp = 360, heightDp = 773, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma32234647() = AuditRoot(false, "com.arakene.presentation.util.Screens.Home") {
    AuditHome(image = true)
}


@Preview(widthDp = 360, heightDp = 773, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma32234773() = AuditRoot(false, "com.arakene.presentation.util.Screens.Home") {
    AuditOverlay(background = { AuditHome(image = true) }) {
        ImageDialogContent(AuditHomeQuote, "존우든", "android.resource://${LocalContext.current.packageName}/${R.drawable.home_registered_image_fixture}", {}, {}, {})
    }
}


@Preview(widthDp = 360, heightDp = 773, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292919326() = AuditRoot(false, "com.arakene.presentation.util.Screens.Home") {
    AuditOverlay(background = { AuditHome(image = true) }) {
        ImageDialogContent(AuditHomeQuote, "존우든", "", {}, {}, {})
    }
}


@Preview(widthDp = 360, heightDp = 773, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
internal fun AuditFigma292919488() = AuditRoot(false, "com.arakene.presentation.util.Screens.Home") {
    AuditOverlay(background = { AuditHome(image = true) }) {
        CommonDialogContent("이미지를 삭제하시겠습니까?", {}, {}, body = "삭제 후 이미지를 되돌릴 수 없습니다. 😢", positiveText = "취소", negativeText = "삭제하기", reversed = true, layoutMode = DialogLayoutMode.HomeDarkMeasured)
    }
}
