package com.kelompok1.materialku.presentation.pos

import androidx.lifecycle.viewModelScope
import com.kelompok1.materialku.domain.model.Kategori
import com.kelompok1.materialku.domain.model.Material
import com.kelompok1.materialku.domain.model.StatusTransaksi
import com.kelompok1.materialku.domain.repository.CartLine
import com.kelompok1.materialku.domain.repository.CheckoutError
import com.kelompok1.materialku.domain.repository.CheckoutResult
import com.kelompok1.materialku.domain.repository.IAuthRepository
import com.kelompok1.materialku.domain.repository.IKategoriRepository
import com.kelompok1.materialku.domain.repository.IMaterialRepository
import com.kelompok1.materialku.domain.repository.IPosRepository
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
class PosViewModel @Inject constructor(
    private val materialRepo: IMaterialRepository,
    private val kategoriRepo: IKategoriRepository,
    private val posRepo: IPosRepository,
    private val authRepo: IAuthRepository
) : BaseViewModel() {

    private val filter = MutableStateFlow<PosFilter>(PosFilter.Semua)
    private val search = MutableStateFlow("")
    private val cart = MutableStateFlow<Map<Int, Int>>(emptyMap())
    private val draftList = MutableStateFlow<List<com.kelompok1.materialku.domain.model.Transaksi>>(emptyList())

    private val _state = MutableStateFlow(PosState())
    val state: StateFlow<PosState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<PosEvent>(extraBufferCapacity = 4)
    val events: SharedFlow<PosEvent> = _events.asSharedFlow()

    private var currentUserId: Int? = null

    init {
        // combine max 5 args di Flow API — bungkus filter+search+cart+drafts
        // jadi satu tuple biar tetep muat.
        val tail = combine(filter, search, cart, draftList) { f, q, c, d ->
            PosCombineTail(f, q, c, d)
        }
        combine(materialRepo.observeAll(), kategoriRepo.observeAll(), tail) { mats, kats, t ->
            val katById = kats.associateBy { it.id }
            val filteredByKat = when (val f = t.filter) {
                PosFilter.Semua -> mats
                is PosFilter.ByKategoriName -> mats.filter { m ->
                    katById[m.kategoriId]?.nama.equals(f.name, ignoreCase = true)
                }
            }
            val q = t.search
            val filtered = if (q.isBlank()) filteredByKat else filteredByKat.filter {
                it.nama.contains(q, ignoreCase = true) || it.kode.contains(q, ignoreCase = true)
            }
            val c = t.cart
            val rows = filtered.map { m ->
                PosMaterialRow(
                    material = m,
                    kategoriNama = katById[m.kategoriId]?.nama.orEmpty(),
                    qtyInCart = c[m.id] ?: 0
                )
            }
            val cartLines = mats.mapNotNull { m ->
                val qty = c[m.id] ?: 0
                if (qty <= 0) null
                else CartItem(material = m, qty = qty, subtotal = qty * m.hargaJual)
            }
            PosState(
                items = rows,
                kategoris = kats,
                filter = t.filter,
                search = q,
                cart = cartLines,
                cartCount = cartLines.sumOf { it.qty },
                total = cartLines.sumOf { it.subtotal },
                draftCount = t.drafts.size
            )
        }
            .onEach { _state.value = it }
            .launchIn(viewModelScope)

        authRepo.observeSession()
            .onEach { s -> currentUserId = s?.userId }
            .launchIn(viewModelScope)

        // Observe daftar draft supaya badge & bottom sheet always fresh.
        // Update `draftCount` di state supaya UI re-render otomatis waktu
        // list draft berubah (misal setelah save DRAFT atau openDraft).
        posRepo.observeByStatus(com.kelompok1.materialku.domain.model.StatusTransaksi.DRAFT)
            .onEach { list -> draftList.value = list }
            .launchIn(viewModelScope)
    }

    fun drafts(): List<com.kelompok1.materialku.domain.model.Transaksi> = draftList.value

    fun openDraft(transaksiId: Int) {
        launchWithError {
            val items = posRepo.loadDraftAndDelete(transaksiId)
            val newCart = items.associate { it.materialId to it.qty }
            cart.value = newCart
            _events.emit(PosEvent.DraftLoaded(items.size))
        }
    }

    fun setFilter(f: PosFilter) {
        filter.value = f
    }

    fun setSearch(q: String) {
        search.value = q
    }

    fun addToCart(materialId: Int) {
        cart.value = cart.value.toMutableMap().apply {
            put(materialId, (get(materialId) ?: 0) + 1)
        }
    }

    fun removeFromCart(materialId: Int) {
        cart.value = cart.value.toMutableMap().apply {
            val curr = get(materialId) ?: 0
            if (curr <= 1) remove(materialId) else put(materialId, curr - 1)
        }
    }

    fun clearCartItem(materialId: Int) {
        cart.value = cart.value.toMutableMap().apply { remove(materialId) }
    }

    fun clearCart() {
        cart.value = emptyMap()
    }

    fun checkout(status: StatusTransaksi) {
        val userId = currentUserId
        if (userId == null) {
            launchWithError { _events.emit(PosEvent.Error("Session tidak valid, login ulang")) }
            return
        }
        val lines = state.value.cart.map { CartLine(it.material.id, it.qty, it.material.hargaJual) }
        if (lines.isEmpty()) {
            launchWithError { _events.emit(PosEvent.Error("Keranjang kosong")) }
            return
        }
        launchWithError {
            val result = posRepo.checkout(lines, status, userId)
            result.fold(
                onSuccess = { r ->
                    clearCart()
                    _events.emit(PosEvent.CheckoutSuccess(r, status))
                },
                onFailure = { err ->
                    val msg = when (err) {
                        is CheckoutError.InsufficientStock ->
                            "Stok ${err.nama} kurang: tersedia ${err.stokSaat}, diminta ${err.requested}"
                        else -> err.message ?: "Gagal checkout"
                    }
                    _events.emit(PosEvent.Error(msg))
                }
            )
        }
    }
}

data class PosState(
    val items: List<PosMaterialRow> = emptyList(),
    val kategoris: List<Kategori> = emptyList(),
    val filter: PosFilter = PosFilter.Semua,
    val search: String = "",
    val cart: List<CartItem> = emptyList(),
    val cartCount: Int = 0,
    val total: Double = 0.0,
    val draftCount: Int = 0
)

private data class PosCombineTail(
    val filter: PosFilter,
    val search: String,
    val cart: Map<Int, Int>,
    val drafts: List<com.kelompok1.materialku.domain.model.Transaksi>
)

data class PosMaterialRow(
    val material: Material,
    val kategoriNama: String,
    val qtyInCart: Int
)

data class CartItem(
    val material: Material,
    val qty: Int,
    val subtotal: Double
)

sealed interface PosFilter {
    data object Semua : PosFilter
    data class ByKategoriName(val name: String) : PosFilter
}

sealed interface PosEvent {
    data class CheckoutSuccess(val result: CheckoutResult, val status: StatusTransaksi) : PosEvent
    data class DraftLoaded(val itemCount: Int) : PosEvent
    data class Error(val message: String) : PosEvent
}
