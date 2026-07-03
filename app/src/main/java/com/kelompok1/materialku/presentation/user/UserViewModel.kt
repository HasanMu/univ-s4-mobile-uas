package com.kelompok1.materialku.presentation.user

import androidx.lifecycle.viewModelScope
import com.kelompok1.materialku.domain.model.RoleEnum
import com.kelompok1.materialku.domain.model.User
import com.kelompok1.materialku.domain.repository.IAuthRepository
import com.kelompok1.materialku.domain.repository.IUserRepository
import com.kelompok1.materialku.presentation.base.BaseViewModel
import com.kelompok1.materialku.util.PasswordHasher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepo: IUserRepository,
    private val authRepo: IAuthRepository,
    private val hasher: PasswordHasher
) : BaseViewModel() {

    private val _state = MutableStateFlow(UserListState())
    val state: StateFlow<UserListState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<UserEvent>(extraBufferCapacity = 4)
    val events: SharedFlow<UserEvent> = _events.asSharedFlow()

    init {
        userRepo.observeAll()
            .onEach { list -> _state.value = _state.value.copy(items = list) }
            .launchIn(viewModelScope)

        // Simpen id user yang lagi login — buat guard delete-diri-sendiri
        // supaya admin gak sengaja lock-out.
        authRepo.observeSession()
            .onEach { s -> _state.value = _state.value.copy(currentUserId = s?.userId) }
            .launchIn(viewModelScope)
    }

    fun save(input: UserFormInput) {
        val username = input.username.trim()
        val password = input.password
        val isCreate = input.editingId == null

        val fieldError: Pair<UserField, String>? = when {
            username.isEmpty() -> UserField.USERNAME to "Username wajib diisi"
            username.length < 3 -> UserField.USERNAME to "Username minimal 3 karakter"
            isCreate && password.isEmpty() -> UserField.PASSWORD to "Password wajib diisi"
            isCreate && password.length < 4 -> UserField.PASSWORD to "Password minimal 4 karakter"
            !isCreate && password.isNotEmpty() && password.length < 4 ->
                UserField.PASSWORD to "Password minimal 4 karakter"
            else -> null
        }
        if (fieldError != null) {
            launchWithError {
                _events.emit(UserEvent.ValidationError(fieldError.first, fieldError.second))
            }
            return
        }

        launchWithError {
            val existing = userRepo.findByUsername(username)
            if (existing != null && existing.id != (input.editingId ?: -1)) {
                _events.emit(UserEvent.ValidationError(UserField.USERNAME, "Username '$username' sudah dipakai"))
                return@launchWithError
            }

            if (isCreate) {
                userRepo.insert(
                    User(
                        username = username,
                        passwordHash = hasher.hash(password),
                        role = input.role,
                        aktif = input.aktif
                    )
                )
            } else {
                val current = userRepo.findById(input.editingId!!) ?: return@launchWithError
                val nextHash = if (password.isNotEmpty()) hasher.hash(password) else current.passwordHash
                userRepo.update(
                    current.copy(
                        username = username,
                        passwordHash = nextHash,
                        role = input.role,
                        aktif = input.aktif
                    )
                )
            }
            _events.emit(UserEvent.Saved)
        }
    }

    fun delete(id: Int) {
        launchWithError {
            if (id == _state.value.currentUserId) {
                _events.emit(UserEvent.DeleteBlocked("Tidak bisa menghapus akun yang sedang login"))
                return@launchWithError
            }
            userRepo.delete(id)
            _events.emit(UserEvent.Deleted)
        }
    }
}

enum class UserField { USERNAME, PASSWORD }

data class UserListState(
    val items: List<User> = emptyList(),
    val currentUserId: Int? = null
)

data class UserFormInput(
    val editingId: Int? = null,
    val username: String,
    val password: String,
    val role: RoleEnum,
    val aktif: Boolean
)

sealed interface UserEvent {
    data object Saved : UserEvent
    data object Deleted : UserEvent
    data class ValidationError(val field: UserField, val message: String) : UserEvent
    data class DeleteBlocked(val message: String) : UserEvent
}
