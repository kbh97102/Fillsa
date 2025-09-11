package com.arakene.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.arakene.presentation.R
import com.arakene.presentation.util.FillsaColorScheme


val ColorScheme.defaultButtonColors: ButtonColors
    get() {
        return ButtonColors(
            contentColor = Color.White,
            containerColor = Color.White,
            disabledContentColor = Color.White,
            disabledContainerColor = Color.White
        )
    }

val ColorScheme.positiveButtonColors: ButtonColors
    get() {
        return ButtonColors(
            contentColor = Purple01,
            containerColor = Purple01,
            disabledContentColor = Purple01,
            disabledContainerColor = Purple01
        )
    }

internal val fillsaTypo = FillsaTypo(
    heading1 = TextStyle(
        fontWeight = FontWeight.Bold,
        fontFamily = pretendard,
        fontSize = 32.sp,
        lineHeight = 48.sp
    ),
    heading2 = TextStyle(
        fontWeight = FontWeight.Bold,
        fontFamily = pretendard,
        fontSize = 28.sp,
        lineHeight = 42.sp
    ),
    heading3 = TextStyle(
        fontWeight = FontWeight.Bold,
        fontFamily = pretendard,
        fontSize = 24.sp,
        lineHeight = 36.sp
    ),
    heading4 = TextStyle(
        fontWeight = FontWeight.Bold,
        fontFamily = pretendard,
        fontSize = 20.sp,
        lineHeight = 30.sp
    ),
    subtitle1 = TextStyle(
        fontWeight = FontWeight.Bold,
        fontFamily = pretendard,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    subtitle2 = TextStyle(
        fontWeight = FontWeight.Bold,
        fontFamily = pretendard,
        fontSize = 14.sp,
        lineHeight = 21.sp
    ),
    body1 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontFamily = pretendard,
        fontSize = 20.sp,
        lineHeight = 30.sp
    ),
    body2 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontFamily = pretendard,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    body3 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontFamily = pretendard,
        fontSize = 14.sp,
        lineHeight = 21.sp
    ),
    body4 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontFamily = pretendard,
        fontSize = 12.sp,
        lineHeight = 18.sp
    ),
    buttonLargeBold = TextStyle(
        fontWeight = FontWeight.Bold,
        fontFamily = pretendard,
        fontSize = 20.sp,
    ),
    buttonLargeNormal = TextStyle(
        fontWeight = FontWeight.Normal,
        fontFamily = pretendard,
        fontSize = 20.sp,
    ),
    buttonMediumBold = TextStyle(
        fontWeight = FontWeight.Bold,
        fontFamily = pretendard,
        fontSize = 16.sp,
    ),
    buttonMediumNormal = TextStyle(
        fontWeight = FontWeight.Normal,
        fontFamily = pretendard,
        fontSize = 16.sp
    ),
    buttonSmallBold = TextStyle(
        fontWeight = FontWeight.Bold,
        fontFamily = pretendard,
        fontSize = 14.sp,
    ),
    buttonSmallNormal = TextStyle(
        fontWeight = FontWeight.Normal,
        fontFamily = pretendard,
        fontSize = 14.sp,
    ),
    buttonXSmallBold = TextStyle(
        fontWeight = FontWeight.Bold,
        fontFamily = pretendard,
        fontSize = 12.sp,
    ),
    buttonXSmallNormal = TextStyle(
        fontWeight = FontWeight.Normal,
        fontFamily = pretendard,
        fontSize = 12.sp,
    ),
    quote = TextStyle(
        fontWeight = FontWeight.Normal,
        fontFamily = gangwoneduall,
        fontSize = 16.sp,
        lineHeight = 24.sp
    )
)

@Composable
fun FillsaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }

        darkTheme -> FillsaColorScheme(
            background = colorResource(R.color.gray_700),
            onBackground1 = colorResource(R.color.white),
            onBackground2 = colorResource(R.color.purple01),
            backgroundContainer = colorResource(R.color.gray_600),
            primaryContainer = colorResource(R.color.gray_600),
            onPrimaryContainer = colorResource(R.color.white),
            outline = colorResource(R.color.gray_500),
            outlineVariant = colorResource(R.color.gray_200),
            toastMessageBackground = colorResource(R.color.gray_500),
            onToastMessage1 = colorResource(R.color.white),
            onToastMessage2 = colorResource(R.color.green_1a),
            backgroundDim = colorResource(R.color.gray_700).copy(alpha = 0.8f),
            // common
            secondaryContainer = colorResource(R.color.primary),
            onSecondaryContainer1 = colorResource(R.color.primary),
            onSecondaryContainer2 = colorResource(R.color.primary),
            tertiaryContainer = colorResource(R.color.primary),
            onTertiaryContainer = colorResource(R.color.primary),
            tertiaryOutline1 = colorResource(R.color.primary),
            tertiaryOutline2 = colorResource(R.color.primary),
            tertiary = colorResource(R.color.primary),
            onTertiary1 = colorResource(R.color.primary),
            onTertiary2 = colorResource(R.color.primary),
        )

        else -> FillsaColorScheme(
            background = colorResource(R.color.primary),
            onBackground1 = colorResource(R.color.gray_700),
            onBackground2 = colorResource(R.color.purple01),
            backgroundContainer = colorResource(R.color.white),
            primaryContainer = colorResource(R.color.purple01),
            onPrimaryContainer = colorResource(R.color.white),
            outline = colorResource(R.color.purple01),
            outlineVariant = colorResource(R.color.gray_200),
            toastMessageBackground = colorResource(R.color.gray_700),
            onToastMessage1 = colorResource(R.color.white),
            onToastMessage2 = colorResource(R.color.green_1a),
            backgroundDim = colorResource(R.color.gray_700).copy(alpha = 0.8f),
            // common
            secondaryContainer = colorResource(R.color.primary),
            onSecondaryContainer1 = colorResource(R.color.primary),
            onSecondaryContainer2 = colorResource(R.color.primary),
            tertiaryContainer = colorResource(R.color.primary),
            onTertiaryContainer = colorResource(R.color.primary),
            tertiaryOutline1 = colorResource(R.color.primary),
            tertiaryOutline2 = colorResource(R.color.primary),
            tertiary = colorResource(R.color.primary),
            onTertiary1 = colorResource(R.color.primary),
            onTertiary2 = colorResource(R.color.primary),
        )
    }

    CompositionLocalProvider(
        LocalFillsaTypo provides fillsaTypo,
        LocalFillsaColorScheme provides colorScheme
    ) {
        MaterialTheme(
            content = content
        )
    }
}