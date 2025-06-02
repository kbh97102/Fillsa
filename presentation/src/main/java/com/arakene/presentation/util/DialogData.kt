package com.arakene.presentation.util

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.arakene.presentation.ui.theme.pretendard

@Stable
class DialogData private constructor() {

    var title: String? = null
    var body: String? = null
    var onClick: (() -> Unit)? = null
    var cancelOnClick: (() -> Unit)? = null
    var okText = "확인"
    var cancelText = "취소"
    var titleTextStyle = TextStyle(
        fontWeight = FontWeight.Bold,
        fontFamily = pretendard,
        fontSize = 16.sp,
        lineHeight = 24.sp
    )
    var bodyTextStyle = TextStyle(
        fontWeight = FontWeight.Normal,
        fontFamily = pretendard,
        fontSize = 16.sp,
        lineHeight = 24.sp
    )


    class Builder {
        private val data = DialogData()

        fun title(title: String?) = apply { data.title = title }
        fun body(body: String?) = apply { data.body = body }
        fun onClick(onClick: (() -> Unit)?) = apply { data.onClick = onClick }
        fun cancelOnClick(cancelOnClick: (() -> Unit)?) =
            apply { data.cancelOnClick = cancelOnClick }

        fun okText(okText: String) = apply { data.okText = okText }
        fun cancelText(cancelText: String) = apply { data.cancelText = cancelText }
        fun titleTextStyle(style: TextStyle) = apply { data.titleTextStyle = style }
        fun bodyTextStyle(style: TextStyle) = apply { data.bodyTextStyle = style }

        fun build(): DialogData = data
    }
}

@Stable
class DialogDataHolder {
    var show by mutableStateOf(false)
    var data: DialogData? by mutableStateOf(null)
}
