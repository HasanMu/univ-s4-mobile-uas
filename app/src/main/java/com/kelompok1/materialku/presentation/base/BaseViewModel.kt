package com.kelompok1.materialku.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        launchError(throwable.message ?: "Terjadi kesalahan")
    }

    protected fun launchWithError(block: suspend () -> Unit) {
        viewModelScope.launch(exceptionHandler) {
            block()
        }
    }

    protected fun launchError(message: String) {
        viewModelScope.launch {
            _error.emit(message)
        }
    }
}