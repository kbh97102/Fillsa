package com.arakene.presentation.ui.mypage

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.arakene.presentation.R
import com.arakene.presentation.ui.common.PositiveButton
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.IsDarkMode
import com.arakene.presentation.util.noEffectClickable

@Composable
fun ThemeDialog(
    dismiss: () -> Unit,
    changeThemeToDarkMode: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {

    val darkMode = IsDarkMode.current

    var darkModeState by remember {
        mutableStateOf(darkMode)
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

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(MaterialTheme.shapes.small)
                .background(FillsaTheme.colorScheme.backgroundContainer)
                .padding(horizontal = 12.dp)
                .padding(top = 20.dp, bottom = 12.dp),
        ) {
            ThemeItem(
                "라이트",
                darkModeState == false,
                setSelected = {
                    darkModeState = false
                },
                modifier = Modifier
            )

            Spacer(Modifier.height(15.dp))

            ThemeItem(
                "다크",
                darkModeState == true,
                setSelected = {
                    darkModeState = true
                }
            )

            Spacer(Modifier.height(24.dp))

            PositiveButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.ok),
                onClick = {
                    changeThemeToDarkMode(darkModeState)
                    dismiss()
                }
            )

        }

    }
}


@Composable
private fun ThemeItem(
    text: String,
    selected: Boolean,
    setSelected: (Boolean) -> Unit,
    modifier: Modifier = Modifier
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
            Image(painterResource(R.drawable.icn_radio_selected), contentDescription = null)
        } else {
            Image(painterResource(R.drawable.icn_radio_unselected), contentDescription = null)
        }
    }

}