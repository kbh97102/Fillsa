package com.arakene.presentation.ui.common

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.arakene.presentation.R

@Composable
fun CheckBox(
    isLike: Boolean,
    modifier: Modifier = Modifier
) {

    Crossfade(modifier = modifier, targetState = isLike, label = "checkboxFade") { isChecked ->
        val imageRes = if (isChecked) R.drawable.icn_checkbox_on else R.drawable.icn_checkbox_off
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null
        )
    }

}