package com.arakene.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.arakene.presentation.R

val pretendard = FontFamily(
    Font(R.font.pretendard_400, FontWeight.Normal, FontStyle.Normal),
    Font(R.font.pretendard_700, FontWeight.Bold, FontStyle.Normal),
)

@Immutable
data class FillsaTypo(
    val heading1: TextStyle = TextStyle.Default,
    val heading2: TextStyle = TextStyle.Default,
    val heading3: TextStyle = TextStyle.Default,
    val heading4: TextStyle = TextStyle.Default,
    val subtitle1: TextStyle = TextStyle.Default,
    val subtitle2: TextStyle = TextStyle.Default,
    val body1: TextStyle = TextStyle.Default,
    val body2: TextStyle = TextStyle.Default,
    val body3: TextStyle = TextStyle.Default,
    val body4: TextStyle = TextStyle.Default,
    val buttonLargeBold: TextStyle = TextStyle.Default,
    val buttonLargeNormal: TextStyle = TextStyle.Default,
    val buttonMediumBold: TextStyle = TextStyle.Default,
    val buttonMediumNormal: TextStyle = TextStyle.Default,
    val buttonSmallBold: TextStyle = TextStyle.Default,
    val buttonSmallNormal: TextStyle = TextStyle.Default,
    val buttonXSmallBold: TextStyle = TextStyle.Default,
    val buttonXSmallNormal: TextStyle = TextStyle.Default
)

object FillsaTheme {
    val typography: FillsaTypo
        @Composable
        get() = LocalFillsaTypo.current
}

val LocalFillsaTypo = staticCompositionLocalOf {
    FillsaTypo()
}