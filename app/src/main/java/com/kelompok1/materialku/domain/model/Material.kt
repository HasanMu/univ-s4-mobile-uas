package com.kelompok1.materialku.domain.model

data class Material(
    val id: Int = 0,
    val kode: String,
    val nama: String,
    val hargaJual: Double,
    val stokSaat: Int,
    val stokMin: Int,
    val kategoriId: Int,
    val satuanId: Int,
    val supplierId: Int? = null
) {
    fun isStokKritis(): Boolean = stokSaat <= stokMin
}
