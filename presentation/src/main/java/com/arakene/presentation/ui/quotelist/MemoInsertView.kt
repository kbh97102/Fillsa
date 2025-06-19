package com.arakene.presentation.ui.quotelist

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.arakene.presentation.R
import com.arakene.presentation.ui.common.CustomTextField
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.ui.theme.defaultButtonColors
import com.arakene.presentation.util.CommonAction
import com.arakene.presentation.util.CommonEffect
import com.arakene.presentation.util.HandleViewEffect
import com.arakene.presentation.util.noEffectClickable
import com.arakene.presentation.viewmodel.ListViewModel

@Composable
fun MemoInsertView(
    savedMemo: String,
    memberQuoteSeq: String,
    popBackStack: (String) -> Unit,
    viewModel: ListViewModel = hiltViewModel()
) {

    var memo by remember {
        mutableStateOf(savedMemo)
    }

    val black = colorResource(R.color.gray_700)

    BackHandler {
        popBackStack(memo)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.postSaveMemo(memberQuoteSeq, memo)
        }
    }

    HandleViewEffect(
        viewModel.effect,
        LocalLifecycleOwner.current
    ) {
        when (it) {
            is CommonEffect.PopBackStack -> {
                popBackStack(memo)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 15.dp)
    ) {

        Image(
            painter = painterResource(R.drawable.icn_arrow),
            contentDescription = null,
            modifier = Modifier
                .padding(vertical = 9.dp)
                .noEffectClickable { viewModel.handleContract(CommonAction.PopBackStack) }
        )

        Column(modifier = Modifier.padding(horizontal = 5.dp)) {
            CustomTextField(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .weight(1f),
                value = memo,
                onValueChange = {
                    memo = it
                },
                textStyle = FillsaTheme.typography.body1,
                colors = TextFieldDefaults.colors(
                    unfocusedPlaceholderColor = Color.Transparent,
                    focusedPlaceholderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = black,
                    unfocusedTextColor = black,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                placeholder = {
                    Text(
                        stringResource(R.string.insert_memo),
                        style = FillsaTheme.typography.body1,
                        color = colorResource(R.color.gray_ba)
                    )
                }
            )

            Button(
                modifier = Modifier.padding(vertical = 6.dp),
                border = BorderStroke(1.dp, color = black),
                shape = MaterialTheme.shapes.small,
                colors = MaterialTheme.colorScheme.defaultButtonColors,
                onClick = {
                    popBackStack(memo)
                },
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    stringResource(R.string.out),
                    style = FillsaTheme.typography.body3,
                    color = black
                )
            }
        }

    }

}