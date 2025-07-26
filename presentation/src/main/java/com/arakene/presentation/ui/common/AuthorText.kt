package com.arakene.presentation.ui.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.getWikipediaUriString
import com.arakene.presentation.util.noEffectClickable

@Composable
fun AuthorText(
    author: String,
    modifier: Modifier = Modifier,
    style: TextStyle = FillsaTheme.typography.body2,
    color: Color = MaterialTheme.colorScheme.onPrimary
) {

    val uriHandler = LocalUriHandler.current

    Text(
        modifier = modifier
            .noEffectClickable {
                uriHandler.openUri(getWikipediaUriString(author))
            },
        text = author,
        style = style,
        color = color,
        textDecoration = TextDecoration.Underline,
        textAlign = TextAlign.Center
    )

}