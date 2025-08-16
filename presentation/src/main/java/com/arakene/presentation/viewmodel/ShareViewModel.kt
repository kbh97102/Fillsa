package com.arakene.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.arakene.domain.usecase.home.GetShareDescriptionVisibleUseCase
import com.arakene.domain.usecase.home.SetShareDescriptionVisibleUseCase
import com.arakene.presentation.util.Action
import com.arakene.presentation.util.BaseViewModel
import com.arakene.presentation.util.action.ShareAction
import com.arakene.presentation.util.state.ShareState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShareViewModel @Inject constructor(
    private val setShareDescriptionVisibleUseCase: SetShareDescriptionVisibleUseCase,
    private val getShareDescriptionVisibleUseCase: GetShareDescriptionVisibleUseCase
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(ShareState(descriptionShouldVisible = true))
    val uiState: StateFlow<ShareState> = _uiState.asStateFlow()

    init {
        checkShouldShowDescription()
    }

    private fun checkShouldShowDescription() {
        viewModelScope.launch {
            _uiState.value =
                _uiState.value.copy(descriptionShouldVisible = getShareDescriptionVisibleUseCase())
        }
    }


    override fun handleAction(action: Action) {
        when (action) {
            is ShareAction.ClickDescription -> {
                viewModelScope.launch {
                    setShareDescriptionVisibleUseCase(false)
                    _uiState.value = _uiState.value.copy(descriptionShouldVisible = false)
                }
            }
        }
    }
}