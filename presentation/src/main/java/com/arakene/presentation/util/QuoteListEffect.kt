package com.arakene.presentation.util

interface QuoteListEffect: Effect {

    data class Refresh(val likeYn: Boolean): QuoteListEffect

}