package com.kelompok1.materialku.presentation.dashboard

import androidx.lifecycle.viewModelScope
import com.kelompok1.materialku.domain.model.RoleEnum
import com.kelompok1.materialku.domain.repository.IAuthRepository
import com.kelompok1.materialku.domain.repository.IMaterialRepository
import com.kelompok1.materialku.presentation.base.BaseViewModel
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
class DashboardViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
    private val materialRepository: IMaterialRepository
) : BaseViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<DashboardEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<DashboardEvent> = _events.asSharedFlow()

    init {
        authRepository.observeSession()
            .onEach { snapshot ->
                if (snapshot == null) {
                    _events.tryEmit(DashboardEvent.SessionExpired)
                } else {
                    _state.value = _state.value.copy(
                        userName = snapshot.username,
                        role = snapshot.role
                    )
                }
            }
            .launchIn(viewModelScope)

        // Stat material + kritis diturunkan langsung dari observeAll()
        // supaya angka di dashboard ikut berubah realtime waktu user CRUD
        // material — tanpa perlu manual refresh.
        materialRepository.observeAll()
            .onEach { materials ->
                _state.value = _state.value.copy(
                    statMaterial = materials.size,
                    statKritis = materials.count { it.isStokKritis() }
                )
            }
            .launchIn(viewModelScope)

        // TODO(pria): statTransaksi tunggu TransaksiDao masuk.
    }

    fun logout() {
        launchWithError {
            authRepository.clearSession()
            _events.emit(DashboardEvent.LoggedOut)
        }
    }
}

data class DashboardState(
    val userName: String = "",
    val role: RoleEnum? = null,
    val statMaterial: Int = 0,
    val statTransaksi: Int = 0,
    val statKritis: Int = 0
)

sealed interface DashboardEvent {
    data object LoggedOut : DashboardEvent
    data object SessionExpired : DashboardEvent
}
