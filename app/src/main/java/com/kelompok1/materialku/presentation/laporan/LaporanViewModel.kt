package com.kelompok1.materialku.presentation.laporan

import androidx.lifecycle.viewModelScope
import com.kelompok1.materialku.domain.repository.IReportRepository
import com.kelompok1.materialku.domain.repository.LaporanData
import com.kelompok1.materialku.domain.repository.LaporanPeriode
import com.kelompok1.materialku.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LaporanViewModel @Inject constructor(
    private val reportRepo: IReportRepository
) : BaseViewModel() {

    private val _state = MutableStateFlow(LaporanState())
    val state: StateFlow<LaporanState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<LaporanEvent>(extraBufferCapacity = 2)
    val events: SharedFlow<LaporanEvent> = _events.asSharedFlow()

    init {
        load(LaporanPeriode.HariIni)
    }

    fun load(periode: LaporanPeriode) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, periode = periode)
            runCatching { reportRepo.buildLaporan(periode) }.fold(
                onSuccess = { data ->
                    _state.value = LaporanState(loading = false, periode = periode, data = data)
                },
                onFailure = { err ->
                    _state.value = _state.value.copy(loading = false)
                    _events.emit(LaporanEvent.Error(err.message ?: "Gagal load laporan"))
                }
            )
        }
    }

    fun requestExport() {
        val data = _state.value.data ?: return
        viewModelScope.launch { _events.emit(LaporanEvent.ExportRequested(data)) }
    }
}

data class LaporanState(
    val loading: Boolean = false,
    val periode: LaporanPeriode = LaporanPeriode.HariIni,
    val data: LaporanData? = null
)

sealed interface LaporanEvent {
    data class ExportRequested(val data: LaporanData) : LaporanEvent
    data class Error(val message: String) : LaporanEvent
}
