package com.arakene.presentation.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arakene.domain.model.AdState
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.logDebug
import com.arakene.presentation.viewmodel.AdViewModel

@Composable
fun SingleLineAdSection(
    modifier: Modifier = Modifier,
    refresh: Boolean = false,
) {


    val viewModel: AdViewModel = hiltViewModel()

    val adState by viewModel.adState.collectAsStateWithLifecycle()

    LaunchedEffect(refresh) {
        if (refresh) {
            logDebug("Effect refresh ad")
            viewModel.testMethod()
        }
    }

    LifecycleResumeEffect(viewModel) {

        logDebug("Resume refresh ad")

        viewModel.testMethod()

        onPauseOrDispose {

        }
    }

    when (adState) {
        is AdState.Success -> {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 20.dp)
                    .clickable {
                        logDebug("CLick")
                        viewModel.testMethod()
                    }
            ) {

                (adState as? AdState.Success)?.nativeAd?.headline?.let {
                    Text(
                        text = it,
                        color = colorResource(R.color.gray_700),
                        textAlign = TextAlign.Center,
                        style = FillsaTheme.typography.buttonXSmallNormal
                    )
                }
            }
        }

        else -> {}
    }
}