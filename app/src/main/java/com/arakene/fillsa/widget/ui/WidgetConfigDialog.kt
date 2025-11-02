package com.arakene.fillsa.widget.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.arakene.presentation.R
import com.arakene.presentation.ui.common.PositiveButton
import com.arakene.presentation.ui.mypage.ThemeItem
import com.arakene.presentation.ui.theme.FillsaTheme

@Composable
fun WidgetConfigDialog(
    items: List<String>,
    selected: String,
    setSelected: (String) -> Unit,
    visible: Boolean,
    dismiss: () -> Unit,
    modifier: Modifier = Modifier) {

    var localSelected by remember{
        mutableStateOf(selected)
    }

    if (visible){
        Dialog(
            onDismissRequest = dismiss
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(FillsaTheme.colorScheme.backgroundContainer)
                    .padding(horizontal = 12.dp)
                    .padding(top = 20.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(30.dp)
            ) {

                items.forEach { item ->
                    ThemeItem(
                        text = item,
                        selected = item == localSelected,
                        setSelected = {
                            localSelected = item
                        },
                        selectedImage = painterResource(com.arakene.data.R.drawable.icn_radio_selected_purple)
                    )
                }


                PositiveButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.ok),
                    onClick = {
                        setSelected(localSelected)
                        dismiss()
                    }
                )

            }

        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    FillsaTheme {
        WidgetConfigDialog(
            items = listOf("작게", "중간", "크게",),
            selected = "작게",
            setSelected = {},
            visible = true,
            dismiss = {}

        )
    }
}