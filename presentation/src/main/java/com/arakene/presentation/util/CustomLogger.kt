package com.arakene.presentation.util

import android.util.Log

fun logError(msg: String, tag: String = ">>>>") = Log.e(tag, msg)
fun logDebug(msg: String, tag: String = ">>>>") = Log.d(tag, msg)