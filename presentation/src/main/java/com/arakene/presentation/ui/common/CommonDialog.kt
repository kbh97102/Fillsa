package com.arakene.presentation.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme

@Composable
fun CommonDialog(
    title: String,
    negativeText: String,
    negativeOnClick: () -> Unit,
    positiveText: String,
    positiveOnClick: () -> Unit,
    reversed: Boolean = false
) {

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(MaterialTheme.shapes.small)
                .background(Color.White)
                .padding(12.dp)
        ) {

            Text(
                title,
                style = FillsaTheme.typography.subtitle1,
                color = colorResource(R.color.gray_700),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 52.dp),
                textAlign = TextAlign.Center,
            )

            Row(
                modifier = Modifier.padding(top = 44.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // TODO: 이거 리스트로 관리하는 방법도 있긴한데 그건 weight에서 문제가 생김 , 다른방법 없나?
                if (!reversed) {
                    NegativeButton(
                        modifier = Modifier.weight(1f),
                        text = negativeText,
                        onClick = negativeOnClick
                    )

                    PositiveButton(
                        modifier = Modifier.weight(1f),
                        text = positiveText,
                        onClick = positiveOnClick
                    )
                } else {
                    PositiveButton(
                        modifier = Modifier.weight(1f),
                        text = negativeText,
                        onClick = negativeOnClick
                    )

                    NegativeButton(
                        modifier = Modifier.weight(1f),
                        text = positiveText,
                        onClick = positiveOnClick
                    )
                }

            }

        }


    }
}

@Composable
@Preview
private fun CommonDialogPreview() {
    FillsaTheme {
        CommonDialog(
            title = "로그인 후 사용하실 수 있습니다.",
            positiveText = "로그인 하기",
            positiveOnClick = {},
            negativeText = "취소",
            negativeOnClick = {}
        )
    }
}