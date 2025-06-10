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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.CommonEffect
import com.arakene.presentation.util.HandleViewEffect
import com.arakene.presentation.util.MyPageAction
import com.arakene.presentation.util.MyPageScreens
import com.arakene.presentation.util.Navigate
import com.arakene.presentation.viewmodel.MyPageViewModel

@Composable
fun MyPageView(
    navigate: Navigate,
    modifier: Modifier = Modifier,
    viewModel: MyPageViewModel = hiltViewModel()
) {

    val isLogged by viewModel.isLogged.collectAsState(false)

    val lifeCycle = LocalLifecycleOwner.current

    HandleViewEffect(
        viewModel.effect,
        lifeCycle
    ) {
        when (it) {
            is CommonEffect.Move -> {
                navigate(it.screen)
            }
        }
    }


    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 20.dp)
    ) {
        // LOGO
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp), horizontalArrangement = Arrangement.Center
        ) {
            Image(painterResource(R.drawable.icn_logo), contentDescription = null)
        }

        // Login or UserName
        MyPageLoginSection(
            modifier = Modifier.padding(top = 10.dp),
            isLogged = isLogged
        )

        // Notice
        MyPageItem(
            image = painterResource(R.drawable.icn_info),
            text = stringResource(R.string.notice),
            onClick = {
                viewModel.handleContract(CommonEffect.Move(MyPageScreens.Notice))
            },
            modifier = Modifier.padding(top = 20.dp)
        )

        // Alert
        MyPageItem(
            image = painterResource(R.drawable.icn_bell),
            text = stringResource(R.string.alert),
            onClick = {
                viewModel.handleContract(CommonEffect.Move(MyPageScreens.Alert))
            },
            modifier = Modifier.padding(top = 20.dp)
        )

        // Theme
        MyPageItem(
            image = painterResource(R.drawable.icn_theme),
            text = stringResource(R.string.theme),
            onClick = {},
            modifier = Modifier.padding(top = 20.dp)
        )

        // version + logout
        MyPageBottomButtonSection(
            modifier = modifier.padding(top = 20.dp),
            isLogged = isLogged,
            logout = {
                viewModel.handleContract(MyPageAction.Logout)
            })
    }

}

@Preview
@Composable
private fun MyPageViewPreview() {
    FillsaTheme {
        MyPageView(
            navigate = {}
        )
    }
}