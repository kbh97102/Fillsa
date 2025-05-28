package com.arakene.presentation.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel: ViewModel() {

    private val _action: MutableSharedFlow<Action> = MutableSharedFlow()
    val action = _action.asSharedFlow()

    private val _effect: Channel<Effect> = Channel()
    val effect = _effect.receiveAsFlow()


    init {
        viewModelScope.launch {
            _action.onEach {
                handleAction(it)
            }.launchIn(this)
        }
    }

    protected abstract fun handleAction(action: Action)

    fun handleContract(contract: Contract) {

        when (contract) {
            is Action -> {
                emitAction(contract)
            }

            is Effect -> {
                emitEffect(contract)
            }

            else -> {}
        }

    }

    protected open fun emitAction(action: Action) {
        viewModelScope.launch {
            _action.emit(action)
        }
    }

    protected open fun emitEffect(effect: Effect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }

}