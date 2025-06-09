package com.arakene.presentation.util

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arakene.domain.util.ApiResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

abstract class BaseViewModel : ViewModel() {

    // TODO: 이게 최선인가?
    var lastContract: Contract? = null

    private val _action: MutableSharedFlow<Action> = MutableSharedFlow()
    val action = _action.asSharedFlow()

    private val _effect: Channel<Effect> = Channel()
    val effect = _effect.receiveAsFlow()

    private val _error: MutableSharedFlow<String> = MutableSharedFlow()
    val error = _error.asSharedFlow()

    val isProcessing = mutableStateOf(false)

    init {
        viewModelScope.launch {
            _action.onEach {
                handleAction(it)
            }.launchIn(this)
        }
    }

    protected abstract fun handleAction(action: Action)

    fun handleContract(contract: Contract) {
        lastContract = contract
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

    protected suspend fun <T> getResponse(response: ApiResult<T>): T? {
        isProcessing.value = true
        return when (response) {
            is ApiResult.Success -> {
                isProcessing.value = false
                response.data
            }

            is ApiResult.Fail -> {
                isProcessing.value = false

                when (response.error?.errorCode) {
                    403 -> {
                        _error.emit("403")
                    }
                }

                null
            }

            is ApiResult.Error -> {
                isProcessing.value = false
                when (response.error) {
                    is HttpException -> {
                        _error.emit("404")
                    }
                }
                null
            }
        }

    }

}