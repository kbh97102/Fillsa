package com.arakene.presentation.util

import android.content.Context
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.arakene.presentation.R

@Stable
class DialogData private constructor() {

    var title: String = ""
    var body: String = ""
    var onClick: (() -> Unit)? = null
    var cancelOnClick: (() -> Unit)? = null
    var okText = "확인"
    var cancelText = "취소"
    var titleTextSize: TextUnit = 16.sp
    var bodyTextSize: TextUnit = 16.sp
    var reversed = false
    var drawableId = -1


    class Builder {
        private val data = DialogData()

        fun title(title: String) = apply { data.title = title }
        fun body(body: String) = apply { data.body = body }
        fun onClick(onClick: (() -> Unit)?) = apply { data.onClick = onClick }
        fun cancelOnClick(cancelOnClick: (() -> Unit)?) =
            apply { data.cancelOnClick = cancelOnClick }

        fun okText(okText: String) = apply { data.okText = okText }
        fun cancelText(cancelText: String) = apply { data.cancelText = cancelText }
        fun titleTextSize(style: TextUnit) = apply { data.titleTextSize = style }
        fun bodyTextSize(style: TextUnit) = apply { data.bodyTextSize = style }
        fun reversed(reversed: Boolean) = apply { data.reversed = reversed }
        fun drawableId(drawableId: Int) = apply { data.drawableId = drawableId }

        fun build(): DialogData = data

        fun buildNetworkError(
            context: Context,
            okOnClick: () -> Unit
        ): DialogData {

            data.apply {
                title = context.getString(R.string.network_error)
                drawableId = R.drawable.icn_network_error
                cancelText = context.getString(R.string.finish)
                okText = context.getString(R.string.retry)
                onClick = okOnClick
            }

            return data
        }
    }
}

@Stable
class DialogDataHolder {
    var show by mutableStateOf(false)
    var data: DialogData? by mutableStateOf(null)
}
