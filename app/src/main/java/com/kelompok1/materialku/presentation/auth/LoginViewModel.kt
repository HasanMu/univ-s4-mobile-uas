package com.kelompok1.materialku.presentation.auth

import com.kelompok1.materialku.domain.model.RoleEnum
import com.kelompok1.materialku.domain.repository.IAuthRepository
import com.kelompok1.materialku.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: IAuthRepository
) : BaseViewModel() {

    private val _state = MutableStateFlow<LoginState>(LoginState.Idle)
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun login(username: String, password: String) {
        val u = username.trim()
        if (u.isEmpty() || password.isEmpty()) {
            _state.value = LoginState.Error("Username dan password wajib diisi")
            return
        }
        _state.value = LoginState.Loading
        launchWithError {
            val user = authRepository.login(u, password)
            if (user == null) {
                _state.value = LoginState.Error("Username atau password salah")
            } else {
                authRepository.saveSession(user)
                _state.value = LoginState.Success(user.role)
            }
        }
    }

    fun resetError() {
        if (_state.value is LoginState.Error) _state.value = LoginState.Idle
    }
}

sealed interface LoginState {
    data object Idle : LoginState
    data object Loading : LoginState
    data class Error(val message: String) : LoginState
    data class Success(val role: RoleEnum) : LoginState
}
