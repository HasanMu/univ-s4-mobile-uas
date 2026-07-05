package com.kelompok1.materialku.presentation.supplier

import androidx.lifecycle.viewModelScope
import com.kelompok1.materialku.domain.model.Supplier
import com.kelompok1.materialku.domain.repository.ISupplierRepository
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
class SupplierViewModel @Inject constructor(
    private val supplierRepo: ISupplierRepository
) : BaseViewModel() {

    private val _state = MutableStateFlow(SupplierListState())
    val state: StateFlow<SupplierListState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<SupplierEvent>(extraBufferCapacity = 4)
    val events: SharedFlow<SupplierEvent> = _events.asSharedFlow()

    init {
        supplierRepo.observeAll()
            .onEach { list ->
                _state.value = _state.value.copy(items = list)
            }
            .launchIn(viewModelScope)
    }

    fun save(input: SupplierFormInput) {
        val nama = input.nama.trim()
        val kontak = input.kontak.trim()
        val alamat = input.alamat.trim()

        if (nama.isEmpty()) {
            launchWithError {
                _events.emit(SupplierEvent.ValidationError(SupplierField.NAMA, "Nama supplier wajib diisi"))
            }
            return
        }

        launchWithError {
            val existingList = _state.value.items
            val duplicate = existingList.any {
                it.nama.equals(nama, ignoreCase = true) && it.id != (input.editingId ?: -1)
            }
            if (duplicate) {
                _events.emit(SupplierEvent.ValidationError(SupplierField.NAMA, "Nama '$nama' sudah dipakai"))
                return@launchWithError
            }

            if (input.editingId == null) {
                supplierRepo.insert(
                    Supplier(
                        nama = nama,
                        kontak = kontak,
                        alamat = alamat,
                        aktif = input.aktif
                    )
                )
            } else {
                val existing = supplierRepo.findById(input.editingId) ?: return@launchWithError
                supplierRepo.update(
                    existing.copy(
                        nama = nama,
                        kontak = kontak,
                        alamat = alamat,
                        aktif = input.aktif
                    )
                )
            }
            _events.emit(SupplierEvent.Saved)
        }
    }

    fun delete(id: Int) {
        launchWithError {
            supplierRepo.delete(id)
            _events.emit(SupplierEvent.Deleted)
        }
    }
}

data class SupplierListState(
    val items: List<Supplier> = emptyList()
)

data class SupplierFormInput(
    val editingId: Int? = null,
    val nama: String,
    val kontak: String,
    val alamat: String,
    val aktif: Boolean
)

enum class SupplierField { NAMA }

sealed interface SupplierEvent {
    data object Saved : SupplierEvent
    data object Deleted : SupplierEvent
    data class ValidationError(val field: SupplierField, val message: String) : SupplierEvent
}
