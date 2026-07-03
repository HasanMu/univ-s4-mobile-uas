package com.kelompok1.materialku.domain.model

data class Supplier(
    val id: Int = 0,
    val nama: String,
    val kontak: String = "",
    val alamat: String = "",
    val aktif: Boolean = true
)
