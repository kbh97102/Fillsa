package com.arakene.domain.repository

import com.arakene.domain.model.AdState
import kotlinx.coroutines.flow.StateFlow

interface AdRepository {

    fun getAdStateFlow(): StateFlow<AdState>

}