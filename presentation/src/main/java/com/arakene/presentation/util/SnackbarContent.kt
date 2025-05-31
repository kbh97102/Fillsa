package com.arakene.presentation.util

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme

@Composable
fun SnackbarContent(
    message: String,
    displayIcon: Boolean = true
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .background(
                color = colorResource(R.color.gray_700),
                shape = MaterialTheme.shapes.small
            )
            .padding(horizontal = 12.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (displayIcon) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(colorResource(R.color.green_1a)),
                contentAlignment = Alignment.Center
            ) {
                Image(painter = painterResource(R.drawable.icn_check), contentDescription = null)
            }
        }

        Text(
            message,
            style = FillsaTheme.typography.body2,
            color = Color.White,
            modifier = Modifier.padding(start = 8.dp)
        )

    }

}


@Preview(showBackground = true)
@Composable
private fun SnackbarContentPreview() {
    SnackbarContent(
        message = "복사"
    )
}