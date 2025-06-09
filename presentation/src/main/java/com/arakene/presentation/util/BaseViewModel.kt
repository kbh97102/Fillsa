package com.arakene.presentation.util

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arakene.domain.util.ApiResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }

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
        setLoading(true)
        viewModelScope.launch {
            delay(250)
            // TODO: 검증 필요
            if (isProcessing.value) {
                setLoading(true)
            }
        }
        return when (response) {
            is ApiResult.Success -> {
                isProcessing.value = false
                setLoading(false)
                response.data
            }

            is ApiResult.Fail -> {
                isProcessing.value = false
                setLoading(false)
                null
            }

            is ApiResult.Error -> {
                isProcessing.value = false
                setLoading(false)
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