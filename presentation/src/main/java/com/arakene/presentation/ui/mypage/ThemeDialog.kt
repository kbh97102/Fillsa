package com.arakene.presentation.ui.mypage

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.arakene.domain.util.DarkModeType
import com.arakene.presentation.R
import com.arakene.presentation.ui.common.PositiveButton
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.IsDarkMode
import com.arakene.presentation.util.noEffectClickable

@Composable
fun ThemeDialog(
    dismiss: () -> Unit,
    currentDarkModeType: DarkModeType,
    changeThemeToDarkMode: (DarkModeType) -> Unit,
    modifier: Modifier = Modifier,
) {

    var localDarkModeType by remember(currentDarkModeType) {
        mutableStateOf(currentDarkModeType)
    }

    Dialog(
        onDismissRequest = {
            dismiss()
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = true
        )
    ) {
        ThemeDialogContent(
            selectedDarkModeType = localDarkModeType,
            onSelect = { localDarkModeType = it },
            onConfirm = {
                changeThemeToDarkMode(localDarkModeType)
                dismiss()
            },
            modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp)
        )
    }
}

@Composable
private fun ThemeDialogContent(
    selectedDarkModeType: DarkModeType,
    onSelect: (DarkModeType) -> Unit,
    onConfirm: () -> Unit,
    darkMode: Boolean = IsDarkMode.current,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(FillsaTheme.colorScheme.backgroundContainer)
            .then(
                if (darkMode) Modifier.border(1.dp, colorResource(R.color.gray_500), MaterialTheme.shapes.small)
                else Modifier
            )
            .padding(horizontal = 12.dp)
            .padding(top = 20.dp, bottom = 12.dp),
    ) {
        ThemeItem("라이트", selectedDarkModeType == DarkModeType.LIGHT, setSelected = { onSelect(DarkModeType.LIGHT) }, darkMode = darkMode)
        Spacer(Modifier.height(30.dp))
        ThemeItem("다크", selectedDarkModeType == DarkModeType.DARK, setSelected = { onSelect(DarkModeType.DARK) }, darkMode = darkMode)
        Spacer(Modifier.height(30.dp))
        ThemeItem("시스템", selectedDarkModeType == DarkModeType.SYSTEM, setSelected = { onSelect(DarkModeType.SYSTEM) }, darkMode = darkMode)
        Spacer(Modifier.height(24.dp))
        PositiveButton(modifier = Modifier.fillMaxWidth(), text = stringResource(R.string.ok), onClick = onConfirm)
    }
}


@Composable
fun ThemeItem(
    text: String,
    selected: Boolean,
    setSelected: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    darkMode: Boolean = IsDarkMode.current,
    selectedImage: Painter = painterResource(if (darkMode) R.drawable.icn_radio_selected else R.drawable.icn_radio_selected_light),
    notSelectedImage: Painter = painterResource(R.drawable.icn_radio_unselected)
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .noEffectClickable {
                setSelected(!selected)
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text,
            style = FillsaTheme.typography.buttonLargeNormal,
            color = FillsaTheme.colorScheme.onBackground1
        )

        if (selected) {
            Image(selectedImage, contentDescription = null)
        } else {
            Image(notSelectedImage, contentDescription = null)
        }
    }

}

@Preview(name = "Theme item light", widthDp = 296, heightDp = 24, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
private fun ThemeItemLightPreview() {
    FillsaTheme(darkTheme = false) {
        ThemeItem(text = "라이트", selected = true, setSelected = {}, darkMode = false)
    }
}

@Preview(name = "Theme popup light", widthDp = 320, heightDp = 237, uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", fontScale = 1f)
@Composable
private fun ThemeDialogLightPreview() {
    FillsaTheme(darkTheme = false) {
        ThemeDialogContent(DarkModeType.LIGHT, onSelect = {}, onConfirm = {}, darkMode = false, modifier = Modifier.fillMaxWidth())
    }
}

@Preview(name = "Theme popup dark", widthDp = 320, heightDp = 237, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", fontScale = 1f)
@Composable
private fun ThemeDialogDarkPreview() {
    FillsaTheme(darkTheme = true) {
        ThemeDialogContent(DarkModeType.DARK, onSelect = {}, onConfirm = {}, darkMode = true, modifier = Modifier.fillMaxWidth())
    }
}
