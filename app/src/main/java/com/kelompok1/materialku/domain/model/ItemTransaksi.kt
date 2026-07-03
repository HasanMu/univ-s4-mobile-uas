package com.kelompok1.materialku.domain.model

data class ItemTransaksi(
    val id: Int = 0,
    val transaksiId: Int,
    val materialId: Int,
    val qty: Int,
    val hargaSatuan: Double,
    val subtotal: Double
)
