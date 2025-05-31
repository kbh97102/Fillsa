package com.arakene.presentation.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitHorizontalDragOrCancellation
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.noEffectClickable
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun DailyQuotaSection(
    text: String,
    author: String,
    next: () -> Unit,
    before: () -> Unit,
    navigate: () -> Unit,
    modifier: Modifier = Modifier
) {

    SubcomposeLayout(modifier = modifier.fillMaxWidth()) { constraints ->
        val rest = subcompose("WiseSayingRest") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(320f / 250f)
                    .shadow(
                        3.dp,
                        shape = MaterialTheme.shapes.medium,
                        ambientColor = colorResource(R.color.gray_cb).copy(alpha = 0.7f)
                    )
                    .background(
                        MaterialTheme.colorScheme.secondary,
                        shape = MaterialTheme.shapes.medium
                    )
                    .noEffectClickable {
                        navigate()
                    }
                    .pointerInput(Unit) {
                        while (true) {
                            awaitPointerEventScope {

                                val down = awaitFirstDown()
                                var totalDrag = 0f

                                // 드래그 추적 시작
                                var drag = awaitHorizontalDragOrCancellation(down.id)
                                while (drag != null) {
                                    totalDrag += drag.positionChange().x
                                    drag = awaitHorizontalDragOrCancellation(drag.id)
                                }

                                // 손을 뗀 시점에서 판단
                                if (abs(totalDrag) > 100) { // threshold 조정 가능
                                    if (totalDrag > 0) {
                                        before()
                                    } else {
                                        next()
                                    }
                                }

                            }
                        }
                    }
                ,
                contentAlignment = Alignment.Center
            ) {

                Image(
                    painter = painterResource(R.drawable.img_wise_saying_background),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )

                Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = text,
                        style = FillsaTheme.typography.body2,
                        color = colorResource(R.color.gray_700),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        text = author,
                        style = FillsaTheme.typography.body2,
                        color = colorResource(R.color.gray_700),
                        textDecoration = TextDecoration.Underline,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
            .map {
                it.measure(constraints)
            }.first()

        val leftArrow = subcompose("WireSayingLeftArrow") {
            Image(
                painterResource(R.drawable.icn_arrow_circle),
                contentDescription = null,
                modifier = Modifier.noEffectClickable { before() })
        }.map {
            it.measure(Constraints())
        }.first()

        val rightArrow = subcompose("WireSayingRightArrow") {
            Image(
                painterResource(R.drawable.icn_arrow_circle),
                contentDescription = null,
                modifier = Modifier
                    .rotate(180f)
                    .noEffectClickable { next() }
            )
        }.map {
            it.measure(Constraints())
        }.first()

        layout(rest.measuredWidth, rest.measuredHeight) {
            rest.placeRelative(0, 0)

            leftArrow.placeRelative(
                0 - leftArrow.measuredWidth / 2f.roundToInt(),
                rest.measuredHeight / 2f.roundToInt() - leftArrow.measuredHeight / 2f.roundToInt()
            )
            rightArrow.placeRelative(
                rest.measuredWidth - leftArrow.measuredWidth / 2f.roundToInt(),
                rest.measuredHeight / 2f.roundToInt() - leftArrow.measuredHeight / 2f.roundToInt()
            )
        }
    }


}

@Preview(widthDp = 500, heightDp = 500, showBackground = true)
@Composable
private fun WiseSayingSectionPreview() {
    FillsaTheme {
        DailyQuotaSection(
            text = "상황을 가장 잘 활용하는 사람이 가장 좋은 상황을 맞는다.",
            author = "jone wooden",
            next = {},
            before = {},
            navigate = {},
            modifier = Modifier.padding(50.dp)
        )
    }
}