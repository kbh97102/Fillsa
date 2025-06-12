package com.arakene.presentation

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun TextIndexFindTest() {
        val text= "로그인 시, 이용약관 및 개인정보 처리방침에 동의하는 것으로 간주됩니다."

        val target = "이용약관"

        println("targetIndex ${text.indexOf(target)}")
    }
}