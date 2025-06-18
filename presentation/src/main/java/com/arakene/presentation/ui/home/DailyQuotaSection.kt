package com.arakene.presentation.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.arakene.presentation.R
import com.arakene.presentation.ui.common.AuthorText
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.DateCondition
import com.arakene.presentation.util.noEffectClickable
import java.time.LocalDate
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun DailyQuotaSection(
    text: String,
    author: String,
    next: () -> Unit,
    before: () -> Unit,
    navigate: () -> Unit,
    date: LocalDate,
    modifier: Modifier = Modifier
) {

    var amount by remember {
        mutableStateOf(0f)
    }

    val displayBeforeButton by remember(date) {
        mutableStateOf(date > DateCondition.startDay)
    }

    val displayNextButton by remember(date) {
        mutableStateOf(date < LocalDate.now())
    }

    SubcomposeLayout(
        modifier = modifier
            .fillMaxWidth()
    ) { constraints ->
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
                        detectDragGestures(
                            onDragEnd = {
                                if (abs(amount) > 150) {
                                    if (amount > 0) {
                                        before()
                                    } else {
                                        next()
                                    }
                                }
                                amount = 0f
                            }
                        ) { change, dragAmount ->
                            amount += dragAmount.x
                        }
                    },
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

                    AuthorText(
                        author = author,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    )
                }
            }
        }
            .map {
                it.measure(constraints)
            }.first()

        val leftArrow = subcompose("WireSayingLeftArrow") {
            if (displayBeforeButton) {
                Image(
                    painterResource(R.drawable.icn_arrow_circle),
                    contentDescription = null,
                    modifier = Modifier.noEffectClickable { before() })
            }
        }.map {
            it.measure(Constraints())
        }.firstOrNull()

        val rightArrow = subcompose("WireSayingRightArrow") {
            if (displayNextButton) {
                Image(
                    painterResource(R.drawable.icn_arrow_circle),
                    contentDescription = null,
                    modifier = Modifier
                        .rotate(180f)
                        .noEffectClickable { next() }
                )
            }
        }.map {
            it.measure(Constraints())
        }.firstOrNull()

        layout(rest.measuredWidth, rest.measuredHeight) {
            rest.placeRelative(0, 0)

            leftArrow?.placeRelative(
                0 - leftArrow.measuredWidth / 2f.roundToInt(),
                rest.measuredHeight / 2f.roundToInt() - leftArrow.measuredHeight / 2f.roundToInt()
            )
            rightArrow?.placeRelative(
                rest.measuredWidth - rightArrow.measuredWidth / 2f.roundToInt(),
                rest.measuredHeight / 2f.roundToInt() - rightArrow.measuredHeight / 2f.roundToInt()
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
            date = LocalDate.now(),
            modifier = Modifier.padding(50.dp)
        )
    }
}