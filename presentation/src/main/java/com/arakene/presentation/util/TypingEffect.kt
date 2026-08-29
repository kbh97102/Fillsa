package com.arakene.presentation.util

interface TypingEffect : Effect {

    data class Refresh(val seq: Int) : TypingEffect

}