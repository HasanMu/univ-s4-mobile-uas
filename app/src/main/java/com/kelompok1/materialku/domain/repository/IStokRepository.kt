package com.kelompok1.materialku.domain.repository

import com.kelompok1.materialku.domain.model.JenisStok
import com.kelompok1.materialku.domain.model.StokLog
import kotlinx.coroutines.flow.Flow

interface IStokRepository {
    fun observeLog(): Flow<List<StokLog>>
    fun observeLogByMaterial(materialId: Int): Flow<List<StokLog>>

    /**
     * Catat mutasi stok secara atomik.
     *
     * MASUK menambah stokSaat, KELUAR mengurangi. Guard: KELUAR ditolak
     * kalau qty > stokSaat (stok tidak boleh negatif).
     *
     * @return Result.success dengan stok baru, atau Result.failure dengan
     *         MutasiError kalau ada kendala validasi/data.
     */
    suspend fun catatMutasi(
        materialId: Int,
        jenis: JenisStok,
        qty: Int,
        keterangan: String,
        userId: Int
    ): Result<Int>
}

sealed class MutasiError(message: String) : Exception(message) {
    data object MaterialNotFound : MutasiError("Material tidak ditemukan")
    data object InvalidQty : MutasiError("Qty harus lebih dari 0")
    data class InsufficientStock(val stokSaat: Int, val requested: Int) :
        MutasiError("Stok tidak cukup: tersedia $stokSaat, diminta $requested")
}
