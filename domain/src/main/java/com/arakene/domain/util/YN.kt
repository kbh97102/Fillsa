package com.arakene.domain.util

enum class YN(val type: String) {

    Y("Y"),
    N("N");

    companion object {
        fun getYN(data: String) = when (data) {
            Y.type -> Y
            else -> N
        }
    }
}