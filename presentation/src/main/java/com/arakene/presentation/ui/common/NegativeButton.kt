package com.arakene.presentation.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.ui.theme.defaultButtonColors


@Composable
fun NegativeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Button(
        modifier = modifier,
        onClick = onClick,
        colors = MaterialTheme.colorScheme.defaultButtonColors,
        contentPadding = PaddingValues(horizontal = 34.5.dp, vertical = 15.dp),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, color = colorResource(R.color.purple01))
    ) {
        Text(
            text,
            style = FillsaTheme.typography.buttonMediumBold,
            color = colorResource(R.color.purple01)
        )
    }

}


@Composable
@Preview
private fun NegativeButtonPreview() {
    FillsaTheme {
        NegativeButton(
            text = "취소",
            onClick = {}
        )
    }
}