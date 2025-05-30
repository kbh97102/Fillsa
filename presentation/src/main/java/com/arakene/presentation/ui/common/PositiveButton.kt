package com.arakene.presentation.ui.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.ui.theme.positiveButtonColors

@Composable
fun PositiveButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Button(
        modifier = modifier,
        onClick = onClick,
        colors = MaterialTheme.colorScheme.positiveButtonColors,
        contentPadding = PaddingValues(horizontal = 34.5.dp, vertical = 15.dp),
        shape = MaterialTheme.shapes.small
    ) {
        Text(text, style = FillsaTheme.typography.buttonMediumBold, color = Color.White)
    }

}


@Composable
@Preview
private fun PositiveButtonPreview() {
    FillsaTheme {
        PositiveButton(
            text = "로그인 하기",
            onClick = {}
        )
    }
}