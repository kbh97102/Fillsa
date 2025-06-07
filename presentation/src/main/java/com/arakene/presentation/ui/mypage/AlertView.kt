package com.arakene.presentation.ui.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arakene.presentation.R
import com.arakene.presentation.ui.common.HeaderSection
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.DialogData
import com.arakene.presentation.util.DialogDataHolder
import com.arakene.presentation.util.LocalDialogDataHolder
import com.arakene.presentation.util.MyPageAction
import com.arakene.presentation.util.noEffectClickable
import com.arakene.presentation.viewmodel.MyPageViewModel

@Composable
fun AlertView(
    modifier: Modifier = Modifier,
    viewModel: MyPageViewModel = hiltViewModel(),
    dialogDataHolder: DialogDataHolder = LocalDialogDataHolder.current
) {

    val context = LocalContext.current

    // TODO: 이게 정말 좋은 구조일까? 그냥 텍스트 크기정도만 조절 가능하도록 하는게 좋지 않을까
    val titleTextStyle = FillsaTheme.typography.heading4
    val bodyTextStyle = FillsaTheme.typography.body2

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        HeaderSection(
            stringResource(R.string.alert),
            onBackPress = {}
        )

        AlertSwitchSection()

        Row(
            modifier = Modifier
                .padding(top = 50.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondary)
                .padding(horizontal = 20.dp, vertical = 19.dp)
                .noEffectClickable {
                    dialogDataHolder.data = DialogData.Builder()
                        .title(context.getString(R.string.resign_title))
                        .titleTextStyle(titleTextStyle)
                        .bodyTextStyle(bodyTextStyle)
                        .body(context.getString(R.string.resign_body))
                        .reversed(true)
                        .okText(context.getString(R.string.cancel))
                        .cancelText(context.getString(R.string.resign))
                        .cancelOnClick {
                            viewModel.handleContract(MyPageAction.Resign)
                        }
                        .build()

                    dialogDataHolder.show = true
                },
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                stringResource(R.string.resign),
                style = FillsaTheme.typography.body2,
                color = colorResource(R.color.gray_700)
            )

            Image(painter = painterResource(R.drawable.icn_sign_out), contentDescription = null)

        }

    }

}