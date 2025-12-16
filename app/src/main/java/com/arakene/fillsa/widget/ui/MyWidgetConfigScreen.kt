package com.arakene.fillsa.widget.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.LocalContext
import androidx.glance.currentState
import com.arakene.fillsa.widget.WidgetPrefsKey
import com.arakene.fillsa.widget.dataStore
import com.arakene.presentation.R
import com.arakene.presentation.ui.common.PositiveButton
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.noEffectClickable
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


@Composable
fun MyWidgetConfigScreen(
    onClick: () -> Unit,
    dataStore: DataStore<Preferences>
) {

    val scope = rememberCoroutineScope()

    val data by dataStore.data.collectAsState(null)

    val widgetFontSize by remember(data) {
        mutableStateOf(
            data?.get(WidgetPrefsKey.FONT_SIZE_KEY) ?: "중간"
        )
    }

    val widgetLanguage by remember(data) {
        mutableStateOf(
            data?.get(WidgetPrefsKey.LANGUAGE_KEY) ?: "한국어"
        )
    }


    var languageVisible by remember { mutableStateOf(false) }
    var fontSizeVisible by remember { mutableStateOf(false) }

    val languageOptions = listOf("한국어", "영어")
    val fontSizeOptions = listOf("작게", "중간", "크게")

    var selectedLanguage by remember(widgetLanguage) {
        mutableStateOf(widgetLanguage)
    }

    var selectedFontSize by remember(widgetFontSize) {
        mutableStateOf(widgetFontSize)
    }

    var displayExample by remember {
        mutableStateOf(true)
    }

    WidgetConfigDialog(
        items = languageOptions,
        selected = selectedLanguage,
        setSelected = {
            selectedLanguage = it
        },
        visible = languageVisible,
        dismiss = {
            languageVisible = false
        }
    )

    WidgetConfigDialog(
        items = fontSizeOptions,
        selected = selectedFontSize,
        setSelected = {
            selectedFontSize = it
        },
        visible = fontSizeVisible,
        dismiss = {
            fontSizeVisible = false
        }
    )


    val sizeType by dataStore.data.map {
        it[stringPreferencesKey("SIZE_KEY")] ?: "SMALL"
    }.collectAsState("SMALL")

    LaunchedEffect(sizeType) {
        Log.e(">>>>TEST", "type $sizeType")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painterResource(R.drawable.icn_arrow_black),
                contentDescription = null
            )

            Spacer(Modifier.width(10.dp))

            Text(
                stringResource(com.arakene.fillsa.R.string.widget),
                color = Color.Black,
                style = FillsaTheme.typography.heading4
            )
        }

        HorizontalDivider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 13.dp)
                .noEffectClickable {
                    displayExample = !displayExample
                }
            ,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                stringResource(com.arakene.fillsa.R.string.widget_example_view),
                color = colorResource(
                    R.color.gray_700
                ),
                style = FillsaTheme.typography.body2
            )

            Image(painterResource(R.drawable.icn_arrow_down_black), contentDescription = null,
                modifier = Modifier.rotate(
                    if (displayExample){
                        180f
                    } else {
                        0f
                    }
                ))
        }

        HorizontalDivider()

        if (displayExample) {
            Spacer(Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 60.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                WidgetPreview2x2(
                    isCurrent = sizeType == "SMALL"
                )
                Spacer(Modifier.height(5.dp))
                Text("비율(2x2)", color = Color.Black)

                Spacer(Modifier.height(20.dp))

                WidgetPreview4x2(
                    isCurrent = sizeType != "SMALL"
                )
                Spacer(Modifier.height(5.dp))
                Text("비율(4x2)", color = Color.Black)
            }

            Spacer(Modifier.height(20.dp))

            HorizontalDivider()
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 13.dp)
                .noEffectClickable {
                    languageVisible = true
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                "언어설정",
                color = colorResource(
                    R.color.gray_700
                ),
                style = FillsaTheme.typography.body2
            )

            Image(
                painterResource(R.drawable.icn_arrow_black),
                contentDescription = null,
                modifier = Modifier.rotate(180f)
            )
        }

        HorizontalDivider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 13.dp)
                .noEffectClickable {
                    fontSizeVisible = true
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                "텍스트 크기",
                color = colorResource(
                    R.color.gray_700
                ),
                style = FillsaTheme.typography.body2
            )

            Image(
                painterResource(R.drawable.icn_arrow_black),
                contentDescription = null,
                modifier = Modifier.rotate(180f)
            )
        }

        HorizontalDivider()


        Spacer(Modifier.weight(1f))

        PositiveButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            text = "확인",
            onClick = {
                scope.launch {
                    launch {
                        dataStore.edit { editor ->
                            editor[WidgetPrefsKey.FONT_SIZE_KEY] = selectedFontSize
                            editor[WidgetPrefsKey.LANGUAGE_KEY] = selectedLanguage
                        }
                    }.join()
                    onClick()
                }
            }
        )

        Spacer(Modifier.height(20.dp))

    }


}

@Preview
@Composable
private fun ConfigPreview() {
}