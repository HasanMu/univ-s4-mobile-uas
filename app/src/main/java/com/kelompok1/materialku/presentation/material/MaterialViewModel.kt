package com.kelompok1.materialku.presentation.material

import androidx.lifecycle.viewModelScope
import com.kelompok1.materialku.domain.model.Kategori
import com.kelompok1.materialku.domain.model.Material
import com.kelompok1.materialku.domain.model.Satuan
import com.kelompok1.materialku.domain.model.Supplier
import com.kelompok1.materialku.domain.repository.IKategoriRepository
import com.kelompok1.materialku.domain.repository.IMaterialRepository
import com.kelompok1.materialku.domain.repository.ISatuanRepository
import com.kelompok1.materialku.domain.repository.ISupplierRepository
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
class MaterialViewModel @Inject constructor(
    private val materialRepo: IMaterialRepository,
    private val kategoriRepo: IKategoriRepository,
    private val satuanRepo: ISatuanRepository,
    private val supplierRepo: ISupplierRepository
) : BaseViewModel() {

    private val filter = MutableStateFlow<MaterialFilter>(MaterialFilter.Semua)

    private val _state = MutableStateFlow(MaterialListState())
    val state: StateFlow<MaterialListState> = _state.asStateFlow()

    private val _formState = MutableStateFlow(MaterialFormState())
    val formState: StateFlow<MaterialFormState> = _formState.asStateFlow()

    private val _events = MutableSharedFlow<MaterialEvent>(extraBufferCapacity = 4)
    val events: SharedFlow<MaterialEvent> = _events.asSharedFlow()

    init {
        // List materials + filter reactive
        combine(materialRepo.observeAll(), kategoriRepo.observeAll(), filter) { mats, kats, f ->
            val katById = kats.associateBy { it.id }
            val filtered = when (f) {
                MaterialFilter.Semua -> mats
                is MaterialFilter.ByKategoriName -> mats.filter { m ->
                    katById[m.kategoriId]?.nama?.equals(f.name, ignoreCase = true) == true
                }
            }
            MaterialListState(
                items = filtered.map { m ->
                    MaterialRow(
                        material = m,
                        kategoriNama = katById[m.kategoriId]?.nama.orEmpty()
                    )
                },
                filter = f,
                kategoris = kats
            )
        }
            .onEach { _state.value = it }
            .launchIn(viewModelScope)
    }

    fun setFilter(f: MaterialFilter) {
        filter.value = f
    }

    fun loadFormData(materialId: Int?) {
        launchWithError {
            val kategoris = kategoriRepo.listAktif()
            val satuans = satuanRepo.listAll()
            val suppliers = supplierRepo.listAktif()
            val existing = materialId?.let { materialRepo.findById(it) }
            _formState.value = MaterialFormState(
                editingId = existing?.id,
                kode = existing?.kode.orEmpty(),
                nama = existing?.nama.orEmpty(),
                hargaText = existing?.hargaJual?.toLong()?.toString().orEmpty(),
                stokAwalText = existing?.stokSaat?.toString().orEmpty(),
                stokMinText = existing?.stokMin?.toString().orEmpty(),
                kategoriId = existing?.kategoriId ?: kategoris.firstOrNull()?.id,
                satuanId = existing?.satuanId ?: satuans.firstOrNull()?.id,
                supplierId = existing?.supplierId,
                kategoris = kategoris,
                satuans = satuans,
                suppliers = suppliers,
                loaded = true
            )
        }
    }

    fun updateForm(update: MaterialFormState.() -> MaterialFormState) {
        _formState.value = _formState.value.update()
    }

    fun save() {
        val f = _formState.value
        val kode = f.kode.trim()
        val nama = f.nama.trim()
        val harga = f.hargaText.toDoubleOrNull() ?: 0.0
        val stokAwal = f.stokAwalText.toIntOrNull() ?: 0
        val stokMin = f.stokMinText.toIntOrNull() ?: 0
        val kategoriId = f.kategoriId
        val satuanId = f.satuanId

        val error = when {
            kode.isEmpty() -> "Kode material wajib diisi"
            nama.isEmpty() -> "Nama material wajib diisi"
            harga <= 0.0 -> "Harga harus lebih dari 0"
            stokAwal < 0 -> "Stok awal tidak boleh negatif"
            stokMin < 0 -> "Stok minimum tidak boleh negatif"
            kategoriId == null -> "Pilih kategori dulu"
            satuanId == null -> "Pilih satuan dulu"
            else -> null
        }
        if (error != null) {
            _formState.value = f.copy(errorMessage = error)
            return
        }

        launchWithError {
            // Cek kode unik (kecuali kalau update dengan id yang sama)
            val existing = materialRepo.findByKode(kode)
            if (existing != null && existing.id != (f.editingId ?: -1)) {
                _formState.value = f.copy(errorMessage = "Kode '$kode' sudah dipakai material lain")
                return@launchWithError
            }

            val material = Material(
                id = f.editingId ?: 0,
                kode = kode,
                nama = nama,
                hargaJual = harga,
                stokSaat = stokAwal,
                stokMin = stokMin,
                kategoriId = kategoriId!!,
                satuanId = satuanId!!,
                supplierId = f.supplierId
            )
            if (f.editingId == null) {
                materialRepo.insert(material)
            } else {
                materialRepo.update(material)
            }
            _events.emit(MaterialEvent.Saved)
        }
    }

    fun delete(id: Int) {
        launchWithError {
            materialRepo.delete(id)
        }
    }
}

data class MaterialRow(
    val material: Material,
    val kategoriNama: String
)

data class MaterialListState(
    val items: List<MaterialRow> = emptyList(),
    val filter: MaterialFilter = MaterialFilter.Semua,
    val kategoris: List<Kategori> = emptyList()
)

sealed interface MaterialFilter {
    data object Semua : MaterialFilter
    data class ByKategoriName(val name: String) : MaterialFilter
}

data class MaterialFormState(
    val editingId: Int? = null,
    val kode: String = "",
    val nama: String = "",
    val hargaText: String = "",
    val stokAwalText: String = "",
    val stokMinText: String = "",
    val kategoriId: Int? = null,
    val satuanId: Int? = null,
    val supplierId: Int? = null,
    val kategoris: List<Kategori> = emptyList(),
    val satuans: List<Satuan> = emptyList(),
    val suppliers: List<Supplier> = emptyList(),
    val errorMessage: String? = null,
    val loaded: Boolean = false
)

sealed interface MaterialEvent {
    data object Saved : MaterialEvent
}
