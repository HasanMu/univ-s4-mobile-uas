package com.kelompok1.materialku.presentation.satuan

import androidx.lifecycle.viewModelScope
import com.kelompok1.materialku.domain.model.Satuan
import com.kelompok1.materialku.domain.repository.ISatuanRepository
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
class SatuanViewModel @Inject constructor(
    private val satuanRepo: ISatuanRepository
) : BaseViewModel() {

    private val _state = MutableStateFlow(SatuanListState())
    val state: StateFlow<SatuanListState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<SatuanEvent>(extraBufferCapacity = 4)
    val events: SharedFlow<SatuanEvent> = _events.asSharedFlow()

    init {
        satuanRepo.observeAll()
            .onEach { list -> _state.value = _state.value.copy(items = list) }
            .launchIn(viewModelScope)
    }

    fun save(input: SatuanFormInput) {
        val nama = input.nama.trim()
        val simbol = input.simbol.trim()

        val fieldError: Pair<SatuanField, String>? = when {
            nama.isEmpty() -> SatuanField.NAMA to "Nama satuan wajib diisi"
            simbol.isEmpty() -> SatuanField.SIMBOL to "Simbol wajib diisi"
            simbol.length > 10 -> SatuanField.SIMBOL to "Simbol maksimal 10 karakter"
            else -> null
        }
        if (fieldError != null) {
            launchWithError {
                _events.emit(SatuanEvent.ValidationError(fieldError.first, fieldError.second))
            }
            return
        }

        launchWithError {
            val existingList = _state.value.items
            val duplicate = existingList.any {
                it.nama.equals(nama, ignoreCase = true) && it.id != (input.editingId ?: -1)
            }
            if (duplicate) {
                _events.emit(SatuanEvent.ValidationError(SatuanField.NAMA, "Satuan '$nama' sudah dipakai"))
                return@launchWithError
            }

            if (input.editingId == null) {
                satuanRepo.insert(Satuan(nama = nama, simbol = simbol))
            } else {
                val existing = satuanRepo.findById(input.editingId) ?: return@launchWithError
                satuanRepo.update(existing.copy(nama = nama, simbol = simbol))
            }
            _events.emit(SatuanEvent.Saved)
        }
    }

    fun delete(id: Int) {
        launchWithError {
            satuanRepo.delete(id)
            _events.emit(SatuanEvent.Deleted)
        }
    }
}

data class SatuanListState(
    val items: List<Satuan> = emptyList()
)

data class SatuanFormInput(
    val editingId: Int? = null,
    val nama: String,
    val simbol: String
)

enum class SatuanField { NAMA, SIMBOL }

sealed interface SatuanEvent {
    data object Saved : SatuanEvent
    data object Deleted : SatuanEvent
    data class ValidationError(val field: SatuanField, val message: String) : SatuanEvent
}
