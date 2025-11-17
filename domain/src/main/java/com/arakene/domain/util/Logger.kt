package com.arakene.domain.util

import android.util.Log

object Logger {

    fun logE(msg: String, tag: String = ">>>>") = Log.e(tag, msg)
}