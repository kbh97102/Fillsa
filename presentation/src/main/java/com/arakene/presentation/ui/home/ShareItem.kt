package com.arakene.presentation.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme

@Composable
fun ShareItem(
    quote: String,
    author: String,
    graphicLayer: GraphicsLayer?,
    modifier: Modifier = Modifier,
    backgroundUri: Int = R.drawable.img_share_background_1,
    textColor: Color = colorResource(R.color.gray_700)
) {
    Box(
        modifier = if (graphicLayer != null) {
            modifier.drawWithContent {
                graphicLayer.record {
                    this@drawWithContent.drawContent()
                }
                drawLayer(graphicLayer)
            }
        } else {
            modifier
        }
    ) {

        Image(
            painter = painterResource(backgroundUri),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 13.dp)
                .align(Alignment.Center)
        ) {
            Text(
                text = quote,
                color = textColor,
                style = FillsaTheme.typography.quote,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Text(
                author,
                color = textColor,
                style = FillsaTheme.typography.quote,
                textDecoration = TextDecoration.Underline,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    ShareItem(
        graphicLayer = rememberGraphicsLayer(),
        author = "123",
        quote = "QQaslkdfja;lsdkjf;lksdjf"
    )

}