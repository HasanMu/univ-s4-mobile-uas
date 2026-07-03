package com.kelompok1.materialku.domain.repository

import com.kelompok1.materialku.domain.model.StatusTransaksi
import com.kelompok1.materialku.domain.model.Transaksi
import kotlinx.coroutines.flow.Flow

interface IPosRepository {
    fun observeAll(): Flow<List<Transaksi>>
    fun observeByStatus(status: StatusTransaksi): Flow<List<Transaksi>>
    suspend fun findById(id: Int): Transaksi?

    /**
     * Checkout: insert Transaksi + item-item + decrement stok + StokLog KELUAR,
     * semua dalam satu database transaction. Kalau ada material yang stoknya
     * kurang, seluruh transaksi di-rollback.
     *
     * @return Result.success dengan no faktur baru, atau Result.failure
     *         dengan CheckoutError.
     */
    suspend fun checkout(
        items: List<CartLine>,
        status: StatusTransaksi,
        userId: Int
    ): Result<CheckoutResult>
}

data class CartLine(
    val materialId: Int,
    val qty: Int,
    val hargaSatuan: Double
)

data class CheckoutResult(
    val transaksiId: Int,
    val noFaktur: String,
    val totalHarga: Double
)

sealed class CheckoutError(message: String) : Exception(message) {
    data object EmptyCart : CheckoutError("Keranjang kosong")
    data object InvalidQty : CheckoutError("Qty item tidak valid")
    data class InsufficientStock(
        val materialId: Int,
        val nama: String,
        val stokSaat: Int,
        val requested: Int
    ) : CheckoutError("Stok tidak cukup untuk $nama: tersedia $stokSaat, diminta $requested")
    data class MaterialNotFound(val materialId: Int) :
        CheckoutError("Material id=$materialId tidak ditemukan")
}
