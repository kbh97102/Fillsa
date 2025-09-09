package com.arakene.presentation.util

import android.content.Context
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arakene.presentation.R

@Stable
class DialogData private constructor() {

    var title: String = ""
    var body: String = ""
    var onClick: (() -> Unit)? = null
    var cancelOnClick: (() -> Unit)? = null
    var okText = "확인"
    var cancelText = "취소"
    var titleTextStyle: TypographyEnum = TypographyEnum.Heading4
    var bodyTextStyle: TypographyEnum = TypographyEnum.Body2
    var reversed = false
    var drawableId = -1
    var singleButton = false



    class Builder {
        private val data = DialogData()

        fun title(title: String) = apply { data.title = title }
        fun body(body: String) = apply { data.body = body }
        fun onClick(onClick: (() -> Unit)?) = apply { data.onClick = onClick }
        fun cancelOnClick(cancelOnClick: (() -> Unit)?) =
            apply { data.cancelOnClick = cancelOnClick }

        fun okText(okText: String) = apply { data.okText = okText }
        fun cancelText(cancelText: String) = apply { data.cancelText = cancelText }
        fun titleTextStyle(type: TypographyEnum) = apply { data.titleTextStyle = type }
        fun bodyTextStyle(type: TypographyEnum) = apply { data.bodyTextStyle = type }
        fun reversed(reversed: Boolean) = apply { data.reversed = reversed }
        fun drawableId(drawableId: Int) = apply { data.drawableId = drawableId }
        fun singleButton(isSingleButton: Boolean) = apply { data.singleButton = isSingleButton }

        fun build(): DialogData = data

        fun buildNetworkError(
            context: Context,
            okOnClick: () -> Unit,
            cancelOnClick: () -> Unit,
        ): DialogData {

            data.apply {
                title = context.getString(R.string.network_error)
                drawableId = R.drawable.icn_network_error
                cancelText = context.getString(R.string.finish)
                okText = context.getString(R.string.retry)
                onClick = okOnClick
                this.cancelOnClick = cancelOnClick
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
