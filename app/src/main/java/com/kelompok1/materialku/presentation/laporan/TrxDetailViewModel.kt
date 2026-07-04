package com.kelompok1.materialku.presentation.laporan

import androidx.lifecycle.viewModelScope
import com.kelompok1.materialku.domain.repository.IReportRepository
import com.kelompok1.materialku.domain.repository.TrxDetail
import com.kelompok1.materialku.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrxDetailViewModel @Inject constructor(
    private val reportRepo: IReportRepository
) : BaseViewModel() {

    private val _state = MutableStateFlow(TrxDetailState())
    val state: StateFlow<TrxDetailState> = _state.asStateFlow()

    fun load(transaksiId: Int) {
        if (_state.value.detail?.transaksi?.id == transaksiId) return
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            runCatching { reportRepo.getTrxDetail(transaksiId) }.fold(
                onSuccess = { detail ->
                    _state.value = TrxDetailState(loading = false, detail = detail)
                },
                onFailure = { err ->
                    _state.value = TrxDetailState(
                        loading = false,
                        error = err.message ?: "Gagal memuat detail"
                    )
                }
            )
        }
    }
}

data class TrxDetailState(
    val loading: Boolean = false,
    val detail: TrxDetail? = null,
    val error: String? = null
)
