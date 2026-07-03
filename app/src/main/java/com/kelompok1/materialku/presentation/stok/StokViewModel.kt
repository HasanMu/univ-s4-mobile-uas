package com.kelompok1.materialku.presentation.stok

import androidx.lifecycle.viewModelScope
import com.kelompok1.materialku.domain.model.JenisStok
import com.kelompok1.materialku.domain.model.Material
import com.kelompok1.materialku.domain.repository.IAuthRepository
import com.kelompok1.materialku.domain.repository.IMaterialRepository
import com.kelompok1.materialku.domain.repository.IStokRepository
import com.kelompok1.materialku.domain.repository.MutasiError
import com.kelompok1.materialku.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class StokViewModel @Inject constructor(
    private val materialRepo: IMaterialRepository,
    private val stokRepo: IStokRepository,
    private val authRepo: IAuthRepository
) : BaseViewModel() {

    private val filter = MutableStateFlow<StokFilter>(StokFilter.Semua)

    private val _state = MutableStateFlow(StokListState())
    val state: StateFlow<StokListState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<StokEvent>(extraBufferCapacity = 4)
    val events: SharedFlow<StokEvent> = _events.asSharedFlow()

    private var currentUserId: Int? = null

    init {
        combine(materialRepo.observeAll(), filter) { list, f ->
            val filtered = when (f) {
                StokFilter.Semua -> list
                StokFilter.Kritis -> list.filter { it.isStokKritis() }
                StokFilter.Aman -> list.filter { !it.isStokKritis() }
            }
            StokListState(items = filtered, filter = f, allMaterials = list)
        }
            .onEach { _state.value = it }
            .launchIn(viewModelScope)

        authRepo.observeSession()
            .onEach { s -> currentUserId = s?.userId }
            .launchIn(viewModelScope)
    }

    fun setFilter(f: StokFilter) {
        filter.value = f
    }

    fun catatMutasi(input: MutasiInput) {
        val userId = currentUserId
        if (userId == null) {
            launchWithError { _events.emit(StokEvent.Error("Session tidak valid, login ulang")) }
            return
        }
        if (input.materialId == null) {
            launchWithError { _events.emit(StokEvent.Error("Pilih material dulu")) }
            return
        }
        val qty = input.qtyText.toIntOrNull()
        if (qty == null || qty <= 0) {
            launchWithError { _events.emit(StokEvent.Error("Qty harus angka lebih dari 0")) }
            return
        }
        launchWithError {
            val result = stokRepo.catatMutasi(
                materialId = input.materialId,
                jenis = input.jenis,
                qty = qty,
                keterangan = input.keterangan.trim(),
                userId = userId
            )
            result.fold(
                onSuccess = { stokBaru -> _events.emit(StokEvent.MutasiSaved(stokBaru)) },
                onFailure = { err ->
                    val msg = when (err) {
                        is MutasiError.InsufficientStock ->
                            "Stok tidak cukup: tersedia ${err.stokSaat}, diminta ${err.requested}"
                        else -> err.message ?: "Gagal mencatat mutasi"
                    }
                    _events.emit(StokEvent.Error(msg))
                }
            )
        }
    }
}

data class StokListState(
    val items: List<Material> = emptyList(),
    val filter: StokFilter = StokFilter.Semua,
    val allMaterials: List<Material> = emptyList()
)

sealed interface StokFilter {
    data object Semua : StokFilter
    data object Kritis : StokFilter
    data object Aman : StokFilter
}

data class MutasiInput(
    val materialId: Int?,
    val jenis: JenisStok,
    val qtyText: String,
    val keterangan: String
)

sealed interface StokEvent {
    data class MutasiSaved(val stokBaru: Int) : StokEvent
    data class Error(val message: String) : StokEvent
}
