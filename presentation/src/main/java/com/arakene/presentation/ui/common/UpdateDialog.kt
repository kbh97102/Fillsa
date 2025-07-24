package com.arakene.presentation.ui.common

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.arakene.presentation.R
import com.arakene.presentation.ui.theme.FillsaTheme
import androidx.core.net.toUri

@Composable
fun UpdateDialog(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(MaterialTheme.shapes.small)
                .background(Color.White)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.icn_warning),
                contentDescription = null,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                "필사 앱 업데이트 안내",
                textAlign = TextAlign.Center,
                style = FillsaTheme.typography.heading4,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Text(
                "최적의 사용 환경을 위해 최신 버전의 앱으로 업데이트 해주세요.",
                textAlign = TextAlign.Center,
                style = FillsaTheme.typography.body2,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            PositiveButton(
                "앱 업데이트",
                onClick = {
                    val playStoreIntent = Intent(Intent.ACTION_VIEW).apply {
                        data = "market://details?id=${context.packageName}".toUri()
                        setPackage(context.packageName)
                    }
                    context.startActivity(playStoreIntent)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    UpdateDialog()
}