package com.arakene.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.arakene.domain.model.AdState
import com.arakene.domain.usecase.GetAdStateUseCase
import com.arakene.presentation.util.Action
import com.arakene.presentation.util.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdViewModel @Inject constructor(
    private val getAdStateUseCase: GetAdStateUseCase,
) : BaseViewModel() {


    private val _adState = MutableStateFlow<AdState>(AdState.Loading)
    val adState: StateFlow<AdState> = _adState

    fun testMethod() {
        viewModelScope.launch {
            _adState.value = AdState.Loading
            try {
                val ad = getAdStateUseCase(useCache = false)
                if (ad != null) {
                    _adState.value = AdState.Success(ad)
                } else {
                    _adState.value = AdState.Error
                }
            } catch (e: Exception) {
                _adState.value = AdState.Error
            }
        }
    }

    override fun handleAction(action: Action) {
        TODO("Not yet implemented")
    }
}