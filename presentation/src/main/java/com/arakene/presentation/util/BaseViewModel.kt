package com.arakene.presentation.util

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arakene.domain.responses.ErrorResponse
import com.arakene.domain.util.ApiResult
import com.arakene.domain.util.CommonError
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.UnknownHostException

abstract class BaseViewModel : ViewModel() {

    // TODO: 이게 최선인가?
    var lastContract: Contract? = null

    private val _action: MutableSharedFlow<Action> = MutableSharedFlow()
    val action = _action.asSharedFlow()

    private val _effect: Channel<Effect> = Channel()
    val effect = _effect.receiveAsFlow()

    private val _error: MutableSharedFlow<CommonError> = MutableSharedFlow()
    val error = _error.asSharedFlow()

    val isProcessing = mutableStateOf(false)

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }

    init {
        viewModelScope.launch {
            _action.throttleFirst(250).collectLatest {
                when (it) {
                    is CommonAction.PopBackStack -> {
                        emitEffect(CommonEffect.PopBackStack)
                    }

                    else -> handleAction(it)
                }
            }
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
                if (contract is CommonEffect.EmitError) {
                    viewModelScope.launch {
                        _error.emit(contract.commonError)
                    }
                }
                else {
                    emitEffect(contract)
                }
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

    protected suspend fun <T> getResponse(response: ApiResult<T>, useLoading: Boolean = true): T? {
        isProcessing.value = true
        setLoading(true)
        viewModelScope.launch {
            delay(250)
            // TODO: 검증 필요
            if (isProcessing.value) {
                if (useLoading) {
                    setLoading(true)
                }
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
                _error.emit(response.error)
                null
            }
        }

    }

}