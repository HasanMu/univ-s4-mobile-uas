package com.kelompok1.materialku.presentation.kategori

import androidx.lifecycle.viewModelScope
import com.kelompok1.materialku.domain.model.Kategori
import com.kelompok1.materialku.domain.repository.IKategoriRepository
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
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class KategoriViewModel @Inject constructor(
    private val kategoriRepo: IKategoriRepository
) : BaseViewModel() {

    private val _state = MutableStateFlow(KategoriListState())
    val state: StateFlow<KategoriListState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<KategoriEvent>(extraBufferCapacity = 4)
    val events: SharedFlow<KategoriEvent> = _events.asSharedFlow()

    init {
        kategoriRepo.observeAll()
            .onEach { list ->
                _state.value = _state.value.copy(items = list)
            }
            .launchIn(viewModelScope)
    }

    fun save(input: KategoriFormInput) {
        val nama = input.nama.trim()
        val deskripsi = input.deskripsi.trim()

        if (nama.isEmpty()) {
            launchWithError {
                _events.emit(KategoriEvent.ValidationError("Nama kategori wajib diisi"))
            }
            return
        }

        launchWithError {
            val existingList = _state.value.items
            val duplicate = existingList.any {
                it.nama.equals(nama, ignoreCase = true) && it.id != (input.editingId ?: -1)
            }
            if (duplicate) {
                _events.emit(KategoriEvent.ValidationError("Nama kategori '$nama' sudah dipakai"))
                return@launchWithError
            }

            if (input.editingId == null) {
                kategoriRepo.insert(
                    Kategori(
                        nama = nama,
                        deskripsi = deskripsi,
                        createdAt = LocalDate.now(),
                        aktif = input.aktif
                    )
                )
            } else {
                val existing = kategoriRepo.findById(input.editingId) ?: return@launchWithError
                kategoriRepo.update(
                    existing.copy(
                        nama = nama,
                        deskripsi = deskripsi,
                        aktif = input.aktif
                    )
                )
            }
            _events.emit(KategoriEvent.Saved)
        }
    }

    fun delete(id: Int) {
        launchWithError {
            kategoriRepo.delete(id)
            _events.emit(KategoriEvent.Deleted)
        }
    }
}

data class KategoriListState(
    val items: List<Kategori> = emptyList()
)

data class KategoriFormInput(
    val editingId: Int? = null,
    val nama: String,
    val deskripsi: String,
    val aktif: Boolean
)

sealed interface KategoriEvent {
    data object Saved : KategoriEvent
    data object Deleted : KategoriEvent
    data class ValidationError(val message: String) : KategoriEvent
}
