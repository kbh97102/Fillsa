package com.arakene.presentation.ui.mypage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme

@Composable
fun VersionSection(modifier: Modifier = Modifier) {

    val context = LocalContext.current

    val version = remember {
        context.packageManager.getPackageInfo(context.packageName, 0)
            .versionName.toString()
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 13.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            stringResource(R.string.version),
            style = FillsaTheme.typography.subtitle1,
            color = colorResource(R.color.gray_700)
        )

        Text(version, style = FillsaTheme.typography.body2, color = colorResource(R.color.gray_700))

    }

}

@Preview(showBackground = true)
@Composable
private fun VersionSectionPreview() {
    VersionSection()
}