package com.arakene.data.util

import android.util.Log

object CustomLogger {
    fun logError(msg: String, tag: String = ">>>>") = Log.e(tag, msg)
    fun logDebug(msg: String, tag: String = ">>>>") = Log.d(tag, msg)
}

